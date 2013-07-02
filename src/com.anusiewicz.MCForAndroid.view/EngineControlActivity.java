package com.anusiewicz.MCForAndroid.view;

import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.anusiewicz.MCForAndroid.R;
import com.anusiewicz.MCForAndroid.TCP.TCPClient;
import com.anusiewicz.MCForAndroid.model.MCRequest;

/**
 * Created with IntelliJ IDEA.
 * User: Szymon Anusiewicz
 * Date: 01.07.13
 * Time: 17:35
 */
public class EngineControlActivity extends Activity implements TCPClient.TcpMessageListener {

    private TCPClient mTCPClient;
    private EditText serverIpText, portText, zadaneText, aktualneText, uchybText, skokText;
    private TextView infoText;
    private boolean isSettingConnection = false;
    private boolean isConnected, isReadyToSend;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.engine_control_layout);

        serverIpText = (EditText) findViewById(R.id.ipAdress);
        portText = (EditText) findViewById(R.id.portAddress);
        infoText = (TextView) findViewById(R.id.infoText);
        zadaneText = (EditText)  findViewById(R.id.zadaneText);
        aktualneText = (EditText) findViewById(R.id.aktualneText);
        uchybText =  (EditText) findViewById(R.id.uchybText);
        skokText = (EditText) findViewById(R.id.skokText);

        zadaneText.setEnabled(false);
        aktualneText.setEnabled(false);
        uchybText.setEnabled(false);

        Button bReset = (Button) findViewById(R.id.buttonReset);
        bReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isConnected &&isReadyToSend) {

                        resetValues();
                }


            }
        });

        Button bRefresh = (Button) findViewById(R.id.buttonRefresh);
        bRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isConnected && isReadyToSend) {

                    MCRequest request = new MCRequest(MCRequest.MCCommand.READ_WORD, MCRequest.MCDeviceCode.D, 202);
                    String msg = MCRequest.generateStringFromRequest(request);
                    mTCPClient.sendMessage(msg);
                    isReadyToSend = false;

                }


            }
        });

        Button bUp = (Button) findViewById(R.id.buttonUP);
        bUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isConnected &&isReadyToSend) {
                Integer value;
                try {
                    value = Integer.parseInt(zadaneText.getText().toString()) + Integer.parseInt(skokText.getText().toString());
                } catch (NumberFormatException e) {

                    e.printStackTrace();
                    Toast.makeText(EngineControlActivity.this,"Wpisz skok położenia",Toast.LENGTH_SHORT).show();
                    return;
                }
                MCRequest request = new MCRequest(MCRequest.MCCommand.WRITE_WORD, MCRequest.MCDeviceCode.D, 200, value, null);
                String msg = MCRequest.generateStringFromRequest(request);
                mTCPClient.sendMessage(msg);
                isReadyToSend = false;
                zadaneText.setText(value.toString());
                }


            }
        });

        Button bDown = (Button) findViewById(R.id.buttonDown);
        bDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isConnected && isReadyToSend) {
                Integer value;
                try {
                    value = Integer.parseInt(zadaneText.getText().toString()) - Integer.parseInt(skokText.getText().toString());
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    Toast.makeText(EngineControlActivity.this,"Wpisz skok położenia",Toast.LENGTH_SHORT).show();
                    return;
                }
                MCRequest request = new MCRequest(MCRequest.MCCommand.WRITE_WORD, MCRequest.MCDeviceCode.D, 200, value, null);
                String msg = MCRequest.generateStringFromRequest(request);
                mTCPClient.sendMessage(msg);
                isReadyToSend = false;
                zadaneText.setText(value.toString());
                }
            }
        });

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
    }

    @Override
    protected void onPause() {
        super.onPause();

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

    private void resetValues() {

        StringBuilder builder = new StringBuilder(50);
        builder.append(MCRequest.MCCommand.WRITE_WORD.getCommandCode())
                .append("FF0000")
                .append(MCRequest.MCDeviceCode.D.getDeviceCode());

            String devNum = Integer.toHexString(200);

            for ( int i = 1; i<= 8-devNum.length(); i++ ) {
                builder.append("0");
            }
            builder.append(devNum)
                    .append("0300")
                    .append("000000000000");

        zadaneText.setText("0");
        aktualneText.setText("0");
        uchybText.setText("0");

        mTCPClient.sendMessage(builder.toString());
        isReadyToSend = false;

    }

    @Override
    public void onMessage(String message) {
        Log.i("TCP","ON MESSAGE ____________________________" + message);
        isReadyToSend =true;

        Integer value;
        if (message.startsWith("8100")) {

            value = Integer.parseInt(message.substring(3),16);
        }  else {
            return;
        }
        final int finalValue = value;

        EngineControlActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                aktualneText.setText(finalValue + "");
                uchybText.setText(Integer.parseInt(zadaneText.getText().toString()) - finalValue + "");
            }
        });

    }

    public class connectTask extends AsyncTask<String,String,String> {

        @Override
        protected String doInBackground(String... message) {

            isSettingConnection = true;
            mTCPClient = new TCPClient(EngineControlActivity.this);
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
                isReadyToSend = true;
            } else {
                infoText.setText("Couldn't connect");
                infoText.setTextColor(Color.RED);
                isConnected = false;
            }
            isSettingConnection = false;
        }
    }
}
