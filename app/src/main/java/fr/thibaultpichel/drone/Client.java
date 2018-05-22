package fr.thibaultpichel.drone;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.util.Set;

public class Client extends AppCompatActivity {

    public static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    private static final int REQUEST_ENABLE_BT = 1;
    private final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private Handler handler, periodHandler;
    private MyBluetoothService myBluetoothService;
    private MesureThreadClient mesureThreadClient;
    private BluetoothSocket mmSocket;
    private final int INTERVAL_SEND = 300;
    private Context context;
    private WifiManager wifiMan;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);
        boolean enabled = true;

        this.handler = new Handler();
        this.periodHandler = new Handler();

        this.wifiMan = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        // On vérifie que le BT est supporté
        if (mBluetoothAdapter == null) {
            Log.d("Client", "Device does not support BT");
        }
        else{ // On vérifie que le BT est activé
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                Log.d("Client", "Please enable BT");
            }
            else{
                Log.d("Client", "Enabling BT Success");

            }
            //On cherche l'autre téléphone parmis les appareils déjà appareillés
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
                        found = true;

                        ConnectClientTask connectClientTask = new ConnectClientTask(device, handler){
                            @Override
                            protected void onPostExecute(MyBluetoothService mbs) {
                                myBluetoothService = mbs;

                                setContentView(R.layout.content_drone_control);
                            }
                        };
                        connectClientTask.execute(); //On lance l'AsyncTask de connexion


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

        if(v.getId()== R.id.b_followme) { //bouton followme


            Log.d("Client", "sending commands");
            String cmd = (String) b_clique.getContentDescription();
            this.myBluetoothService.sendCommand(cmd);

            //client ecoute une requete du serveur

            Log.d("Client", "listening for request");
            this.mmSocket = this.myBluetoothService.getSocket();

            mesureThreadClient = new MesureThreadClient(this.mmSocket, wifiMan);


            Log.d("Client", "listening for request in loop");
            this.mesureThreadClient.startConnectedThread();

            //this.periodHandler.postDelayed(mesureThreadClient.getThread(), INTERVAL_SEND);

        }
        else {
            Log.d("Client", "sending commands");
            String cmd = (String) b_clique.getContentDescription();
            this.myBluetoothService.sendCommand(cmd);
        }



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