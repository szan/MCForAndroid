package com.anusiewicz.MCForAndroid.views;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.anusiewicz.MCForAndroid.model.MCDeviceCode;
import com.anusiewicz.MCForAndroid.model.MCRequest;

/**
 * Created with IntelliJ IDEA.
 * User: Szymon Anusiewicz
 * Date: 25.08.13
 * Time: 12:37
 */
public abstract class MCDeviceItem extends LinearLayout {

    protected MCDeviceCode deviceType;
    protected int deviceNumber;
    protected String deviceName;

    public MCDeviceItem(Context context, MCDeviceCode deviceCode, int deviceNumber,String deviceName) throws IndexOutOfBoundsException{
        super(context);
        this.deviceType = deviceCode;
        this.deviceName = deviceName;

        if (deviceNumber <= deviceCode.getDeviceRange()) {
            this.deviceNumber = deviceNumber;
        } else {
            throw new IndexOutOfBoundsException("Choose " + deviceType + " devices from 0 to " + deviceType.getDeviceRange());
        }
    }

    public abstract MCRequest getRequest();

    public abstract void updateView();

    public static MCDeviceItem createMCDeviceItem(Context context, MCDeviceCode deviceCode, int deviceNumber, String deviceName) {

        if (MCDeviceCode.bitDevices().contains(deviceCode)){
             return new MCBitDeviceItem(context,deviceCode,deviceNumber,deviceName);
        } else if (MCDeviceCode.wordDevices().contains(deviceCode)) {
             return  new MCWordDeviceItem(context,deviceCode,deviceNumber,deviceName);
        } else {
            return null;
        }

    }

}
