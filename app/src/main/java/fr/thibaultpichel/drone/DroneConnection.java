package fr.thibaultpichel.drone;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by tpichel & jessking on 18/05/18.
 * Classe DroneConnection héritant d'une activité
 * Activité d'accueil donnant le choix par 2 boutons de passer en mode Client ou bien Serveur
 */

public class DroneConnection extends AppCompatActivity implements View.OnClickListener {

    public static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    private  UsbManager usbMan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drone_connection);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //On définit les actions sur les boutons
        Button connect_server = (Button) findViewById(R.id.button_connect_server);
        connect_server.setOnClickListener(this);
        Button connect_client = (Button) findViewById(R.id.button_connect_client);
        connect_client.setOnClickListener(this);


    }

    //Fonction listener qui récupère le bouton cliqué
    public void onClick(View v){
        //Dans la reaction au bouton
        Intent playIntent;

        switch(v.getId()) {

            //Si on choisit le mode client, l'activté correspondante est lancée
            case R.id.button_connect_client:
                playIntent = new Intent(this, Client.class);
                startActivity(playIntent);
                break;

            //Si on choisit le mode serveur, la connexion USB avec le drone est d'abord établie
            case R.id.button_connect_server:

                //On créé les intent qui permettront d'afficher l'état de la connexion au drone
                PendingIntent mPermissionIntent =
                        PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
                IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);

                this.usbMan = (UsbManager) getSystemService(Context.USB_SERVICE);

                //Creation du service qui intercepte la réponse de l’accessoire
                Intent receiverIntent = registerReceiver(UsbBroadcastReceiver.getInstance(usbMan), filter);
                UsbAccessory[] accessoryList = usbMan.getAccessoryList();

                //Si la liste d'accessoires USB est nulle
                if (accessoryList == null) {
                    //Pas d’accessoire USB connecté, on affiche une notification
                    Toast.makeText(this.getApplicationContext(), "No connected drone", Toast.LENGTH_LONG).show();
                   /* playIntent = new Intent(this, Server.class);
                    startActivity(playIntent);*/
                }
                else {
                //Sinon, on demande la connection
                    usbMan.requestPermission(accessoryList[0], mPermissionIntent);

                    //On affiche une notification
                    Toast.makeText(this.getApplicationContext(), "Drone found", Toast.LENGTH_LONG).show();
                    playIntent = new Intent(this, Server.class);
                    startActivity(playIntent);
                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_drone_connection, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
