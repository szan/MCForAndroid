package com.anusiewicz.MCForAndroid.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.TextView;
import com.anusiewicz.MCForAndroid.R;
import com.anusiewicz.MCForAndroid.model.MCDeviceCode;
import com.anusiewicz.MCForAndroid.model.MCRequest;

/**
 * Created with IntelliJ IDEA.
 * User: Szymon Anusiewicz
 * Date: 25.08.13
 * Time: 12:42
 */
public class MCWordDeviceItem extends MCDeviceItem {

    public MCWordDeviceItem(Context context, MCDeviceCode deviceCode, int deviceNumber, String deviceName) throws IndexOutOfBoundsException{
        super(context,deviceCode,deviceNumber,deviceName);

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.mc_word_device_item, this, true);

        TextView deviceNameText = (TextView) findViewById(R.id.deviceNameText);
        deviceNameText.setText(deviceName);
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
