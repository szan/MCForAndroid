package com.anusiewicz.MCForAndroid.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.anusiewicz.MCForAndroid.R;
import com.anusiewicz.MCForAndroid.controllers.TCPClient;
import com.anusiewicz.MCForAndroid.controllers.ConnectionManager;
import com.anusiewicz.MCForAndroid.model.Constants;
import com.anusiewicz.MCForAndroid.model.MCCommand;
import com.anusiewicz.MCForAndroid.model.MCDeviceCode;
import com.anusiewicz.MCForAndroid.model.MCRequest;

public class CustomCommandActivity extends ActivityWithMenu implements TCPClient.TcpMessageListener,ConnectionManager.ConnectionListener {


    private TCPClient mTCPClient;
    private EditText commandText, responseText, deviceNumberText,wordValueText;
    private ConnectionInfoText infoText;
    private CheckBox checkBox;
    private boolean isConnected;
    private Spinner commandSpinner,deviceSpinner;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_command_layout);

        connectionManager.registerListener(this);
        infoText = (ConnectionInfoText) findViewById(R.id.infoText);
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
                    deviceSpinner.setAdapter(new ArrayAdapter<MCDeviceCode>(CustomCommandActivity.this, android.R.layout.simple_spinner_item, MCDeviceCode.bitDevices()));
                    deviceNumberText.setEnabled(true);
                    wordValueText.setEnabled(false);
                    checkBox.setEnabled(false);
                } else if(commandSpinner.getSelectedItem().equals(MCCommand.READ_WORD)){
                    deviceSpinner.setEnabled(true);
                    deviceSpinner.setAdapter(new ArrayAdapter<MCDeviceCode>(CustomCommandActivity.this, android.R.layout.simple_spinner_item, MCDeviceCode.wordDevices()));
                    deviceNumberText.setEnabled(true);
                    wordValueText.setEnabled(false);
                    checkBox.setEnabled(false);
                } else if (commandSpinner.getSelectedItem().equals(MCCommand.WRITE_BIT)) {
                    deviceSpinner.setEnabled(true);
                    deviceSpinner.setAdapter(new ArrayAdapter<MCDeviceCode>(CustomCommandActivity.this, android.R.layout.simple_spinner_item, MCDeviceCode.bitDevices()));
                    deviceNumberText.setEnabled(true);
                    wordValueText.setEnabled(false);
                    checkBox.setEnabled(true);
                }   else if (commandSpinner.getSelectedItem().equals(MCCommand.WRITE_WORD)) {
                    deviceSpinner.setEnabled(true);
                    deviceSpinner.setAdapter(new ArrayAdapter<MCDeviceCode>(CustomCommandActivity.this, android.R.layout.simple_spinner_item, MCDeviceCode.wordDevices()));
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
                        Toast.makeText(CustomCommandActivity.this,"Insert device number",Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                Integer wordNum = null;
                if (wordValueText.isEnabled()) {

                    try {
                        wordNum = new Integer(wordValueText.getText().toString());
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        Toast.makeText(CustomCommandActivity.this,"Insert value to write",Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(CustomCommandActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();

                }
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (connectionManager.getConnectionStatus() == ConnectionManager.ConnectionStatus.OK) {
            isConnected = true;
            mTCPClient = connectionManager.getTCPClient();
            mTCPClient.registerListener(this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mTCPClient != null) {
            mTCPClient.unregisterListener(this);
            isConnected = false;
        }
    }

    @Override
    public void onConnectionEstablished() {
        mTCPClient = connectionManager.getTCPClient();
        CustomCommandActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                infoText.update();
            }
            });
        isConnected = true;
    }

    @Override
    public void onConnectionLost() {
        CustomCommandActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                infoText.update();
            }
        });
        isConnected = false;
    }

    @Override
    public void onConnectionFailed() {
        CustomCommandActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                infoText.update();
            }
        });
        isConnected = false;
    }

    @Override
    public void onConnecting() {
        CustomCommandActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                infoText.update();
            }
        });
        isConnected = false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {

            if(resultCode == RESULT_OK){
                final String name = data.getStringExtra(Constants.DEVICE_NAME_TAG);
                final MCDeviceCode code = (MCDeviceCode) data.getSerializableExtra(Constants.DEVICE_TYPE_TAG);
                final Integer number = data.getIntExtra(Constants.DEVICE_NUMBER_TAG, 0);

                CustomCommandActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        responseText.setText(name + code + number);
                    }
                });

            }
        }
    }

    @Override
    public void onReceive(String request, final String response) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                responseText.setText(response);
            }
        });
    }
}
