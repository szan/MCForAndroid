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
        setContentView(R.layout.main);

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


    @Override
    public void onMessage(TCPClient client, String message) {
        Log.i("TCP","ON MESSAGE ____________________________" + message);
        isReadyToSend =true;

    }

    public class connectTask extends AsyncTask<String,String,String> {

        @Override
        protected String doInBackground(String... message) {

            isSettingConnection = true;
            mTCPClient = new TCPClient();
            mTCPClient.setTcpListener(EngineControlActivity.this);
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
