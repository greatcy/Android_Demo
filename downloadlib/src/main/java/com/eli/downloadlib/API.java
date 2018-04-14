package com.eli.downloadlib;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

import com.github.axet.androidlibrary.widgets.OptimizationPreferenceCompat;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by chenjunheng on 2018/2/27.
 */
//TODO all download should instead by my download lib
@Deprecated
public class API {
    public static final String TAG = "DLLibTag";

    private static Thread initThread;
    private static final ArrayList<Runnable> initArray = new ArrayList<>();
    private static final Handler handler = new Handler();
    private static boolean mHasInit;

    public interface ICreateTaskCallBack {
        void onComplete();
    }

    /**
     * must init before download torrent!
     *
     * @param runAfterInit
     */
    public synchronized static void init(final Context context, Runnable runAfterInit) {
        if (mHasInit && runAfterInit != null) {
            runAfterInit.run();
            return;
        }

        if (runAfterInit != null)
            initArray.add(runAfterInit);

        if (initThread != null)
            return;
        initThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Storage.getInstance(context).create();

                for (Runnable r : initArray) {
                    handler.post(r);
                }
                initArray.clear();
                initThread = null;

                mHasInit = true;
            }
        }, "Init Thread");
        initThread.start();
    }

    public static void createTask(Context context, File file, ICreateTaskCallBack callBack) {
        try {
            byte[] buf = FileUtils.readFileToByteArray(file);
            addTorrentFromBytes(context, buf);
            callBack.onComplete();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void addTorrentFromBytes(Context context, byte[] bytes) {
        Storage.getInstance(context).addTorrentFromBytes(bytes);
    }

    public static com.github.axet.androidlibrary.app.Storage getStorage(Context context) {
        return Storage.getInstance(context);
    }
}
