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
 * Created by tpichel & jessking on 18/05/18.
 * Classe qui permet d'écrire et de lire dans le socket Bluetooth d'échange de messages
 * via un thread lancé sur chaque téléphone.
 * Le thread est manipulé par l'intermédiaire de cette classe.
 * Elle est identique côté client et côté serveur.
 */

public class MyBluetoothService {
    private static final String TAG = "MY_APP_DEBUG_TAG";
    private Handler mHandler; // handler that gets info from Bluetooth service
    private BluetoothSocket socket;
    private ConnectedThread connectedThread;

    //Méthode retournant le handler (Non utilisée)
    public Handler getHandler(){
        return this.mHandler;
    }

    // Defines several constants used when transmitting messages between the
    // service and the UI.

    //Méthode constructeur 1
    public MyBluetoothService(BluetoothSocket socket, Handler h){
        this.socket = socket;
        this.connectedThread = new ConnectedThread(socket);
        this.mHandler = h;
    }

    //Méthode constructeur 2 (Inutilisée) Aurait-pu l'être car côté client aucun handler utilisé
    public MyBluetoothService(BluetoothSocket socket){
        this.socket = socket;
        this.connectedThread = new ConnectedThread(socket);
    }

    //Méthode retournant le socket utilisé lors des échanges
    public BluetoothSocket getSocket() {
        return socket;
    }

    /*Méthode lançant le thread privé (runnable) inaccessible depuis l'extérieur car déclaré privé
    * et dans la classe MyBluetoothService
    */
    public void startConnectedThread(){
        this.connectedThread.start();
    }

    //Méthode qui lance une commande String en appelant la méthode write contenue dans le thread
    public void sendCommand(String message){
        this.connectedThread.write(message);
        Log.d("Client send Command", message);

    }

    //Classe privée thread de connexion héritant de thread à l'intérieur du service
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private InputStream mmInStream = null;
        private OutputStream mmOutStream = null;
        private byte[] mmBuffer; // mmBuffer store for the stream
        private BufferedReader reader;
        PrintWriter writer;
        /*Objet de type BufferedReader & Printwriter, plus simples à utiliser car n'utilisent
        * que des Strings et non des bytes.
        */

        //Méthode constructeur
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
