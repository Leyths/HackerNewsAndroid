package com.leyths.hn.app;

import android.app.Application;
import android.content.Context;

public class HNApplication extends Application {
    private static Context appContext;

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = getApplicationContext();
    }

    public static Context get() {
        return appContext;
    }
}
