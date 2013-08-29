package com.anusiewicz.MCForAndroid.controllers;

import android.app.Activity;
import android.content.Context;
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

public class MainActivity extends Activity implements TCPClient.TcpMessageListener,ConnectionManager.ConnectionListener {

    private Context context = MCForAndroidApplication.getAppContext();
    private ConnectionManager connectionManager;
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

        connectionManager = MCForAndroidApplication.getConnectionManager();
        connectionManager.registerListener(this);
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

                    if(getIP().length() != 0 && getPort().length() != 0) {
                         if (isConnected) {
                             connectionManager.reconnectToRemoteHost(getIP(),getPort());
                         } else {
                             connectionManager.connectToRemoteHost(getIP(),getPort());
                         }
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

    private String getPort() {
        return portText.getText().toString();
    }



    @Override
    public void onConnectionEstablished() {
        mTCPClient = connectionManager.getTCPClient();
        MainActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                infoText.setText("Connected to: " + mTCPClient.getRemoteHost());
                infoText.setTextColor(Color.GREEN);
            }
            });
        isConnected = true;
        isSettingConnection = false;
    }

    @Override
    public void onConnectionLost() {
        MainActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(MainActivity.this,"Connection Lost...",Toast.LENGTH_SHORT).show();
                infoText.setText("Not connected");
                infoText.setTextColor(Color.LTGRAY);
            }
        });
        isConnected = false;
        isSettingConnection = false;
    }

    @Override
    public void onConnectionFailed() {
        MainActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                infoText.setText("Couldn't connect");
                infoText.setTextColor(Color.RED);
            }
        });
        isConnected = false;
        isSettingConnection = false;
    }

    @Override
    public void onConnecting() {
        MainActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                infoText.setText("Trying to connect...");
                infoText.setTextColor(Color.YELLOW);
            }
        });
        isSettingConnection = true;
        isConnected = false;
    }

    @Override
    public void onReceive() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

}
