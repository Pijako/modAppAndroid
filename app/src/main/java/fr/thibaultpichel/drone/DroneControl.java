package fr.thibaultpichel.drone;

import android.content.Context;
import android.content.IntentFilter;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import java.io.IOException;
import static fr.thibaultpichel.drone.DroneConnection.ACTION_USB_PERMISSION;

/** DEPRECATED CLASS
 * Created by tpichel & jessking on 18/05/18.
 * Classe DroneControl héritant d'une activité
 * Classe OBSOLÈTE utilisée avant ka connexion Bluetooth pour piloter le drone par USB filèrement
 */

public class DroneControl extends AppCompatActivity {

    UsbManager usbMan = (UsbManager) getSystemService(Context.USB_SERVICE);
    IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drone_control);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

    //Listener qui récupère le bouton cliqué
    public void onClick(View v) throws IOException {
        Button b_clique = (Button) findViewById(v.getId());
        UsbBroadcastReceiver usbBroadcastReceiver = UsbBroadcastReceiver.getInstance(usbMan);

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
        else if(b_clique.getContentDescription().equals("followme")) {

        }

        //sendToAccessory est une méthode d'instance, il faut avoir une instance de classe
        usbBroadcastReceiver.sendToAccessory((String) b_clique.getContentDescription());
        //Log.d("TEST", (String) b_clique.getContentDescription());
    }

}
