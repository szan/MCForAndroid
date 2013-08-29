package com.anusiewicz.MCForAndroid.views;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.TextView;
import com.anusiewicz.MCForAndroid.controllers.ConnectionManager;
import com.anusiewicz.MCForAndroid.controllers.MCForAndroidApplication;

/**
 * Created with IntelliJ IDEA.
 * User: Szymon Anusiewicz
 * Date: 29.08.13
 * Time: 23:28
 */
public class ConnectionInfoText extends TextView {

    private String remoteHost;

    public ConnectionInfoText(Context context) {
        super(context);
        remoteHost = null;
        this.update();

    }
    public ConnectionInfoText(Context context ,AttributeSet attr ) {
        super(context, attr);
        remoteHost = null;
        this.update();

    }

    public void update() {
        ConnectionManager.ConnectionStatus connectionStatus;
        try {
            connectionStatus = MCForAndroidApplication.getConnectionManager().getConnectionStatus();
            remoteHost = MCForAndroidApplication.getConnectionManager().getTCPClient().getRemoteHost();
        } catch (NullPointerException e) {
           connectionStatus = ConnectionManager.ConnectionStatus.NOT_CONNECTED;
        }
        switch(connectionStatus) {
            case OK:
                this.setText("Connected to: " + remoteHost);
                this.setTextColor(Color.GREEN);
                break;
            case CONNECTING:
                this.setText("Trying to connect...");
                this.setTextColor(Color.YELLOW);
                break;
            case FAILED:
                this.setText("Couldn't connect to host");
                this.setTextColor(Color.RED);
                break;
            case NOT_CONNECTED:
                this.setText("Not connected");
                this.setTextColor(Color.LTGRAY);
                break;
            case LOST:
                this.setText("Connection lost");
                this.setTextColor(Color.RED);
                break;
            default:
                this.setText("Not connected");
                this.setTextColor(Color.LTGRAY);
        }
    }

}
