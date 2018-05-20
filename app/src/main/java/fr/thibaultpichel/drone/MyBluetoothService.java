package fr.thibaultpichel.drone;

import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringBufferInputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;

/**
 * Created by tpichel on 18/05/18.
 */

public class MyBluetoothService {
    private static final String TAG = "MY_APP_DEBUG_TAG";
    private Handler mHandler; // handler that gets info from Bluetooth service
    private BluetoothSocket socket;
    private ConnectedThread ct;

    // Defines several constants used when transmitting messages between the
    // service and the UI.

    public MyBluetoothService(BluetoothSocket socket){
        this.socket = socket;
        this.ct = new ConnectedThread(socket);
        this.mHandler = new Handler();

    }
    private interface MessageConstants {
        public static final int MESSAGE_READ = 0;
        public static final int MESSAGE_WRITE = 1;
        public static final int MESSAGE_TOAST = 2;

        // ... (Add other message types here as needed.)
    }

    public void getRunCT(){
        this.ct.run();
    }
    public void getWriteCT(String message){
        this.ct.write(message);
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private InputStream mmInStream = null;
        private OutputStream mmOutStream = null;
        private byte[] mmBuffer; // mmBuffer store for the stream
        private BufferedReader reader;
        PrintWriter writer;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;

            // Get the input and output streams; using temp objects because
            // member streams are final.
            try {
                mmInStream = socket.getInputStream();
                mmOutStream = socket.getOutputStream();
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
                    // Read from the InputStream.
                    //numBytes = mmInStream.read(mmBuffer);
                    // Send the obtained bytes to the UI activity.
                    /*
                    Message readMsg = mHandler.obtainMessage(
                            MessageConstants.MESSAGE_READ, numBytes, -1,
                            mmBuffer);*/

                    String receivedMessage = reader.readLine();
                    Log.d("Server - Rcv Message", receivedMessage);

                    //readMsg.sendToTarget();
                } catch (IOException e) {
                    Log.d(TAG, "Input stream was disconnected", e);
                    break;
                }
            }
        }

        // Call this from the main activity to send data to the remote device.
        public void write(String message) {
            Log.d("Client - write", "début envoie");
            //try {
                /*mmOutStream.write(bytes);

                // Share the sent message with the UI activity.

                Message writtenMsg = mHandler.obtainMessage(
                        MessageConstants.MESSAGE_WRITE, -1, -1, mmBuffer);
                writtenMsg.sendToTarget();*/
                writer.println(message);/*
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when sending data", e);

                // Send a failure message back to the activity.
                Message writeErrorMsg =
                        mHandler.obtainMessage(MessageConstants.MESSAGE_TOAST);
                Bundle bundle = new Bundle();
                bundle.putString("toast",
                        "Couldn't send data to the other device");
                writeErrorMsg.setData(bundle);
                mHandler.sendMessage(writeErrorMsg);
            }*/
        }

        // Call this method from the main activity to shut down the connection.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the connect socket", e);
            }
        }
    }
}