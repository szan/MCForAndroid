package com.anusiewicz.MCForAndroid.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import com.anusiewicz.MCForAndroid.R;
import com.anusiewicz.MCForAndroid.TCP.TCPClient;
import com.anusiewicz.MCForAndroid.controllers.ConnectionManager;
import com.anusiewicz.MCForAndroid.controllers.DeviceFactory;
import com.anusiewicz.MCForAndroid.model.Constants;
import com.anusiewicz.MCForAndroid.model.MCDeviceCode;

/**
 * Created with IntelliJ IDEA.
 * User: Szymon Anusiewicz
 */
public class DeviceControlActivity extends ActivityWithMenu implements ConnectionManager.ConnectionListener, TCPClient.TcpMessageListener {

    TCPClient mTCPClient;
    private ConnectionInfoText infoText;
    private LinearLayout controlsLayout;
    private boolean isConnected;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_control_layout);

        infoText = (ConnectionInfoText) findViewById(R.id.infoText);
        controlsLayout = (LinearLayout) findViewById(R.id.devicesLayout);
        Button bAddControl = (Button) findViewById(R.id.buttonAdd);
        bAddControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DeviceFactory.startDevicePicker(DeviceControlActivity.this);
            }
        });

        connectionManager.registerListener(this);
        if (connectionManager.getConnectionStatus() == ConnectionManager.ConnectionStatus.OK) {
              isConnected = true;
        }
    }

    @Override
    public void onConnectionEstablished() {
        mTCPClient = connectionManager.getTCPClient();
        this.runOnUiThread(new Runnable() {
            public void run() {
                infoText.update();
            }
        });
        isConnected = true;
    }

    @Override
    public void onConnectionLost() {
        this.runOnUiThread(new Runnable() {
            public void run() {
                infoText.update();
            }
        });
        isConnected = false;
    }

    @Override
    public void onConnectionFailed() {
        this.runOnUiThread(new Runnable() {
            public void run() {
                infoText.update();
            }
        });
        isConnected = false;
    }

    @Override
    public void onConnecting() {
        this.runOnUiThread(new Runnable() {
            public void run() {
                infoText.update();
            }
        });
        isConnected = false;
    }

    @Override
    public void onReceive() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {

            if(resultCode == RESULT_OK){
                final String name = data.getStringExtra(Constants.DEVICE_NAME_TAG);
                final MCDeviceCode code = (MCDeviceCode) data.getSerializableExtra(Constants.DEVICE_TYPE_TAG);
                final Integer number = data.getIntExtra(Constants.DEVICE_NUMBER_TAG, 0);

                controlsLayout.addView(DeviceFactory.createMCDeviceItem(this,code,number,name));

            }
        }
    }
}
