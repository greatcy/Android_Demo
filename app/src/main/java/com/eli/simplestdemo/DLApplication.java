package com.eli.simplestdemo;

import android.app.Application;

import com.eli.downloadlib.API;
import com.eli.simplestdemo.setting.SettingConfig;

/**
 * Created by chenjunheng on 2018/2/13.
 */

public class DLApplication extends Application {
    private static Application application;

    public DLApplication() {
        application = this;
    }

    public static Application getApplication() {
        return application;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        UIRefreshAgent.getInstance(this).startUpdateLooper();

        SettingConfig.getInstance().init();
    }
}
