package com.leyths.hn.app;

import android.util.Log;

public class Logger {

    private Logger() {
        //
    }

    public static void d(String TAG, String msg) {
        Log.d(TAG, msg);
    }

    public static void e(String TAG, String msg) {
        Log.e(TAG, msg);
    }

    public static void e(String TAG, String msg, Throwable t) {
        Log.e(TAG, msg, t);
    }

    public static void e(String TAG, Throwable t) {
        Log.e(TAG, "", t);
    }
}
