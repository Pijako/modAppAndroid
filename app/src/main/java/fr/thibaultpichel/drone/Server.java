package fr.thibaultpichel.drone;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import java.io.IOException;
import java.util.Arrays;
import java.util.Set;

/**
 * Created by tpichel & jessking on 18/05/18.
 * Classe Server héritant d'une activité
 */

public class Server extends AppCompatActivity {

    //On définit les attributs privés
    public static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    private static final int REQUEST_ENABLE_BT = 1;
    private final int INTERVAL_SEND = 1000; //Période de mesure de distance
    private final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private Handler handler;
    private Set<BluetoothDevice> pairedDevices;
    private Intent enableBtIntent, discoverableIntent;
    private IntentFilter filter;
    private MyBluetoothService myBluetoothService;
    private MesureThreadServer mesureThreadServer;

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

        /*On créé le Handler qui permettra de faire remonter les messages depuis le thread d'échange
          de messages depuis la classe MyBluetoothService afin de les afficher sur la vue de l'activité
        */
        this.handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {

                //On définit les commandes utilisées dans l'envoi standart de commandes au drone
                String[] acceptedCommand = {"Up", "Down", "takeOff", "Forward", "Land", "Backward", "MovLeft", "MovRight", "TurnLeft",
                                            "TurnRight", "Hover", "Adjust", "MovMag", "End", "Emergency"};

                //On met à jour la vue afin d'indiquer que l'on passe en mode réception de commandes
                ((TextView) findViewById(R.id.textView8)).setText("Réception Commandes");
                ((TextView) findViewById(R.id.textView7)).setText(msg.obj.toString());

                //Si le message reçu est une commande de pilotage
                if(Arrays.toString(acceptedCommand).contains(msg.obj.toString())){
                    sendByUsb(msg.obj.toString());
                    Log.d("Server", "commande recu");
                } //Sinon si c'est une activation du "mode suiveur"
                else if (msg.obj.toString().equals("followme")){
                    Log.d("Server", "followme recu");

                    //Le serveur fait du polling au client pour demander des infos regulierement en créant un thread
                    mesureThreadServer = new MesureThreadServer(myBluetoothService.getSocket(), this);
                    //Et en le lançant
                    mesureThreadServer.startConnectedThread();

                } //Sinon si c'est une distance calculée et envoyée depuis le client
                else {
                    Log.d("Server", "Distance reçue-->modif interface");
                    ((TextView) findViewById(R.id.textView8)).setText("Following You");
                    ((TextView) findViewById(R.id.textView7)).setText("Distance Client : "+msg.obj.toString()+" m");
                    moveDrone(new Double(msg.obj.toString()));
                }
            }
        };



        // On vérifie que le Bluetooth est supporté
        if (mBluetoothAdapter == null) {
            Log.d("Server", "Device does not support BT");
        } else {
            // On vérifie que le Bluetooth est activé
            if (!mBluetoothAdapter.isEnabled()) {
                Log.d("Server", "Please activate BT");

                /*On créé et on lance dans la vue de l'activité de la vue les intents permettant
                la demande d'activation du Bluetooth et de la détectabilité du mobile par Bluetooth
                */
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
                // Il y a des terminaux appairés. On les parcourt
                for (BluetoothDevice device : pairedDevices) {

                    Log.d("Server - Device Name", device.getName());

                    //Si on tombe sur l'adresse du client
                    if (device.getAddress().toLowerCase().equals("ec:88:92:35:0d:29")) {

                        //On créé une Async afin de lancer la connexion Bluetooth
                        ConnectServerTask connectServerTask = new ConnectServerTask(this.handler){
                            @Override
                            protected void onPostExecute(MyBluetoothService mbs) {
                                //On récupère l'objet de type MyBluetoothService pour commencer les échanges de messages
                                myBluetoothService = mbs;
                                //On affiche les boutons de contrôle du drone sur le client
                                myBluetoothService.startConnectedThread();

                            }
                        };
                        //On lance l'AsyncTask de connexion
                        connectServerTask.execute();
                    }

                }
            }

        }
    }

    public void moveDrone(double distance){
    /* Fonction supposée entrer en jeu lors de l'activation du mode de suiveur et de la réception
    distances. Non faite.
     */


    }

    //Fonction envoyant les messages par USB
    public void sendByUsb(String msg) {
        UsbBroadcastReceiver usbBroadcastReceiver = UsbBroadcastReceiver.getInstance(UsbBroadcastReceiver.getUsbMan());
        Log.d("USB sending", msg);

        //Ces 2 commandes nécessite l'envoi de plusieurs autres commandes au préalable
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
            //On injecte la commande par USB au drone
            usbBroadcastReceiver.sendToAccessory(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
