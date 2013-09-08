package com.anusiewicz.MCForAndroid.views;

import android.content.Context;
import android.widget.LinearLayout;
import com.anusiewicz.MCForAndroid.model.MCDeviceCode;
import com.anusiewicz.MCForAndroid.model.MCRequest;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Szymon Anusiewicz
 * Date: 25.08.13
 * Time: 12:37
 */
public abstract class DeviceItem extends LinearLayout {

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public int getDeviceNumber() {
        return deviceNumber;
    }

    public void setDeviceNumber(int deviceNumber) {
        this.deviceNumber = deviceNumber;
    }

    public MCDeviceCode getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(MCDeviceCode deviceType) {
        this.deviceType = deviceType;
    }

    protected MCDeviceCode deviceType;
    protected int deviceNumber;
    protected String deviceName;

    public DeviceItem(Context context, MCDeviceCode deviceCode, int deviceNumber, String deviceName) throws IndexOutOfBoundsException{
        super(context);
        this.deviceType = deviceCode;
        this.deviceName = deviceName;

        if (deviceNumber <= deviceCode.getDeviceRange()) {
            this.deviceNumber = deviceNumber;
        } else {
            throw new IndexOutOfBoundsException("Choose " + deviceType + " devices from 0 to " + deviceType.getDeviceRange());
        }
    }

    public abstract MCRequest getWriteRequest(int value);

    public abstract MCRequest getReadRequest();

    public abstract void updateViewFromData(HashMap data);


}
