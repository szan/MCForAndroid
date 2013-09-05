package com.anusiewicz.MCForAndroid.views;

import android.content.Context;
import android.widget.LinearLayout;
import com.anusiewicz.MCForAndroid.model.MCDeviceCode;
import com.anusiewicz.MCForAndroid.model.MCRequest;

/**
 * Created with IntelliJ IDEA.
 * User: Szymon Anusiewicz
 * Date: 25.08.13
 * Time: 12:37
 */
public abstract class DeviceItem extends LinearLayout {

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

    public abstract MCRequest getRequest();

    public abstract void updateView();


}
