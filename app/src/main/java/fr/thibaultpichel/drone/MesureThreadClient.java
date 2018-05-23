package fr.thibaultpichel.drone;

import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * Created by tpichel on 22/05/18.
 */

public class MesureThreadClient {
    private static final String TAG = "MY_APP_DEBUG_TAG";
    private Handler mHandler; // handler that gets info from Bluetooth service
    private BluetoothSocket socket;
    private ConnectedThread connectedThread;
    private WifiManager wifiMan;

    // Defines several constants used when transmitting messages between the
    // service and the UI.

    public MesureThreadClient(BluetoothSocket socket, WifiManager wm){ //pas de handler
        this.socket = socket;
        this.connectedThread = new ConnectedThread(socket);
        this.wifiMan = wm;
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

    public void startConnectedThread(){
        this.connectedThread.start();
    }

    public void sendCommand(String message){
        this.connectedThread.write(message);
        Log.d("Client receive Command", message);

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
        private final int INTERVAL_SEND = 800;

        int rssi;
        double freqInMHz, exp, distance;


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

        //mesure periodique client
        public void run() {
            //mmBuffer = new byte[1024];
            //int numBytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs.

                    //attente d'une requete du serveur
                    while (true) {
                        try {
                            Log.d("Client - Run", "début attente d'une commande du serveur puis envoi d'un message au serveur");
                            String receivedMessage = reader.readLine();
                            Log.d("Client - Rcv Message", receivedMessage);
                            Message msgForA = new Message();
                            msgForA.obj = receivedMessage;

                            //this.mReceiver.onReceive();
                            //MesureThreadClient.this.mHandler.sendMessage(msgForA);
                            //Quand la demande de distance est recue par le client, on calcule la distance et puis l'envoie au serveur

                            //String name = wifiInf.getSSID();
                            // if(name.equals("unknown ssid")) //si pas de connection en wifi
                            WifiInfo wifiInf = wifiMan.getConnectionInfo();//Return dynamic information about the current Wi-Fi connection, if any is active.

                            if (receivedMessage.equals("distance?")){
                                for(ScanResult result : wifiMan.getScanResults()) {

                                    if(result.SSID.equals("AndroidRemote")) { //si cest le wifi de notre drone

                                        rssi = wifiInf.getRssi(); //la puissance
                                        freqInMHz = result.frequency; //la puissance
                                        exp = (27.55 - (20 * Math.log10(freqInMHz)) + Math.abs(rssi)) / 20.0;
                                        distance = Math.pow(10.0, exp);
                                        Log.d("Distance WAP", Double.toString(distance));
                                    }

                                }

                                write(""+distance);
                            }
                        } catch (IOException e) {
                            Log.d(TAG, "Input stream was disconnected", e);
                            break;
                        }
                    }

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
