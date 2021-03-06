package com.leyths.hn.app;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

public class EventBus {
    private static final String TAG = EventBus.class.getSimpleName();
    private static Bus bus;

    private EventBus() {
        // Cannot instantiate
    }

    public static Bus getBus() {
        if (bus == null)
            bus = new Bus(ThreadEnforcer.ANY);
        return bus;
    }

    public static void post(Object event) {
        try {
            getBus().post(event);
        } catch (RuntimeException e) {
            Logger.e(TAG, e);
        }
    }

    public static boolean register(Object o) {
        if (o == null)
            return false;

        getBus().register(o);

        return true;
    }

    public static boolean unregister(Object o) {
        if (o == null)
            return false;
        try {
            getBus().unregister(o);
        } catch (IllegalArgumentException ex) {
            Logger.e(TAG, ex);
            return false;
        }

        return true;
    }
}
