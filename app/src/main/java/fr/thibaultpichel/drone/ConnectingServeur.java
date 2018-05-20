package fr.thibaultpichel.drone;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.ParcelUuid;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.Set;
import java.util.UUID;

public class ConnectingServeur extends AppCompatActivity {

    private final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private static final int REQUEST_ENABLE_BT = 1;

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
        setContentView(R.layout.activity_connecting_serveur);
        Log.d("Server", "Activité lancée");
        boolean enabled = true;

        //-->Connexion Bluetooth
        if (mBluetoothAdapter == null) {
            Log.d("Server", "Device does not support BT");
        } else {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                Log.d("Server", "Please activate BT");
                enabled = false;

                Intent discoverableIntent =
                        new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
                startActivity(discoverableIntent);

            } else {
                Log.d("Server", "Enabling BT Success");

            }

            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

            if(!enabled){
                IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                registerReceiver(mReceiver, filter);

                do {

                    pairedDevices = mBluetoothAdapter.getBondedDevices();

                }while(pairedDevices.size() == 0);
            }

            if (pairedDevices.size() > 0) {
                // There are paired devices. Get the name and address of each paired device.
                for (BluetoothDevice device : pairedDevices) {

                    Log.d("Server - Device Name", device.getName());

                    AcceptThreadServeur acceptThreadServeur = new AcceptThreadServeur();
                    acceptThreadServeur.run();

                    /*for (ParcelUuid u : device.getUuids()) {
                        if (u.getUuid() == UUID.fromString("0d046699-661a-4e27-adf9-fec2ae1f352a")) {
                            Log.d("Device Name", device.getName());
                            AcceptThreadServeur acceptThreadServeur = new AcceptThreadServeur();
                            acceptThreadServeur.run();
                            found = true;
                        }
                    }*/
                }
                /*if (!found) {
                    // Register for broadcasts when a device is discovered.
                    IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                    registerReceiver(mReceiver, filter);
                } else {
                    Log.d("Pairing Device found", "No Broadcast needed");
                }*/

            }

        }
    }
}
