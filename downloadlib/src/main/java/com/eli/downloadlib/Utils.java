package com.eli.downloadlib;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

import libtorrent.Libtorrent;

/**
 * Created by chenjunheng on 2018/2/28.
 */

public class Utils {
    public static String formatSize(Context context, long s) {
        if (s > 0.1 * 1024 * 1024 * 1024) {
            float f = s / 1024f / 1024f / 1024f;
            return context.getString(com.github.axet.androidlibrary.R.string.size_gb, f);
        } else if (s > 0.1 * 1024 * 1024) {
            float f = s / 1024f / 1024f;
            return context.getString(com.github.axet.androidlibrary.R.string.size_mb, f);
        } else {
            float f = s / 1024f;
            return context.getString(com.github.axet.androidlibrary.R.string.size_kb, f);
        }
    }

    public static String formatFree(Context context, long free, long d, long u) {
        return context.getString(R.string.free, formatSize(context, free),
                formatSize(context, d) + context.getString(R.string.per_second),
                formatSize(context, u) + context.getString(R.string.per_second));
    }

    static public String formatDuration(Context context, long diff) {
        int diffMilliseconds = (int) (diff % 1000);
        int diffSeconds = (int) (diff / 1000 % 60);
        int diffMinutes = (int) (diff / (60 * 1000) % 60);
        int diffHours = (int) (diff / (60 * 60 * 1000) % 24);
        int diffDays = (int) (diff / (24 * 60 * 60 * 1000));

        String str = "";

        if (diffDays > 0)
            str = diffDays + context.getString(com.github.axet.androidlibrary.R.string.days_symbol) + " " + formatTime(diffHours) + ":" + formatTime(diffMinutes) + ":" + formatTime(diffSeconds);
        else if (diffHours > 0)
            str = formatTime(diffHours) + ":" + formatTime(diffMinutes) + ":" + formatTime(diffSeconds);
        else
            str = formatTime(diffMinutes) + ":" + formatTime(diffSeconds);

        return str;
    }

    public static String formatTime(int tt) {
        return String.format("%02d", tt);
    }

    public static boolean connectionsAllowed(Context context) {
        final SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(context);
        Libtorrent.setDefaultAnnouncesList(shared.getString(Const.PREFERENCE_ANNOUNCE, ""));

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null&&isConnectedWifi(context)) { // connected to the internet
            return true;
        }
        return false;
    }

    public static boolean isConnectedWifi(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) { // connected to the internet
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                return true;
            }
        }
        return false;
    }
}
