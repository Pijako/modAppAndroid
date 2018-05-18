package fr.thibaultpichel.drone;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

import static android.content.ContentValues.TAG;

/**
 * Created by tpichel on 16/05/18.
 */

public class AcceptThreadServeur extends Thread {
    private final BluetoothServerSocket mmServerSocket;
    private final BluetoothAdapter mBluetoothAdapter;
    private MyBluetoothService myBluetoothService;

    public AcceptThreadServeur() {
        // Use a temporary object that is later assigned to mmServerSocket
        // because mmServerSocket is final.
        BluetoothServerSocket tmp = null;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Log.d("AcceptThreadServer", "got a BT adapter");
        UUID MY_UUID = UUID.fromString("0d046699-661a-4e27-adf9-fec2ae1f352a");
        try {
            tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord("DroneRemote", MY_UUID);
            Log.d("TMP", "tmp OK");
        } catch (IOException e) {
            Log.e(TAG, "Socket's listen() method failed", e);
        }
        mmServerSocket = tmp;
        Log.d("mServerSocket", mmServerSocket.toString());
    }

    public void run() {
        Log.d("RUN", "0");
        BluetoothSocket socket = null;
        // Keep listening until exception occurs or a socket is returned.
        while (true) {
            try {
                socket = mmServerSocket.accept();
                Log.d("RUN", "1");
            } catch (IOException e) {
                Log.e(TAG, "Socket's accept() method failed", e);
                break;
            }

            if (socket != null) {
                // A connection was accepted. Perform work associated with
                // the connection in a separate thread.
                Log.d("RUN", "2");
                manageMyConnectedSocket(socket);
                cancel(); //fermer le socket
                try {
                    mmServerSocket.close();
                    Log.d("RUN", "3");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    private void manageMyConnectedSocket(BluetoothSocket socket) {
        //Lire/écrire les messages
      /*  Log.d("RUN1", "4");
        myBluetoothService = new MyBluetoothService(socket);
        this.myBluetoothService.getRunCT();
        byte msg[]= new byte[50];
        String s = "test message";
        msg = s.getBytes();
        this.myBluetoothService.getWriteCT(msg);*/

    }

    // Closes the connect socket and causes the thread to finish.
    public void cancel() {
        try {
            mmServerSocket.close();
        } catch (IOException e) {
            Log.e(TAG, "Could not close the connect socket", e);
        }
    }
}
