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
 * Created by tpichel & jessking on 18/05/18. Sur le modèle de MyBluetoothService
 *
 * Classe qui permet d'écrire et de lire dans le socket Bluetooth d'échange de messages
 * via un thread lancé sur chaque téléphone.
 * Le thread est manipulé par l'intermédiaire de cette classe.
 *
 * Elle est différente côté client et côté serveur.
 */

public class MesureThreadServer {
    private static final String TAG = "MY_APP_DEBUG_TAG";
    private Handler mHandler; // handler that gets info from Bluetooth service
    private BluetoothSocket socket;
    private ConnectedThread connectedThread;
    // Defines several constants used when transmitting messages between the
    // service and the UI.

    public MesureThreadServer(BluetoothSocket socket, Handler h){
        this.socket = socket;
        this.connectedThread = new ConnectedThread(socket);
        this.mHandler = h;
    }

    public BluetoothSocket getSocket() {
        return socket;
    }

    public void startConnectedThread(){
        this.connectedThread.start();
    }

    public void sendCommand(String message){
        this.connectedThread.write(message);
        Log.d("Serveur send Command", message);

    }

    public ConnectedThread getThread(){
        return this.connectedThread;
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private InputStream mmInStream = null;
        private OutputStream mmOutStream = null;
        private byte[] mmBuffer; // mmBuffer store for the stream
        private BufferedReader reader;
        PrintWriter writer;
        private final int INTERVAL_SEND = 500;

        public ConnectedThread(BluetoothSocket socket) {
            this.mmSocket = socket;

            // Get the input and output streams; using temp objects because
            // member streams are final.
            try {
                mmInStream = mmSocket.getInputStream();
                mmOutStream = mmSocket.getOutputStream();
                reader = new BufferedReader(new InputStreamReader(mmInStream));
                writer = new PrintWriter(mmOutStream, true);
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when creating input/output stream", e);
            }

        }

        //Méthode run() (runnable) propre à la mesure période côté server
        public void run() {
            //mmBuffer = new byte[1024];
            //int numBytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs.

            //while(true){
                //Si le currentTimeMillis est un multiple de 1000ms = 1s
                //if(System.currentTimeMillis()%INTERVAL_SEND == 0) {

                    Log.d("Server - Run", "début envoi d'un message et puis attente la reponse du client");
                    write("distance?");

                    //attente d'une reponse du client
                    while (true) {
                        try {

                            String receivedMessage = reader.readLine();
                            //recevoir la reponse du client
                            Log.d("Server - Rcv Message", receivedMessage);
                            Message msgForA = new Message();
                            msgForA.obj = receivedMessage;
                            //message contenant la distance recu
                            //lancement de l'algo pour suivre le client
                            //MesureThreadServer.this.mHandler.sendMessage(msgForA);

                        } catch (IOException e) {
                            Log.d(TAG, "Input stream was disconnected", e);
                            break;
                        }
                    }
                        //envoi périodique
                       // mHandler.postDelayed(this, INTERVAL_SEND);
                //}
            //}
        }

        // Call this from the main activity to send data to the remote device.
        public void write(String message) {
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
