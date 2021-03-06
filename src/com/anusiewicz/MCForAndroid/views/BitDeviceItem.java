package com.anusiewicz.MCForAndroid.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;
import com.anusiewicz.MCForAndroid.R;
import com.anusiewicz.MCForAndroid.model.MCCommand;
import com.anusiewicz.MCForAndroid.model.MCDeviceCode;
import com.anusiewicz.MCForAndroid.model.MCRequest;
import com.anusiewicz.MCForAndroid.model.MCResponse;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Szymon Anusiewicz
 */
public class BitDeviceItem extends DeviceItem {

    ToggleButton currentValueBit;

    public BitDeviceItem(Context context, MCDeviceCode deviceCode, int deviceNumber, String deviceName) throws IndexOutOfBoundsException {
        super(context, deviceCode, deviceNumber,deviceName);

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.mc_bit_device_item, this, true);

        TextView deviceNameText = (TextView) findViewById(R.id.deviceNameText);
        deviceNameText.setText(deviceName + ":");


        currentValueBit = (ToggleButton) findViewById(R.id.currentValueBit);
        currentValueBit.setTextOff("OFF");
        currentValueBit.setTextOn("ON");
    }

    @Override
    public MCRequest getWriteRequest(int value) {

        boolean bit;
        bit = value > 0;
        return new MCRequest(MCCommand.WRITE_BIT,deviceType,deviceNumber,null,bit);
    }

    @Override
    public MCRequest getReadRequest() {
        return new MCRequest(MCCommand.READ_BIT,deviceType,deviceNumber);
    }

    @Override
    public void updateViewFromData(HashMap data) {
        String key = MCRequest.generateStringFromRequest(this.getReadRequest());
        if (data.containsKey(key)) {
            MCResponse response = (MCResponse) data.get(key);
            if (response.getBitValue() != null) {
                currentValueBit.setChecked(response.getBitValue());
            }
        }
    }
}
