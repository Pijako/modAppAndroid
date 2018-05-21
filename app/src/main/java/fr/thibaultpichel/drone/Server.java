package fr.thibaultpichel.drone;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelUuid;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

public class Server extends AppCompatActivity {

    public static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    private static final int REQUEST_ENABLE_BT = 1;
    private final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private Handler handler;
    private Set<BluetoothDevice> pairedDevices;
    private Intent enableBtIntent, discoverableIntent;
    private IntentFilter filter;

    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);
        Log.d("Server", "Activité lancée");

        this.handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                ((TextView) findViewById(R.id.textView8)).setText("Réception Commandes");
                ((TextView) findViewById(R.id.textView7)).setText(msg.obj.toString());
                sendByUsb(msg.obj.toString());
            }
        };

        // On vérifie que le BT est supporté
        if (mBluetoothAdapter == null) {
            Log.d("Server", "Device does not support BT");
        } else {
            // On vérifie que le BT est activé
            if (!mBluetoothAdapter.isEnabled()) {
                Log.d("Server", "Please activate BT");

                this.enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);

                this.discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
                startActivity(discoverableIntent);

                this.filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                registerReceiver(mReceiver, filter);

            } else {
                Log.d("Server", "Enabling BT Success");
            }

            this.pairedDevices = mBluetoothAdapter.getBondedDevices();

            if (pairedDevices.size() > 0) {
                // There are paired devices. Get the name and address of each paired device.
                for (BluetoothDevice device : pairedDevices) {

                    Log.d("Server - Device Name", device.getName());

                    if (device.getAddress().toLowerCase().equals("ec:88:92:35:0d:29")) {
                        AcceptThreadServeur acceptThreadServeur = new AcceptThreadServeur(this.handler);
                        acceptThreadServeur.start();
                    }

                }
            }

        }
    }

    public void sendByUsb(String msg) {
        UsbBroadcastReceiver usbBroadcastReceiver = UsbBroadcastReceiver.getInstance();
        Log.d("USB sending", msg);
        if (msg.equals("takeOff")) {

            try {
                usbBroadcastReceiver.sendToAccessory("Adjust");
                usbBroadcastReceiver.sendToAccessory("Adjust");
                usbBroadcastReceiver.sendToAccessory("Adjust");
                usbBroadcastReceiver.sendToAccessory("Adjust");
                usbBroadcastReceiver.sendToAccessory("Adjust");

            } catch (IOException e) {
                e.printStackTrace();
            }


        } else if (msg.equals("Forward")) {
            try {
                usbBroadcastReceiver.sendToAccessory("CalibrateMagneto");
                usbBroadcastReceiver.sendToAccessory("CalibrateMagneto");
                usbBroadcastReceiver.sendToAccessory("CalibrateMagneto");
                usbBroadcastReceiver.sendToAccessory("CalibrateMagneto");
                usbBroadcastReceiver.sendToAccessory("CalibrateMagneto");
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        try {
            usbBroadcastReceiver.sendToAccessory(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
