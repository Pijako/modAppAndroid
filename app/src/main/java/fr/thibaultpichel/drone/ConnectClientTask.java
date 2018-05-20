package fr.thibaultpichel.drone;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.util.UUID;

import static android.content.ContentValues.TAG;

/**
 * Created by tpichel on 18/05/18.
 */

public class ConnectClientTask extends AsyncTask<Void, Void, MyBluetoothService> {
    private final BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;
    private final BluetoothAdapter mBluetoothAdapter;
    private MyBluetoothService myBluetoothService;
    private Handler handler;
    private Handler controlHandler;

    public ConnectClientTask(BluetoothDevice device, Handler h) {
        // Use a temporary object that is later assigned to mmSocket
        // because mmSocket is final.
        BluetoothSocket tmp = null;
        this.handler = h;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mmDevice = device;
        Log.d("CTC - mclientSocket","avant try");
        try {
            // Get a BluetoothSocket to connect with the given BluetoothDevice.
            // MY_UUID is the app's UUID string, also used in the server code.
            UUID MY_UUID = UUID.fromString("0d046699-661a-4e27-adf9-fec2ae1f352a");
            tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            Log.d("CTC - mclientSocket", tmp.toString());
        } catch (IOException e) {
            Log.e(TAG, "CTC - Socket's create() method failed", e);
        }
        mmSocket = tmp;
        Log.d("CTC - mclientSocket", mmSocket.toString());
    }

    protected MyBluetoothService doInBackground(Void... values) {
        // Cancel discovery because it otherwise slows down the connection.
        mBluetoothAdapter.cancelDiscovery();

        try {
            // Connect to the remote device through the socket. This call blocks
            // until it succeeds or throws an exception.
            Log.d("CTC - mclientSocket", "run - avantconnect");
            mmSocket.connect();


        } catch (IOException connectException) {
            // Unable to connect; close the socket and return.
            try {
                mmSocket.close();
            } catch (IOException closeException) {
                Log.e(TAG, "CTC - Could not close the client socket", closeException);
            }
            return null;
        }

        // The connection attempt succeeded. Perform work associated with
        // the connection in a separate thread.
        manageMyConnectedSocket(this.mmSocket); //Ne sert plus à rien
        return new MyBluetoothService(mmSocket, this.handler);
        //cancel(); //fermer le socket
    }

    private void manageMyConnectedSocket(BluetoothSocket socket) {
        Log.d("Client", "manageMyConnectedSocket début");
        //Lire/écrire les messages pour tester la connexion
    }

    // Closes the client socket and causes the thread to finish.
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
            Log.e(TAG, "CTC - Could not close the client socket", e);
        }
    }
}