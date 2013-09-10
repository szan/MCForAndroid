package com.anusiewicz.MCForAndroid.controllers;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import com.anusiewicz.MCForAndroid.views.ConnectionActivity;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Szymon Anusiewicz
 * Date: 28.08.13
 * Time: 20:42
 */
public class ConnectionManager implements TCPClient.OnTCPConnectionLostListener {


    public interface ConnectionListener {

        public void onConnectionEstablished();

        public void onConnectionLost();

        public void onConnectionFailed();

        public void onConnecting();

    }

    private static final String TAG = ConnectionManager.class.getName();
    private Context context = MCForAndroidApplication.getAppContext();
    private TCPClient mTCPClient;
    private ArrayList<ConnectionListener> connectionListeners = new ArrayList<ConnectionListener>();
    private ConnectionStatus status = ConnectionStatus.NOT_CONNECTED;

    public static enum ConnectionStatus {
        OK,
        LOST,
        FAILED,
        NOT_CONNECTED,
        CONNECTING
    }

    public TCPClient getTCPClient() {
            return mTCPClient;
    }

    private void setConnectionStatus(ConnectionStatus status) {
        this.status = status;
    }

    public ConnectionStatus getConnectionStatus() {
        return this.status;
    }

    public void registerListener(ConnectionListener listener) {
        connectionListeners.add(listener);
    }

    public void unregisterListener(ConnectionListener listener) {
        connectionListeners.remove(listener);
    }

    public void connectToRemoteHost(String IPAddress, String port) {
        if (!getConnectionStatus().equals(ConnectionStatus.OK))  {
            Log.i(TAG,"Trying to connect");
            new ConnectTask().execute(IPAddress,port);
        } else {
            reconnectToRemoteHost(IPAddress, port);
        }
    }

    public void reconnectToRemoteHost(String IPAddress, String port) {
        Log.i(TAG,"Trying to reconnect");
        disconnectFromRemoteHost();
        connectToRemoteHost(IPAddress,port);
    }

    public void disconnectFromRemoteHost() {
        Log.i(TAG,"Disconnecting");
        setConnectionStatus(ConnectionStatus.NOT_CONNECTED);
        if (mTCPClient != null) {
             mTCPClient.disconnect();
             mTCPClient = null;
        }
        for (ConnectionListener listener: connectionListeners){
            listener.onConnectionLost();
        }

    }

    public void showConnectionActivity(Context context) {
        Intent i = new Intent(context, ConnectionActivity.class);
        context.startActivity(i);
    }


    public class ConnectTask extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... params) {

            setConnectionStatus(ConnectionStatus.CONNECTING);
            for (ConnectionListener listener: connectionListeners){
                listener.onConnecting();
            }
            try {
                String IPAddress =params[0];
                int Port = Integer.parseInt(params[1]);
                mTCPClient = new TCPClient(ConnectionManager.this);
                mTCPClient.connect(IPAddress, Port);
                return mTCPClient.getRemoteHost();
            } catch (Exception e) {
                Log.e(TAG, "Couldn't connect to remote host. " + e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(String remoteHost) {
            super.onPostExecute(remoteHost);

            if (remoteHost != null) {
                setConnectionStatus(ConnectionStatus.OK);
                Log.i(TAG,"Connected to: " + remoteHost);
                for (ConnectionListener listener: connectionListeners){
                    listener.onConnectionEstablished();
                }
            } else {
                setConnectionStatus(ConnectionStatus.FAILED);
                Log.i(TAG,"Connection failed");
                for (ConnectionListener listener: connectionListeners){
                    listener.onConnectionFailed();
                }
            }
        }
    }

    @Override
    public void lostConnection() {
        setConnectionStatus(ConnectionStatus.LOST);

        Log.i(TAG,"Connection lost");
        for (ConnectionListener listener: connectionListeners){
            listener.onConnectionLost();
        }
    }

}
