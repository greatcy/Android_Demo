package com.eli.simplestdemo;

import android.content.Context;

import android.os.Handler;
import android.util.Log;

import com.eli.downloadlib.Storage;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by eli on 18-4-7.
 */

public class UIRefreshAgent {
    public static int MONITOR_DOWNLOADED_FLAG = 2;
    public static int MONITOR_DOWNLOADING_FLAG = 1;
    private static UIRefreshAgent mInstance;
    private final int REFRESH_GAP = 2000;
    private Context mContext;
    private Set<WeakReference<ICallBack>> mDownloadedMonitors;
    private List<Storage.Torrent> mDownloadedTorrents;
    private Set<WeakReference<ICallBack>> mDownloadingMonitors;
    private List<Storage.Torrent> mDownloadingTorrents;

    private UIRefreshAgent(Context paramContext) {
        this.mContext = paramContext.getApplicationContext();
        this.mDownloadedMonitors = new HashSet();
        this.mDownloadingMonitors = new HashSet();
        this.mDownloadingTorrents = new ArrayList();
        this.mDownloadedTorrents = new ArrayList();
    }

    public static UIRefreshAgent getInstance(Context paramContext) {
        if (mInstance == null) {
            mInstance = new UIRefreshAgent(paramContext);
        }
        return mInstance;
    }

    private void notify2Callbacks(Set<WeakReference<ICallBack>> paramSet, final List<Storage.Torrent> paramList) {
        for (final WeakReference<ICallBack> localWeakReference : paramSet) {
            if ((localWeakReference != null) && (localWeakReference.get() != null)) {
                new Handler(this.mContext.getMainLooper()).post(new Runnable() {
                    public void run() {
                        localWeakReference.get().onListChange(paramList);
                    }
                });
            }
        }
    }

    private void update() {
        int count = Storage.getInstance(this.mContext).count();
        boolean isDLChanged = false;
        boolean isDLIChanged = false;
        if (count == 0) {
            if (this.mDownloadingTorrents.size() != 0) {
                this.mDownloadingTorrents.clear();
                isDLIChanged = true;
            }
            if (this.mDownloadedTorrents.size() != 0) {
                isDLChanged = true;
            }
        }

        for (int i = 0; i < count; i++) {
            Storage.Torrent torrent = Storage.getInstance(this.mContext).torrent(i);
            if (torrent != null) {
                if (torrent.getProgress() == 100) {
                    if (!this.mDownloadedTorrents.contains(torrent)) {
                        isDLChanged = true;
                        isDLIChanged = true;
                        this.mDownloadedTorrents.add(torrent);
                        this.mDownloadingTorrents.remove(torrent);
                    }
                } else if (torrent.getProgress() < 100) {
                    if (!this.mDownloadingTorrents.contains(torrent)) {
                        isDLIChanged = true;
                        this.mDownloadingTorrents.add(torrent);
                    }
                }
            }
        }
        if (isDLIChanged) {
            notify2Callbacks(this.mDownloadingMonitors, this.mDownloadingTorrents);
        }
        if (isDLChanged) {
            notify2Callbacks(this.mDownloadedMonitors, this.mDownloadedTorrents);
        }
    }

    public void registerMonitor(ICallBack paramICallBack, int paramInt) {
        if (paramInt == MONITOR_DOWNLOADED_FLAG) {
            this.mDownloadedMonitors.add(new WeakReference(paramICallBack));
            paramICallBack.onListChange(this.mDownloadedTorrents);
        }
        while (paramInt != MONITOR_DOWNLOADING_FLAG) {
            return;
        }
        this.mDownloadingMonitors.add(new WeakReference(paramICallBack));
        paramICallBack.onListChange(this.mDownloadingTorrents);
    }

    public void startUpdateLooper() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                for (; ; ) {
                    try {
                        sleep(REFRESH_GAP);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    UIRefreshAgent.this.update();
                }
            }
        }.start();
    }

    public interface ICallBack {
        void onListChange(List<Storage.Torrent> paramList);
    }

}
