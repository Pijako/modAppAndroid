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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import java.io.IOException;

import static fr.thibaultpichel.drone.DroneConnection.ACTION_USB_PERMISSION;

public class DroneControl extends AppCompatActivity {

    //UsbManager usbMan = (UsbManager) getSystemService(Context.USB_SERVICE);
    IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drone_control);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

    public void onClick(View v) throws IOException {
        Button b_clique = (Button) findViewById(v.getId());
        UsbBroadcastReceiver usbBroadcastReceiver = UsbBroadcastReceiver.getInstance();

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

        usbBroadcastReceiver.sendToAccessory((String) b_clique.getContentDescription());//sendToAccessory est une méthode d'instance, il faut avoir une instance
        //Log.d("TEST","TEST : "+(String) b_clique.getContentDescription());
    }

}
