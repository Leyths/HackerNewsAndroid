package com.leyths.hn.ui;

import android.text.format.DateUtils;

import com.leyths.hn.R;
import com.leyths.hn.app.HNApplication;

import java.util.Date;

public class DateHelper {

    private DateHelper() {
        //
    }

    public static String dateToRelativeTime(Date date) {
        long minsAgo = dateToMinsAgo(date);

        final int HOUR = 60;
        final int DAY = HOUR * 24;
        final int WEEK = DAY * 7;
        final int YEAR = DAY * 365;

        if(minsAgo <= 1) {
            return HNApplication.get().getResources().getString(R.string.moments_ago);
        }

        if(minsAgo > 1 && minsAgo < HOUR) {
            return HNApplication.get().getResources().getString(R.string.mins_ago, minsAgo);
        }

        if(minsAgo >= HOUR && minsAgo < DAY) {
            return HNApplication.get().getResources().getString(R.string.hours_ago, minsAgo / HOUR);
        }

        if(minsAgo >= DAY && minsAgo < WEEK) {
            return HNApplication.get().getResources().getString(R.string.days_ago, minsAgo / DAY);
        }

        if(minsAgo >= WEEK && minsAgo < YEAR) {
            return HNApplication.get().getResources().getString(R.string.weeks_ago, minsAgo / WEEK);
        }

        return HNApplication.get().getResources().getString(R.string.years_ago, minsAgo / YEAR);
    }

    private static long dateToMinsAgo(Date date) {
        long ageMins = new Date().getTime() - date.getTime();
        return ageMins / DateUtils.MINUTE_IN_MILLIS;
    }
}
