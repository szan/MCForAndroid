package com.anusiewicz.MCForAndroid.views;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
public class WordDeviceItem extends DeviceItem {

    EditText currentValueText;

    public WordDeviceItem(Context context, MCDeviceCode deviceCode, int deviceNumber, String deviceName) throws IndexOutOfBoundsException{
        super(context,deviceCode,deviceNumber,deviceName);

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.mc_word_device_item, this, true);

        TextView deviceNameText = (TextView) findViewById(R.id.deviceNameText);
        deviceNameText.setText(deviceName + ":");

        currentValueText = (EditText) findViewById(R.id.currentValueText);
    }

    @Override
    public MCRequest getReadRequest() {
        return new MCRequest(MCCommand.READ_WORD,deviceType,deviceNumber);
    }

    @Override
    public MCRequest getWriteRequest(int value) {

        MCRequest ret;

        try {
            ret = new MCRequest(MCCommand.WRITE_WORD,deviceType,deviceNumber,value,null);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            ret = null;
        }
        return ret;
    }

    @Override
    public void updateViewFromData(HashMap data) {
        String key = MCRequest.generateStringFromRequest(this.getReadRequest());
        if (data.containsKey(key)) {
            MCResponse response = (MCResponse) data.get(key);
            if (response.getWordValue() != null) {
                currentValueText.setText(response.getWordValue().toString());
            }
        }
    }
}
