package fr.thibaultpichel.drone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbManager;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import static fr.thibaultpichel.drone.DroneConnection.ACTION_USB_PERMISSION;

/**
 * Created by tpichel & jessking on 14/05/18.
 * Classe qui hérite de BroadcastRceiver, et qui permet d'utiliser la connexion USB
 * pour injecter les commandes au drone.
 */

public class UsbBroadcastReceiver extends BroadcastReceiver {

    static UsbManager usbMan;
    private static int compteur = 0;
    static UsbBroadcastReceiver receiver;
    private ParcelFileDescriptor mFileDescriptor;
    private FileDescriptor fd;
    private FileOutputStream mOutputStream;
    private Context appContext;

    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
         this.appContext = context;

        if (action.equals(ACTION_USB_PERMISSION)) {
            //Le drone a accepte la connexion USB
            synchronized (this) {
                UsbAccessory mAccessory = intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);

                //Se souvient de l'accessory
                if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                    Log.d("USB", "extra perimssion granted");
                    if (mAccessory != null) {
                        Log.d("USB", "mAccessory OK non null");
                        mFileDescriptor = usbMan.openAccessory(mAccessory);
                        fd = mFileDescriptor.getFileDescriptor();
                        mOutputStream = new FileOutputStream(fd);
                    }
                    //Sinon
                } else {
                    Log.d("USB onReceive", "permission denied for accessory " + mAccessory);
                }
            }
        }
    }

    //Récupération d'une instance de classe nécessaire lors de l'appel de méthodes
    public static UsbBroadcastReceiver getInstance(UsbManager um) {

        //premiere fois on crée la class
        if (compteur == 0) {
            receiver = new UsbBroadcastReceiver();
            compteur++;
        }
        usbMan = um;

        return receiver;
    }

    //Envoie de la commande
    public void sendToAccessory(final String message) throws IOException {
        Log.d("Server", "STA");
        if (mFileDescriptor != null) {
            new Thread(new Runnable() {
                public void run() {
                    try
                    {
                        Log.d("USB", "Writing data: " + message);

                        mOutputStream.write(message.getBytes());
                    }
                    catch
                            (IOException e) {e.printStackTrace();}
                }
            }).start();
        }
        else{
            Log.d("Pb USB", "FD = null");
        }
    }

    public static UsbManager getUsbMan(){
        return UsbBroadcastReceiver.usbMan;
    }
}
