package com.eli.simplestdemo.setting;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Environment;

import com.eli.simplestdemo.DLApplication;

/**
 * Created by chenjunheng on 2018/4/10.
 */

public class SettingConfig {
    private static SettingConfig mInstance;

    //TODO add features
    private boolean mOnlyWifi, mTorrentDlg, mKeepScreenOn;
    private String mStoreFolder;

    private final String SP_NAME = "settingSP";
    private final String SP_KEY_ONLY_WIFI = "sp_key_only_wifi";
    private final String SP_KEY_TORRENT_DLG = "sp_key_torrent_dlg";
    private final String SP_KEY_KEEP_SCREEN_ON = "sp_key_keep_screen_on";
    private final String SP_KEY_STORE_FOLDER = "storage_path";


    private SettingConfig() {
    }

    public synchronized static SettingConfig getInstance() {
        if (mInstance == null) {
            mInstance = new SettingConfig();
        }

        return mInstance;
    }

    public void init() {
        //store all properties
        SharedPreferences sharedPreferences = DLApplication.getApplication().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);

        mKeepScreenOn = sharedPreferences.getBoolean(SP_KEY_KEEP_SCREEN_ON, false);
        mOnlyWifi = sharedPreferences.getBoolean(SP_KEY_ONLY_WIFI, false);
        mTorrentDlg = sharedPreferences.getBoolean(SP_KEY_TORRENT_DLG, false);
        mStoreFolder = sharedPreferences.getString(SP_KEY_STORE_FOLDER,
                Environment.getExternalStorageDirectory().getAbsolutePath() +
                        DLApplication.getApplication().getString(com.eli.downloadlib.R.string.app_name));
    }

    public boolean isOnlyWifi() {
        return mOnlyWifi;
    }

    public boolean isTorrentDlg() {
        return mTorrentDlg;
    }

    public boolean isKeepScreenOn() {
        return mKeepScreenOn;
    }

    public String getStoreFolder() {
        return mStoreFolder;
    }

    public void setOnlyWifi(boolean mOnlyWifi) {
        this.mOnlyWifi = mOnlyWifi;
        SharedPreferences sharedPreferences = DLApplication.
                getApplication().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(SP_KEY_ONLY_WIFI,mOnlyWifi)
        .apply();
    }

    public void setTorrentDlg(boolean mTorrentDlg) {
        this.mTorrentDlg = mTorrentDlg;
        SharedPreferences sharedPreferences = DLApplication.
                getApplication().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(SP_KEY_TORRENT_DLG,mTorrentDlg)
                .apply();
    }

    public void setKeepScreenOn(boolean mKeepScreenOn) {
        this.mKeepScreenOn = mKeepScreenOn;
        SharedPreferences sharedPreferences = DLApplication.
                getApplication().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(SP_KEY_KEEP_SCREEN_ON,mKeepScreenOn)
                .apply();
    }

    public void setStoreFolder(String mStoreFolder) {
        this.mStoreFolder = mStoreFolder;
    }
}
