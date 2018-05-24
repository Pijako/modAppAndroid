package fr.thibaultpichel.drone;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import java.io.IOException;
import java.util.Set;

/**
 * Created by tpichel & jessking on 18/05/18.
 * Classe Client héritant d'une activité
 */

public class Client extends AppCompatActivity {

    //Déclaration des paramètres de la classe
    public static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    private static final int REQUEST_ENABLE_BT = 1;
    private final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private Handler handler, periodHandler;
    private MyBluetoothService myBluetoothService;
    private MesureThreadClient mesureThreadClient;
    private BluetoothSocket mmSocket;
    private final int INTERVAL_SEND = 900;
    private Context context;
    private WifiManager wifiMan;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);
        boolean enabled = true;

        //On définit le Handler dont on peut se servir pour remonter des message d'un thread vers l'activité
        this.handler = new Handler();
        this.wifiMan = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        // On vérifie que le Bluetooth est supporté
        if (mBluetoothAdapter == null) {
            Log.d("Client", "Device does not support BT");
        }
        else{ // On vérifie que le Bluetooth est activé
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

                // On les parcours afin de trouver leur nom et leur adresse
                for (BluetoothDevice device : pairedDevices) {
                    String deviceName = device.getName();
                    Log.d("Client - Device Name", device.getName());
                    String deviceHardwareAddress = device.getAddress(); // MAC address
                    Log.d("Client - Device Adress", device.getAddress());

                    //Si l'adresse correspond à celle du téléphone Server
                    if (device.getAddress().toLowerCase().equals("9c:d9:17:4a:93:ba")){
                        found = true;

                        //On créé une Async afin de lancer la connexion Bluetooth
                        ConnectClientTask connectClientTask = new ConnectClientTask(device, handler){
                            //On override la fonction onPostExecute qui se lancera à la fin de l'AsyncTask de connexion
                            @Override
                            protected void onPostExecute(MyBluetoothService mbs) {
                                //On récupère l'objet de type MyBluetoothService pour commencer les échanges de messages
                                myBluetoothService = mbs;
                                //On affiche les boutons de contrôle du drone sur le client
                                setContentView(R.layout.content_drone_control);
                            }
                        };
                        //On lance l'AsyncTask de connexion
                        connectClientTask.execute();


                        Log.d("Client", "You can send commands");
                    }
                }
                if(!found){
                    Log.d("Client", "Server not found");
                }
            }
        }
    }

    //Listener qui récupère le bouton cliqué
    public void onClick(View v) {
        Button b_clique = findViewById(v.getId());

        if(v.getId()== R.id.b_followme) { //Si Bouton followme

            Log.d("Client", "sending commands");
            String cmd = (String) b_clique.getContentDescription();
            this.myBluetoothService.sendCommand(cmd);

            //client ecoute une requete du serveur

            Log.d("Client", "listening for request");
            this.mmSocket = this.myBluetoothService.getSocket();

            //On créé un nouveau thread de pour échanger les messages de type requête/réponse pour les mesures de distance
            this.mesureThreadClient = new MesureThreadClient(this.mmSocket, wifiMan);

            //On lance le thread
            Log.d("Client", "listening for request in loop");
            this.mesureThreadClient.startConnectedThread();

        }
        else { //Sinon, il s'agit d'un bouton de contrôle
            Log.d("Client", "sending commands");
            String cmd = (String) b_clique.getContentDescription();

            //On lance la commande
            this.myBluetoothService.sendCommand(cmd);
        }

    }

}