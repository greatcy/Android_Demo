package com.eli.simplestdemo.download;

import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.eli.simplestdemo.R;

/**
 * Created by chenjunheng on 2018/4/15.
 */

public class NotificationManager {
    private static final int MAX_VALUE = 100;

    public void updateNotification(int taskID, int progress, Context context, String taskName, String speedInfo) {
        final android.app.NotificationManager mNotifyManager =
                (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        if (!TextUtils.isEmpty(taskName)) {
            mBuilder.setContentTitle(taskName);
        }
        if (!TextUtils.isEmpty(speedInfo)) {
            mBuilder.setSubText(speedInfo);
        }

        if (progress < 100) {
            mBuilder.setProgress(MAX_VALUE, progress, false);
        } else {
            mBuilder.setProgress(0, 0, false);// Removes the progress bar
        }

        if (mNotifyManager != null)
            mNotifyManager.notify(taskID, mBuilder.build());
    }

    public void createNotification(int taskID, Context context, String taskName, String speedInfo) {
        final android.app.NotificationManager mNotifyManager =
                (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setContentTitle(taskName)
                .setContentText(speedInfo)
                .setSmallIcon(R.mipmap.ic_launcher);

        if (mNotifyManager != null)
            mNotifyManager.notify(taskID, mBuilder.build());
    }
}
