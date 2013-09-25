package com.anusiewicz.MCForAndroid.model;

import android.os.Environment;

/**
 * Created with IntelliJ IDEA.
 * User: Szymon Anusiewicz
 */
public class Constants {

    public static final String DEVICE_NAME_TAG = "device_name";
    public static final String DEVICE_TYPE_TAG = "device_type";
    public static final String DEVICE_NUMBER_TAG = "device_number";
    public static final String TITLE_TAG = "title";
    public static final String REFRESH_TIME_TAG = "refresh_time";
    public static final String DEVICES_TAG = "devices";
    public static final String SCREEN_NAME_TAG = "screen_name";

    public static final String FILES_DIRECTORY = Environment.getExternalStorageDirectory().getPath() + "/Android/data/com.szymonanusiewicz.MCForAndroid/files/";
}
