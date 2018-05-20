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

    public void run() {
        Log.d("RUN", "0");
        BluetoothSocket socket = null;
        // Keep listening until exception occurs or a socket is returned.
        while (true) {
            try {
                socket = mmServerSocket.accept();
                Log.d("RUN", "1");
            } catch (IOException e) {
                Log.e(TAG, "ATS - Socket's accept() method failed", e);
                break;
            }

            if (socket != null) {
                // A connection was accepted. Perform work associated with
                // the connection in a separate thread.
                Log.d("RUN", "2");
                manageMyConnectedSocket(socket);
                try {
                    mmServerSocket.close();
                    Log.d("RUN", "4");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    private void manageMyConnectedSocket(BluetoothSocket socket) {
        //Lire/écrire les messages
        Log.d("Server", "manageMyConnectedSocket début");
        myBluetoothService = new MyBluetoothService(socket);

        Log.d("Server", "Wait for message");
        this.myBluetoothService.getRunCT();

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
