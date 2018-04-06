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

import libtorrent.Libtorrent;

import static com.eli.simplestdemo.Const.LOG_TAG;

/**
 * Created by chenjunheng on 2018/2/12.
 */
class DownloadingAdapter extends RecyclerView.Adapter<DownloadingAdapter.ViewHolder> {
    private Context mContext;

    public DownloadingAdapter(Context context) {
        this.mContext = context;
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        TextView mTaskName, mTaskSize, mStatus;
        ProgressBar mProgressBar;

        ViewHolder(View v) {
            super(v);
        }
    }


    // Create new views (invoked by the layout manager)
    @Override
    public DownloadingAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                            int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_text_view, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        vh.mProgressBar = (ProgressBar) v.findViewById(R.id.progressBar);
        vh.mTaskName = (TextView) v.findViewById(R.id.tv_task_name);
        vh.mTaskSize = (TextView) v.findViewById(R.id.tv_file_size);
        vh.mStatus = (TextView) v.findViewById(R.id.tv_status);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        Storage.Torrent torrent = Storage.getInstance(mContext).torrent(position);

        holder.mTaskName.setText(torrent.name());
        holder.mTaskSize.setText(Utils.formatSize(mContext, Libtorrent.torrentBytesLength(torrent.t)));
        holder.mProgressBar.setProgress(torrent.getProgress());

        int torrentStatus = Libtorrent.torrentStatus(torrent.t);
        switch (torrentStatus){
            case Libtorrent.StatusChecking:
                holder.mStatus.setText("checking");
                break;
            case Libtorrent.StatusPaused:
                holder.mStatus.setText("pause");
                break;
            case Libtorrent.StatusQueued:
                holder.mStatus.setText("queue");
                break;
            case Libtorrent.StatusDownloading:
                holder.mStatus.setText("downloading");
                break;
            case Libtorrent.StatusSeeding:
                holder.mStatus.setText("seeding");
                break;
        }

        Log.d(LOG_TAG, "download progress:" + torrent.getProgress());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return Storage.getInstance(mContext).count();
    }
}