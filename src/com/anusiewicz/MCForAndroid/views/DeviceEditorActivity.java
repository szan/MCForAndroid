package com.anusiewicz.MCForAndroid.views;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.anusiewicz.MCForAndroid.R;
import com.anusiewicz.MCForAndroid.model.Constants;
import com.anusiewicz.MCForAndroid.model.MCDeviceCode;

/**
 * Created with IntelliJ IDEA.
 * User: Szymon Anusiewicz
 */
public class DeviceEditorActivity extends Activity {

    private static final String TAG = "DeviceEditorActivity";

    private MCDeviceCode deviceType;
    private String deviceName;
    private String deviceNumber;

    Spinner deviceSpinner;
    EditText deviceNumberText, deviceNameText;
    Button bSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_editor_layout);

        deviceSpinner = (Spinner) findViewById(R.id.deviceTypeSpinner);
        deviceSpinner.setAdapter(new ArrayAdapter<MCDeviceCode>(this, android.R.layout.simple_spinner_item, MCDeviceCode.values()));
        deviceNumberText = (EditText) findViewById(R.id.deviceNumberText);
        deviceNumberText.setText("");
        deviceNameText = (EditText) findViewById(R.id.deviceNameText);
        deviceNameText.setText("");
        bSave = (Button) findViewById(R.id.buttonSave);
        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                deviceName = deviceNameText.getText().toString();
                deviceNumber = deviceNumberText.getText().toString();
                deviceType = (MCDeviceCode) deviceSpinner.getSelectedItem();

                if (deviceName.matches("")) {
                    Toast.makeText(DeviceEditorActivity.this,"Specify device name", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (deviceNumber.matches("")) {
                    Toast.makeText(DeviceEditorActivity.this,"Specify device number", Toast.LENGTH_SHORT).show();
                    return;
                }

                Integer devNum;

                try {
                    devNum = new Integer(deviceNumberText.getText().toString());
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    Toast.makeText(DeviceEditorActivity.this,"Insert numeric value",Toast.LENGTH_SHORT).show();
                    return;
                }

                if (devNum > deviceType.getDeviceRange() || devNum < 0) {
                    Toast.makeText(DeviceEditorActivity.this,"Choose " + deviceType + " devices from 0 to " + deviceType.getDeviceRange(),Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent i = new Intent();
                i.putExtra(Constants.DEVICE_TYPE_TAG, deviceType);
                i.putExtra(Constants.DEVICE_NAME_TAG,deviceName);
                i.putExtra(Constants.DEVICE_NUMBER_TAG,devNum);
                setResult(RESULT_OK,i);
                finish();
            }
        });
    }
}
