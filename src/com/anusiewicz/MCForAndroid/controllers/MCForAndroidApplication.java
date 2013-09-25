package com.anusiewicz.MCForAndroid.controllers;

import android.app.Application;
import android.content.Context;

/**
 * Created with IntelliJ IDEA.
 * User: Szymon Anusiewicz
 */
public class MCForAndroidApplication extends Application {

    private static final MCForAndroidApplication INSTANCE = new MCForAndroidApplication();
    private static final ConnectionManager mConnectionManager = new ConnectionManager();

    public static Context getAppContext() {
            return INSTANCE;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public static ConnectionManager getConnectionManager() {
        return mConnectionManager;
    }
}
