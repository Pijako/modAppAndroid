package fr.thibaultpichel.drone;

import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbManager;
import android.os.ParcelUuid;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import java.util.UUID;
import android.os.Parcel;

import java.util.Set;

public class ConnectingClient extends AppCompatActivity {

    public static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    private static final int REQUEST_ENABLE_BT = 1;
    private final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    // Create a BroadcastReceiver for ACTION_FOUND.
    /*
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
    };*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connecting_client);
        boolean enabled = true;

        //-->Connexion Bluetooth
        if (mBluetoothAdapter == null) {
            Log.d("Client", "Device does not support BT");
        }
        else{
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                Log.d("Client", "Please activate BT");

                /*
                Intent discoverableIntent =
                        new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
                startActivity(discoverableIntent);*/
            }
            else{
                Log.d("Client", "Enabling BT Success");

            }

            /*
            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
            if(!enabled){
                IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                registerReceiver(mReceiver, filter);


                do {

                    pairedDevices = mBluetoothAdapter.getBondedDevices();

                }while(pairedDevices.size() == 0);
            }*/

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
                        ConnectThreadClient connectThreadClient = new ConnectThreadClient(device);
                        connectThreadClient.run(); //On se connecte
                        found = true;
                    }
                }
                if(!found){
                    Log.d("Client", "Server not found");
                }
            }
            /*
            if (pairedDevices.size() > 0) {
                // There are paired devices. Get the name and address of each paired device.
                for (BluetoothDevice device : pairedDevices) {

                    Log.d("Client - Device Name",  device.getName());

                    for(ParcelUuid u : device.getUuids()){
                        ConnectThreadClient connectThreadClient = new ConnectThreadClient(device);
                        Log.d("Client - Device Name",  device.getName());
                        connectThreadClient.run(); //On se connecte

                        if(u.getUuid() == UUID.fromString("0d046699-661a-4e27-adf9-fec2ae1f352a")){
                            ConnectThreadClient connectThreadClient = new ConnectThreadClient(device);
                            Log.d("Device Name",  device.getName());
                            connectThreadClient.run(); //On se connecte
                            found = true;
                        }
                    }
                }*/
                /*if(!found){
                    // Register for broadcasts when a device is discovered.
                    IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                    registerReceiver(mReceiver, filter);
                }
                else{
                    Log.d("Pairing Device found", "No Broadcast needed");
                }*/
            }
        }









    /*
        PendingIntent mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        UsbManager usbMan = (UsbManager) getSystemService(Context.USB_SERVICE);
        //Creation du service qui intercepte la réponse de l’accessoire
        Intent receiverIntent = registerReceiver(UsbBroadcastReceiver.getInstance(usbMan), filter);
        UsbAccessory[] accessoryList = usbMan.getAccessoryList();
        if (accessoryList == null) {
            //Pas d’accessoire USB connecté
            Toast.makeText(this.getApplicationContext(), "No connected drone", Toast.LENGTH_LONG).show();
        } else {
            //On demande la connection
            usbMan.requestPermission(accessoryList[0], mPermissionIntent);
            Toast.makeText(this.getApplicationContext(), "Drone found", Toast.LENGTH_LONG).show();
            Intent playIntent = new Intent(this, DroneControl.class);
            startActivity(playIntent);
        }*/
    }

    /*
    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Don't forget to unregister the ACTION_FOUND receiver.
        unregisterReceiver(mReceiver);
    }*/
