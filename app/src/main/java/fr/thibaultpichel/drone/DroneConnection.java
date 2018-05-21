package fr.thibaultpichel.drone;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

public class DroneConnection extends AppCompatActivity implements View.OnClickListener {

    public static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drone_connection);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button connect_server = (Button) findViewById(R.id.button_connect_server);
        connect_server.setOnClickListener(this);
        Button connect_client = (Button) findViewById(R.id.button_connect_client);
        connect_client.setOnClickListener(this);


    }

    public void onClick(View v){
        //Dans la reaction au bouton
        Intent playIntent;
        switch(v.getId()) {
            case R.id.button_connect_client:
                playIntent = new Intent(this, Client.class);
                startActivity(playIntent);
                break;

            case R.id.button_connect_server:
                playIntent = new Intent(this, Server.class);
                startActivity(playIntent);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_drone_connection, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
