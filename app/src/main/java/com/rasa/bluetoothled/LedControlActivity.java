package com.rasa.bluetoothled;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import java.util.UUID;

public class LedControlActivity extends AppCompatActivity {

    private Button btnOn, btnOff, btnDisc;
    private SeekBar brightness;
    private String address;
    private ProgressDialog progressDialog;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket bluetoothSocket;
    private boolean isConnected = false;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_led_control);

        address = getIntent().getStringExtra(MainActivity.EXTRA_ADDRESS);
        btnOn = (Button) findViewById(R.id.btn_on);
        btnOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                turnOn();
            }
        });
        btnOff = (Button) findViewById(R.id.btn_off);
        btnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                turnOff();
            }
        });
        btnDisc = (Button) findViewById(R.id.btn_disconnect);
        btnDisc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disconnect();
            }
        });
        brightness = (SeekBar) findViewById(R.id.brightness_seek);
        brightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                sendCommand(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        new ConnectBt().execute();
    }

    private void disconnect(){
        if(bluetoothSocket!=null){
            try{
                bluetoothSocket.close();
            }catch (Exception e){
                msg("error");
            }
        }
        finish();
    }

    private void turnOff(){
        sendCommand("TF".toString());
    }

    private void turnOn(){
        sendCommand("TO".toString());
    }

    private void sendCommand(String command){
        if(bluetoothSocket!=null){
            try{
                bluetoothSocket.getOutputStream().write(command.getBytes());
            }catch (Exception e){
                msg("error"+command);
            }
        }
    }
    private void msg(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private class ConnectBt  extends AsyncTask<Void, Void, Void>{
        private boolean ConnectStatus = true;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(LedControlActivity.this, "Connecting...", "Please wait!!!");
        }

        @Override
        protected Void doInBackground(Void... params) {
            try{
                if(bluetoothSocket == null || !isConnected){
                    bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                    BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
                    bluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(myUUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    bluetoothSocket.connect();
                }
            }catch (Exception e){
                ConnectStatus = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(!ConnectStatus){
                msg("ConnectionFailed");
                finish();
            }else{
                msg("Connect");
                isConnected = true;
            }
            progressDialog.dismiss();
        }
    }


}
