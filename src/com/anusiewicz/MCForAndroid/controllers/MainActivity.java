package com.anusiewicz.MCForAndroid.controllers;

import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.anusiewicz.MCForAndroid.R;
import com.anusiewicz.MCForAndroid.TCP.TCPClient;
import com.anusiewicz.MCForAndroid.model.MCCommand;
import com.anusiewicz.MCForAndroid.model.MCDeviceCode;
import com.anusiewicz.MCForAndroid.model.MCRequest;

public class MainActivity extends Activity implements TCPClient.TcpMessageListener {
    /**
     * Called when the activity is first created.

    */
    private TCPClient mTCPClient;
    private EditText serverIpText, portText, commandText, responseText, deviceNumberText,wordValueText;
    private TextView infoText;
    private CheckBox checkBox;
    private boolean isSettingConnection = false;
    private boolean isConnected;
    private Spinner commandSpinner,deviceSpinner;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        serverIpText = (EditText) findViewById(R.id.ipAdress);
        portText = (EditText) findViewById(R.id.portAddress);
        infoText = (TextView) findViewById(R.id.infoText);
        commandText = (EditText) findViewById(R.id.editCommand);
        responseText = (EditText) findViewById(R.id.editResponse);
        deviceNumberText = (EditText) findViewById(R.id.deviceNumText);
        wordValueText = (EditText) findViewById(R.id.wordValueText);
        commandSpinner = (Spinner) findViewById(R.id.commandSpinner);
        deviceSpinner = (Spinner) findViewById(R.id.deviceSpinner);
        checkBox = (CheckBox) findViewById(R.id.checkBox);

        commandSpinner.setAdapter(new ArrayAdapter<MCCommand>(this, android.R.layout.simple_spinner_item, MCCommand.values()));
        commandSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (commandSpinner.getSelectedItem().equals(MCCommand.PLC_RUN) || commandSpinner.getSelectedItem().equals(MCCommand.PLC_STOP)) {
                    deviceSpinner.setEnabled(false);
                    deviceNumberText.setEnabled(false);
                    wordValueText.setEnabled(false);
                    checkBox.setEnabled(false);
                }  else if (commandSpinner.getSelectedItem().equals(MCCommand.READ_BIT)) {
                    deviceSpinner.setEnabled(true);
                    deviceSpinner.setAdapter(new ArrayAdapter<MCDeviceCode>(MainActivity.this, android.R.layout.simple_spinner_item, MCDeviceCode.bitDevices()));
                    deviceNumberText.setEnabled(true);
                    wordValueText.setEnabled(false);
                    checkBox.setEnabled(false);
                } else if(commandSpinner.getSelectedItem().equals(MCCommand.READ_WORD)){
                    deviceSpinner.setEnabled(true);
                    deviceSpinner.setAdapter(new ArrayAdapter<MCDeviceCode>(MainActivity.this, android.R.layout.simple_spinner_item, MCDeviceCode.wordDevices()));
                    deviceNumberText.setEnabled(true);
                    wordValueText.setEnabled(false);
                    checkBox.setEnabled(false);
                } else if (commandSpinner.getSelectedItem().equals(MCCommand.WRITE_BIT)) {
                    deviceSpinner.setEnabled(true);
                    deviceSpinner.setAdapter(new ArrayAdapter<MCDeviceCode>(MainActivity.this, android.R.layout.simple_spinner_item, MCDeviceCode.bitDevices()));
                    deviceNumberText.setEnabled(true);
                    wordValueText.setEnabled(false);
                    checkBox.setEnabled(true);
                }   else if (commandSpinner.getSelectedItem().equals(MCCommand.WRITE_WORD)) {
                    deviceSpinner.setEnabled(true);
                    deviceSpinner.setAdapter(new ArrayAdapter<MCDeviceCode>(MainActivity.this, android.R.layout.simple_spinner_item, MCDeviceCode.wordDevices()));
                    deviceNumberText.setEnabled(true);
                    wordValueText.setEnabled(true);
                    checkBox.setEnabled(false);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                commandSpinner.setSelection(0);
            }
        }
        );

        deviceSpinner.setAdapter(new ArrayAdapter<MCDeviceCode>(this, android.R.layout.simple_spinner_item, MCDeviceCode.values()));

        Button bConnect = (Button) findViewById(R.id.buttonConnect);
        bConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!isSettingConnection) {
                    try
                    {
                     if(getIP().length() != 0 && getPort() != 0)
                      {
                          if (isConnected) {
                              mTCPClient.disconnect();
                              mTCPClient = null;
                          }
                         infoText.setText("Trying to connect...");
                         infoText.setTextColor(Color.YELLOW);
                         new connectTask().execute("");
                      }
                    }
                     catch(Exception e)
                    {
                       e.printStackTrace();
                       Log.i("TCP", "Could not connect " + e);
                    }
                }
            }
        });

        Button bDisconnect = (Button) findViewById(R.id.buttonDisconnect);
        bDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mTCPClient != null && !isSettingConnection) {
                    mTCPClient.disconnect();
                    mTCPClient = null;
                    infoText.setText("Not connected");
                    infoText.setTextColor(Color.LTGRAY);
                    isConnected = false;
                }
            }
        });

        Button bSend = (Button) findViewById(R.id.buttonSend);
        bSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isConnected){
                    String msg = commandText.getText().toString();
                    mTCPClient.enqueueRequest(msg);
                }
            }
        });

        Button bGenerate = (Button) findViewById(R.id.buttonGenerate);
        bGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Integer devNum = null;
                if (deviceNumberText.isEnabled()) {
                    try {
                        devNum = new Integer(deviceNumberText.getText().toString());
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this,"Insert device number",Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                Integer wordNum = null;
                if (wordValueText.isEnabled()) {

                    try {
                        wordNum = new Integer(wordValueText.getText().toString());
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this,"Insert value to write",Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                Boolean bit = null;

                if (checkBox.isEnabled()) {
                    bit = checkBox.isChecked();
                }

                try {
                    String command =
                            MCRequest.generateStringFromRequest(new MCRequest((MCCommand)commandSpinner.getSelectedItem(),
                                    (MCDeviceCode)deviceSpinner.getSelectedItem(),
                                    devNum,
                                    wordNum,
                                    bit));

                    commandText.setText(command);
                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        // disconnect
        if (mTCPClient != null) {
            mTCPClient.disconnect();
            mTCPClient = null;
            isConnected = false;
        }

    }

    private String getIP(){
        return serverIpText.getText().toString();
    }

    private int getPort() {
        Log.i("getPort", portText.getText().toString());
        return Integer.parseInt(portText.getText().toString());
    }

    @Override
    public void onMessage(String message) {
        final String msg = message;
        Log.i("TCP","RECEIVED MESSAGE: " + message);
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                responseText.setText(msg);
            }
        });

    }

    @Override
    public void lostConnection() {
        MainActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(MainActivity.this,"Connection Lost...",Toast.LENGTH_SHORT).show();
                infoText.setText("Not connected");
                infoText.setTextColor(Color.LTGRAY);
                }
        });
        isConnected = false;
    }

    public class connectTask extends AsyncTask<String,String,String> {

        @Override
        protected String doInBackground(String... message) {

            isSettingConnection = true;
            mTCPClient = new TCPClient();
            mTCPClient.registerListener(MainActivity.this);
            try {
                mTCPClient.connect(getIP(), getPort());
                return mTCPClient.getRemoteHost();
            } catch (Exception e) {
                  Log.e("TCP", "Couldn't connect to remote host. " + e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(String remoteHost) {
            super.onPostExecute(remoteHost);

            if (remoteHost != null) {
                infoText.setText("Connected to: " + remoteHost);
                infoText.setTextColor(Color.GREEN);
                isConnected = true;
            } else {
                infoText.setText("Couldn't connect");
                infoText.setTextColor(Color.RED);
                isConnected = false;
            }
             isSettingConnection = false;
        }
    }
}