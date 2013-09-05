package com.anusiewicz.MCForAndroid.controllers;

import android.content.Context;
import com.anusiewicz.MCForAndroid.model.MCDeviceCode;
import com.anusiewicz.MCForAndroid.views.BitDeviceItem;
import com.anusiewicz.MCForAndroid.views.DeviceItem;
import com.anusiewicz.MCForAndroid.views.WordDeviceItem;

/**
 * Created with IntelliJ IDEA.
 * User: Szymon Anusiewicz
 */
public class DeviceFactory {


    public static DeviceItem createMCDeviceItem(Context context, MCDeviceCode deviceCode, int deviceNumber, String deviceName) {

        if (MCDeviceCode.bitDevices().contains(deviceCode)){
            return new BitDeviceItem(context,deviceCode,deviceNumber,deviceName);
        } else if (MCDeviceCode.wordDevices().contains(deviceCode)) {
            return  new WordDeviceItem(context,deviceCode,deviceNumber,deviceName);
        } else {
            return null;
        }

    }

}
