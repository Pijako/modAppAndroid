package fr.thibaultpichel.drone;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.IOException;
import java.util.Set;

public class ConnectingClient extends AppCompatActivity {

    public static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    private static final int REQUEST_ENABLE_BT = 1;
    private final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private Handler handler;
    private MyBluetoothService myBluetoothService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connecting_client);
        boolean enabled = true;
        this.handler = new Handler();

        //-->Connexion Bluetooth
        if (mBluetoothAdapter == null) {
            Log.d("Client", "Device does not support BT");
        }
        else{
            if (!mBluetoothAdapter.isEnabled()) {
                Log.d("Client", "Please enable BT");
            }
            else{
                Log.d("Client", "Enabling BT Success");

            }

            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
            boolean found = false;
            if (pairedDevices.size() > 0) {
                // There are paired devices. Get the name and address of each paired device.
                for (BluetoothDevice device : pairedDevices) {
                    String deviceName = device.getName();
                    Log.d("Client - Device Name", device.getName());
                    String deviceHardwareAddress = device.getAddress(); // MAC address
                    Log.d("Client - Device Adress", device.getAddress());
                    if (device.getAddress().toLowerCase().equals("9c:d9:17:4a:93:ba")){

                        ConnectClientTask connectClientTask = new ConnectClientTask(device, handler){
                            @Override
                            protected void onPostExecute(MyBluetoothService mbs) {
                                myBluetoothService = mbs;
                            }
                        };

                        connectClientTask.execute(); //On se connecte
                        found = true;

                        setContentView(R.layout.activity_drone_control);
                        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
                        //setSupportActionBar(toolbar);

                        Log.d("Client", "You can send commands");
                    }
                }
                if(!found){
                    Log.d("Client", "Server not found");
                }
            }
            }
        }

    public void onClick(View v) throws IOException {
        Button b_clique = (Button) findViewById(v.getId());
        String cmd = (String) b_clique.getContentDescription();
        this.myBluetoothService.sendCommand(cmd);

        /*UsbBroadcastReceiver usbBroadcastReceiver = UsbBroadcastReceiver.getInstance(UsbBroadcastReceiver.getUsbMan());

        if(b_clique.getContentDescription().equals("takeOff")){

            usbBroadcastReceiver.sendToAccessory("Adjust");
            usbBroadcastReceiver.sendToAccessory("Adjust");
            usbBroadcastReceiver.sendToAccessory("Adjust");
            usbBroadcastReceiver.sendToAccessory("Adjust");
            usbBroadcastReceiver.sendToAccessory("Adjust");

        }
        else if(b_clique.getContentDescription().equals("Forward")){
            usbBroadcastReceiver.sendToAccessory("CalibrateMagneto");
            usbBroadcastReceiver.sendToAccessory("CalibrateMagneto");
            usbBroadcastReceiver.sendToAccessory("CalibrateMagneto");
            usbBroadcastReceiver.sendToAccessory("CalibrateMagneto");
            usbBroadcastReceiver.sendToAccessory("CalibrateMagneto");

        }
        */
        //usbBroadcastReceiver.sendToAccessory((String) b_clique.getContentDescription());//sendToAccessory est une méthode d'instance, il faut avoir une instance
        //Log.d("TEST",(String) b_clique.getContentDescription());
    }

}