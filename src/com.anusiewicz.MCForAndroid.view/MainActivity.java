package com.anusiewicz.MCForAndroid.view;

import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.anusiewicz.MCForAndroid.R;
import com.anusiewicz.MCForAndroid.TCP.TCPClient;

public class MainActivity extends Activity implements TCPClient.TcpMessageListener {
    /**
     * Called when the activity is first created.

    */
    private TCPClient mTCPClient;
    EditText serverIpText, portText;
    TextView infoText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        serverIpText = (EditText) findViewById(R.id.ipAdress);
        portText = (EditText) findViewById(R.id.portAddress);
        infoText = (TextView) findViewById(R.id.infoText);

        Button bConnect = (Button) findViewById(R.id.buttonConnect);
        bConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mTCPClient == null || !mTCPClient.isConnected()) {
                    try
                    {
                     if(getIP().length() != 0 && getPort() != 0)
                      {
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
                if (mTCPClient != null) {
                    mTCPClient.disconnect();
                    mTCPClient = null;
                    infoText.setText("Disconnected");
                    infoText.setTextColor(Color.RED);
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
        }

    }

    private String getIP(){
        return serverIpText.getText().toString();
    }

    private int getPort() {
        Log.i("getPort",portText.getText().toString() );
        return Integer.parseInt(portText.getText().toString());
    }


    @Override
    public void onMessage(TCPClient client, String message) {
        //TODO
    }

    public class connectTask extends AsyncTask<String,String,String> {

        @Override
        protected String doInBackground(String... message) {

            mTCPClient = new TCPClient();
            mTCPClient.setTcpListener(MainActivity.this);
            mTCPClient.connect(getIP(), getPort());

            return mTCPClient.getRemoteHost();
        }

        @Override
        protected void onPostExecute(String remoteHost) {
            super.onPostExecute(remoteHost);

            if (remoteHost != null) {
                infoText.setText("Connected to: " + remoteHost);
                infoText.setTextColor(Color.GREEN);
            } else {
                infoText.setText("Disconnected");
                infoText.setTextColor(Color.RED);
            }
        }
    }
}
