package com.eli.simplestdemo;

import android.content.Context;

import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import com.eli.downloadlib.Storage;
import com.eli.downloadlib.Utils;
import com.eli.simplestdemo.download.widget.DialProgress;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import libtorrent.Libtorrent;

/**
 * Created by eli on 18-4-7.
 */

public class UIRefreshAgent {
    public static int MONITOR_DOWNLOADED_FLAG = 2;
    public static int MONITOR_DOWNLOADING_FLAG = 1;
    private static UIRefreshAgent mInstance;
    private final int REFRESH_GAP = 1000;
    private Context mContext;
    private Set<WeakReference<ICallBack>> mDownloadedMonitors;
    private List<Storage.Torrent> mDownloadedTorrents;
    private Set<WeakReference<ICallBack>> mDownloadingMonitors;
    private List<Storage.Torrent> mDownloadingTorrents;

    private WeakReference<DialProgress> mTotalSeepProgress;

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

    public void setTotalSeepProgress(DialProgress totalSeepProgress) {
        this.mTotalSeepProgress = new WeakReference<>(totalSeepProgress);
    }

    private void update() {
        //update total speed info 网络测速刷新
        Storage.getInstance(this.mContext).update();

        if (mTotalSeepProgress != null && mTotalSeepProgress.get() != null) {
            final Utils.speedInfo speedInfo = Utils.getSpeedInfo(mContext, Storage.getInstance(mContext).getDownloadSpeed());

            new Handler(mContext.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    mTotalSeepProgress.get().setValue(speedInfo.speedValue, speedInfo.speedUnit);
                }
            });
        }

        //upload all active task
        for (int i = 0; i < Storage.getInstance(this.mContext).count(); i++) {
            Storage.Torrent t = Storage.getInstance(this.mContext).torrent(i);
            if (Libtorrent.torrentActive(t.t)) {
                t.update();
            }
        }

        int count = Storage.getInstance(this.mContext).count();
        boolean isDLChanged = false;
        if (count == 0) {
            if (this.mDownloadingTorrents.size() != 0) {
                this.mDownloadingTorrents.clear();
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
                        this.mDownloadedTorrents.add(torrent);
                        this.mDownloadingTorrents.remove(torrent);
                    }
                } else if (torrent.getProgress() < 100) {
                    if (!this.mDownloadingTorrents.contains(torrent)) {
                        this.mDownloadingTorrents.add(torrent);
                    }
                }
            }
        }

        //TODO don't update downloading frequently
        notify2Callbacks(this.mDownloadingMonitors, this.mDownloadingTorrents);

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
