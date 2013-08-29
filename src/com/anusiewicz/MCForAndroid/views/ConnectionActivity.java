package com.anusiewicz.MCForAndroid.views;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.anusiewicz.MCForAndroid.R;
import com.anusiewicz.MCForAndroid.controllers.ConnectionManager;
import com.anusiewicz.MCForAndroid.controllers.MCForAndroidApplication;

/**
 * Created with IntelliJ IDEA.
 * User: Szymon Anusiewicz
 * Date: 29.08.13
 * Time: 22:59
 */
public class ConnectionActivity extends Activity implements ConnectionManager.ConnectionListener {

    private ConnectionManager connectionManager;
    private EditText serverIpText, portText;
    private ConnectionInfoText infoText;
    private Button bConnect, bDisconnect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connection_layout);
        connectionManager = MCForAndroidApplication.getConnectionManager();
        serverIpText = (EditText) findViewById(R.id.ipAdress);
        portText = (EditText) findViewById(R.id.portAddress);
        infoText = (ConnectionInfoText) findViewById(R.id.connectionInfoItem);

        bConnect = (Button) findViewById(R.id.buttonConnect);
        bConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(getIP().length() != 0 && getPort().length() != 0) {
                    connectionManager.connectToRemoteHost(getIP(),getPort());
                }
            }
        });

        bDisconnect = (Button) findViewById(R.id.buttonDisconnect);
        bDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connectionManager.disconnectFromRemoteHost();
            }
        });
    }

    private String getIP(){
        return serverIpText.getText().toString();
    }

    private String getPort() {
        return portText.getText().toString();
    }

    @Override
    protected void onResume() {
        super.onResume();
        connectionManager.registerListener(this);
        this.runOnUiThread(new Runnable() {
            public void run() {
                infoText.update();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        connectionManager.unregisterListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        serverIpText = null;
        portText = null;
        infoText = null;
        bConnect = null;
        bDisconnect = null;
    }

    @Override
    public void onConnectionEstablished() {
        this.runOnUiThread(new Runnable() {
            public void run() {
                infoText.update();
            }
        });
    }

    @Override
    public void onConnectionLost() {
        this.runOnUiThread(new Runnable() {
            public void run() {
                infoText.update();
            }
        });
    }

    @Override
    public void onConnectionFailed() {
        this.runOnUiThread(new Runnable() {
            public void run() {
                infoText.update();
            }
        });
    }

    @Override
    public void onConnecting() {
        this.runOnUiThread(new Runnable() {
            public void run() {
                infoText.update();
            }
        });
    }
}
