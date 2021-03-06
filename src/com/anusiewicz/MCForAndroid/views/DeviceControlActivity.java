package com.anusiewicz.MCForAndroid.views;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.anusiewicz.MCForAndroid.R;
import com.anusiewicz.MCForAndroid.controllers.FileUtils;
import com.anusiewicz.MCForAndroid.controllers.TCPClient;
import com.anusiewicz.MCForAndroid.controllers.ConnectionManager;
import com.anusiewicz.MCForAndroid.controllers.DeviceFactory;
import com.anusiewicz.MCForAndroid.model.Constants;
import com.anusiewicz.MCForAndroid.model.MCDeviceCode;
import com.anusiewicz.MCForAndroid.model.MCRequest;
import com.anusiewicz.MCForAndroid.model.MCResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.concurrent.*;

/**
 * Created with IntelliJ IDEA.
 * User: Szymon Anusiewicz
 */
public class DeviceControlActivity extends ActivityWithMenu implements ConnectionManager.ConnectionListener, TCPClient.TcpMessageListener {

    private TCPClient mTCPClient;
    private HashMap <String, MCResponse> receivedData = new HashMap<String, MCResponse>();
    ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private ConnectionInfoText infoText;
    private LinearLayout controlsLayout;
    private boolean isConnected, isOnTop;
    private int refreshTime;
    private TextView titleText;
    private final static int DEFAULT_REFRESH_TIME = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();

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

        Button bSave = (Button) findViewById(R.id.buttonSave);
        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveToFile();
            }
        });

        titleText = (TextView) findViewById(R.id.titleTextView);
        refreshTime = DEFAULT_REFRESH_TIME;

        if (intent.hasExtra(Constants.TITLE_TAG)) {
            initiateFromFile(intent.getExtras().getString(Constants.TITLE_TAG));
        }

        if (intent.hasExtra(Constants.SCREEN_NAME_TAG)) {
            titleText.setText(intent.getExtras().getString(Constants.SCREEN_NAME_TAG));
            refreshTime = intent.getExtras().getInt(Constants.REFRESH_TIME_TAG);
        }

        connectionManager.registerListener(this);
        Log.i(DeviceControlActivity.this.getClass().getName(),"Scheduling refresh at fixed rate: " + refreshTime + " seconds");
        executor.scheduleAtFixedRate(new UpdateItems(),0,refreshTime, TimeUnit.SECONDS);
        isOnTop = true;

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mTCPClient != null) {
            mTCPClient.unregisterListener(this);
        }
        isOnTop = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isOnTop = true;
        if (connectionManager.getConnectionStatus() == ConnectionManager.ConnectionStatus.OK) {
            isConnected = true;
            mTCPClient = connectionManager.getTCPClient();
            mTCPClient.registerListener(this);
        }
        if (executor.isTerminated() || executor.isShutdown()) {
            executor.scheduleAtFixedRate(new UpdateItems(),0,DEFAULT_REFRESH_TIME, TimeUnit.SECONDS);
        }
    }

    private class UpdateItems implements Runnable {

        @Override
        public void run() {
            if (isConnected && isOnTop) {
                for ( int i = 0 ; i < controlsLayout.getChildCount(); i++ ) {
                    final DeviceItem item = (DeviceItem) controlsLayout.getChildAt(i);
                    mTCPClient.enqueueRequest(MCRequest.generateStringFromRequest(item.getReadRequest()));
                }
           }

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {

            if(resultCode == RESULT_OK){
                final String name = data.getStringExtra(Constants.DEVICE_NAME_TAG);
                final MCDeviceCode code = (MCDeviceCode) data.getSerializableExtra(Constants.DEVICE_TYPE_TAG);
                final Integer number = data.getIntExtra(Constants.DEVICE_NUMBER_TAG, 0);

                DeviceItem item = DeviceFactory.createMCDeviceItem(this, code, number, name);
                item.setId(controlsLayout.getChildCount());
                controlsLayout.addView(item);
                registerForContextMenu(item);
            }
        }
    }

    @Override
    public void onReceive(String request,String response) {

        Log.i(DeviceControlActivity.class.getName(), "OnReceive");

        MCResponse mcResponse = MCResponse.parseMCResponseFromString(response);
        if (request != null && mcResponse != null) {
        receivedData.put(request,mcResponse);
        }

        updateControls();

    }

    private void updateControls() {
        Log.i(DeviceControlActivity.class.getName(), "OnTop: " + isOnTop + ". Updating controls...");
        if (isOnTop) {

            for ( int i = 0 ; i < controlsLayout.getChildCount(); i++ ) {
                final DeviceItem item = (DeviceItem) controlsLayout.getChildAt(i);
                DeviceControlActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                       item.updateViewFromData(receivedData);
                    }
                });
            }
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        DeviceItem item = (DeviceItem) v;
        menu.setHeaderTitle(item.getDeviceName() + " (" + item.getDeviceType() + item.getDeviceNumber() +")");
        menu.add(Menu.NONE, v.getId(), Menu.NONE, "Delete");
        menu.add(Menu.NONE,v.getId(),Menu.NONE,"Push Value");
        //menu.add(Menu.NONE,v.getId(),Menu.NONE,"Edit");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle() == "Delete" ) {
            View v = controlsLayout.findViewById(item.getItemId());
            controlsLayout.removeView(v);
        } else if (item.getTitle() == "Push Value" ) {
            DeviceItem v = (DeviceItem) controlsLayout.findViewById(item.getItemId());
            new PushDialog(v).showDialog();
        }
        return true;
    }



    private void initiateFromFile(String fileName) {

        String data = FileUtils.readFromFile(Constants.FILES_DIRECTORY + fileName);

        try {
            JSONObject screenObject = new JSONObject(data);
            titleText.setText(screenObject.getString(Constants.TITLE_TAG));

            int time = screenObject.getInt(Constants.REFRESH_TIME_TAG);
            if (time <= 0) {
                refreshTime = DEFAULT_REFRESH_TIME;
            } else {
                refreshTime = time;
            }

            JSONArray devices = screenObject.getJSONArray(Constants.DEVICES_TAG);

            for (int i = 0; i < devices.length(); i++) {
                JSONObject item = devices.getJSONObject(i);

                String name = item.getString(Constants.DEVICE_NAME_TAG);
                MCDeviceCode code = MCDeviceCode.parseString(item.getString(Constants.DEVICE_TYPE_TAG));
                int number = item.getInt(Constants.DEVICE_NUMBER_TAG);

                DeviceItem deviceItem = DeviceFactory.createMCDeviceItem(this, code, number, name);
                deviceItem.setId(controlsLayout.getChildCount());
                controlsLayout.addView(deviceItem);
                registerForContextMenu(deviceItem);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private boolean saveToFile() {

        JSONObject screenObject = new JSONObject();
        try {
            screenObject.put(Constants.TITLE_TAG, titleText.getText());
            screenObject.put(Constants.REFRESH_TIME_TAG, refreshTime);
            JSONArray devices = new JSONArray();

            for ( int i = 0 ; i < controlsLayout.getChildCount(); i++ ) {
                final DeviceItem item = (DeviceItem) controlsLayout.getChildAt(i);
                JSONObject device = new JSONObject();
                device.put(Constants.DEVICE_NAME_TAG,item.getDeviceName());
                device.put(Constants.DEVICE_TYPE_TAG, item.getDeviceType());
                device.put(Constants.DEVICE_NUMBER_TAG,item.getDeviceNumber());
                devices.put(device);
            }
            screenObject.put(Constants.DEVICES_TAG, devices);

            return FileUtils.writeToFile(screenObject.toString(),Constants.FILES_DIRECTORY + titleText.getText());

        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }


    private class PushDialog extends AlertDialog {

        private AlertDialog.Builder builder;
        private Integer pushValue;
        EditText editText = null;

        public PushDialog(final DeviceItem item) {
            super(DeviceControlActivity.this);

            builder = new AlertDialog.Builder(DeviceControlActivity.this);
            // Set the dialog title
            builder.setTitle(item.getDeviceName() + " (" + item.getDeviceType() + item.getDeviceNumber() + ")")

                   .setPositiveButton("Push", new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialog, int id) {

                           if (editText != null) {
                               try {
                                   pushValue = Integer.parseInt(editText.getText().toString());
                               } catch (NumberFormatException e) {
                                   e.printStackTrace();
                                   Toast.makeText(getContext(),"Can't write this value",Toast.LENGTH_SHORT).show();
                               }
                           }
                           if (pushValue == null || !isConnected) {
                               return;
                           }

                           MCRequest request = item.getWriteRequest(pushValue);
                           if (request == null) {
                               Toast.makeText(DeviceControlActivity.this, "Specified value exceeds the valid range",Toast.LENGTH_SHORT).show();
                               return;
                           }

                           mTCPClient.prioritizeRequest(MCRequest.generateStringFromRequest(request));
                           mTCPClient.prioritizeRequest(MCRequest.generateStringFromRequest(item.getReadRequest()));
                       }
                   })
                   .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialog, int id) {
                           dialog.dismiss();
                       }
                   });

            if (MCDeviceCode.bitDevices().contains(item.getDeviceType())) {

                CharSequence[] strings = new CharSequence[2];
                strings[0] = "OFF";
                strings[1] = "ON";
                builder.setSingleChoiceItems(strings, 0 , new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                       pushValue = i;
                    }
                });

                pushValue = 0;
            } else if (MCDeviceCode.wordDevices().contains(item.getDeviceType())) {

                editText = new EditText(getContext());
                editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_NUMBER);
                builder.setView(editText);

            }


        }
        public void showDialog() {
            builder.create().show();
        }
    }
}
