package com.eli.simplestdemo.downloading;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.eli.downloadlib.Storage;
import com.eli.downloadlib.Utils;
import com.eli.simplestdemo.R;
import com.eli.simplestdemo.downloaded.NewDownloadedAdapter;

import libtorrent.Libtorrent;

import static com.eli.simplestdemo.Const.LOG_TAG;

/**
 * Created by chenjunheng on 2018/2/12.
 * //TODO don't extends NewDownloadedAdapter
 */
class DownloadingAdapter extends NewDownloadedAdapter {

    public DownloadingAdapter(Context paramContext) {
        super(paramContext);
    }
}