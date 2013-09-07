package com.anusiewicz.MCForAndroid.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;
import com.anusiewicz.MCForAndroid.R;
import com.anusiewicz.MCForAndroid.model.MCDeviceCode;
import com.anusiewicz.MCForAndroid.model.MCRequest;

/**
 * Created with IntelliJ IDEA.
 * User: Szymon Anusiewicz
 * Date: 25.08.13
 * Time: 15:22
 */
public class BitDeviceItem extends DeviceItem {

    ToggleButton currentValueBit;

    public BitDeviceItem(Context context, MCDeviceCode deviceCode, int deviceNumber, String deviceName) throws IndexOutOfBoundsException {
        super(context, deviceCode, deviceNumber,deviceName);

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.mc_bit_device_item, this, true);

        TextView deviceNameText = (TextView) findViewById(R.id.deviceNameText);
        deviceNameText.setText(deviceName);


        currentValueBit = (ToggleButton) findViewById(R.id.currentValueBit);
        Button bEdit = (Button) findViewById(R.id.buttonEdit);
        bEdit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                currentValueBit.setEnabled(true);
            }
        });
    }

    @Override
    public MCRequest getRequest() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void updateView() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
