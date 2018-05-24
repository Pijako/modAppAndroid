package fr.thibaultpichel.drone;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

import static android.content.ContentValues.TAG;

/**
 * Created by tpichel & jessking on 18/05/18.
 * Classe Task héritant de AsyncTask
 */

public class ConnectServerTask extends AsyncTask<Void, Void, MyBluetoothService> {
    private final BluetoothServerSocket mmServerSocket;
    private final BluetoothAdapter mBluetoothAdapter;
    private Handler handler;

    public ConnectServerTask(Handler h) {
        // Use a temporary object that is later assigned to mmServerSocket
        // because mmServerSocket is final.
        this.handler = h;
        BluetoothServerSocket tmp = null;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Log.d("ATS", "Got a BT adapter");
        UUID MY_UUID = UUID.fromString("0d046699-661a-4e27-adf9-fec2ae1f352a");
        try {
            tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord("DroneRemote", MY_UUID);
            Log.d("ATS - TMP", "tmp OK");
        } catch (IOException e) {
            Log.e(TAG, "ATS - Socket's listen() method failed", e);
        }
        mmServerSocket = tmp;
        Log.d("ATS - mServerSocket", mmServerSocket.toString());
    }

    protected MyBluetoothService doInBackground(Void... values) {
        Log.d("dIB", "0");
        BluetoothSocket socket = null;
        // Keep listening until exception occurs or a socket is returned.
        while (true) {
            try {
                socket = mmServerSocket.accept();
                Log.d("dIB", "1");
            } catch (IOException e) {
                Log.e(TAG, "ATS - Socket's accept() method failed", e);
                break;
            }

            if (socket != null) {
                // A connection was accepted. Perform work associated with
                // the connection in a separate thread.
                Log.d("dIB", "2");
                //manageMyConnectedSocket(socket);
                try {
                    mmServerSocket.close();
                    Log.d("dIB", "3");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
        /*Ici, éventuelle utilisation de manageMyConnectedSocket() pour tester la connexion
         *
         * Puis, on retourne un objet de type MyBluetoothService créé grâce au socket de connexion
         * ainsi qu'un handler fourni et contenant le thread qui permettra au client d'échanger des
         * messages avec le serveur
         */
        return new MyBluetoothService(socket, this.handler);
    }

    //Fonction pour gérer la connexion
    private void manageMyConnectedSocket(BluetoothSocket socket) {
        //Lire ou écrire des messages pour tester la connexion

    }

    /* Fonction pour fermer le socket client et terminer le thread.
    *  (Inutilisée, l'AsyncTask se termine seule)
    */
    public void cancel() {
        try {
            mmServerSocket.close();
        } catch (IOException e) {
            Log.e(TAG, "Could not close the connect socket", e);
        }
    }
}
