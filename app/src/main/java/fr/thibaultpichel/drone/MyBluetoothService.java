package fr.thibaultpichel.drone;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * Created by tpichel on 18/05/18.
 */

public class MyBluetoothService {
    private static final String TAG = "MY_APP_DEBUG_TAG";
    private Handler mHandler; // handler that gets info from Bluetooth service
    private BluetoothSocket socket;
    private ConnectedThread connectedThread;

    public Handler getHandler(){
        return this.mHandler;
    }

    // Defines several constants used when transmitting messages between the
    // service and the UI.

    public MyBluetoothService(BluetoothSocket socket, Handler h){
        this.socket = socket;
        this.connectedThread = new ConnectedThread(socket);
        this.mHandler = h;
    }

    public MyBluetoothService(BluetoothSocket socket){
        this.socket = socket;
        this.connectedThread = new ConnectedThread(socket);
    }

    public BluetoothSocket getSocket() {
        return socket;
    }

    private interface MessageConstants {
        public static final int MESSAGE_READ = 0;
        public static final int MESSAGE_WRITE = 1;
        public static final int MESSAGE_TOAST = 2;

        // ... (Add other message types here as needed.)
    }

    //lancer le thread ConnectedThread
    public void startConnectedThread(){
        this.connectedThread.start();
    }


    public void sendCommand(String message){
        this.connectedThread.write(message);
        Log.d("Client send Command", message);

    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private InputStream mmInStream = null;
        private OutputStream mmOutStream = null;
        private byte[] mmBuffer; // mmBuffer store for the stream
        private BufferedReader reader;
        PrintWriter writer;

        public ConnectedThread(BluetoothSocket socket) {
            this.mmSocket = socket;

            // Get the input and output streams
            try {
                mmInStream = mmSocket.getInputStream();
                mmOutStream = mmSocket.getOutputStream();
                reader = new BufferedReader(new InputStreamReader(mmInStream));
                writer = new PrintWriter(mmOutStream, true);
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when creating input/output stream", e);
            }

        }

        public void run() {
            //mmBuffer = new byte[1024];
            //int numBytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs.
            Log.d("Server - Run", "début attente message");
            while (true) {
                try {
                    //On lit le message reçu
                    String receivedMessage = reader.readLine();
                    Log.d("Server - Rcv Message", receivedMessage);

                    //On l'envoie à l'activité Server via le Handler
                    Message msgForA = new Message();
                    msgForA.obj = receivedMessage;
                    MyBluetoothService.this.mHandler.sendMessage(msgForA);

                } catch (IOException e) {
                    Log.d(TAG, "Input stream was disconnected", e);
                    break;
                }
            }
        }

        // Call this from the main activity to send data to the remote device.
        public void write(String message) {
            //On écrit dans le writer
            writer.println(message);
        }

        public void cancel() {
            try {

                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the connect socket", e);
            }
        }
    }
}
