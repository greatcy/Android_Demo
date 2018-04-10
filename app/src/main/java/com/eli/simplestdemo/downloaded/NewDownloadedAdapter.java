package com.eli.simplestdemo.downloaded;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.eli.downloadlib.Storage;
import com.eli.downloadlib.Utils;
import com.eli.simplestdemo.R;

import java.util.List;

import libtorrent.Libtorrent;

import static com.eli.simplestdemo.Const.LOG_TAG;

/**
 * Created by eli on 18-4-7.
 */

public class NewDownloadedAdapter extends RecyclerView.Adapter<NewDownloadedAdapter.ViewHolder> {
    private Context mContext;
    private List<Storage.Torrent> mData;
    private PopupWindow mOperatorMenu;

    private class MenuListener implements OnClickListener {
        private Storage.Torrent mTorrent;

        MenuListener(Storage.Torrent mTorrent) {
            this.mTorrent = mTorrent;
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.action_pause:
                    break;
                case R.id.action_play:
                    break;
                case R.id.action_delete:
                    Storage.getInstance(mContext).remove(mTorrent);
                    break;
                case R.id.tv_open:
                    break;
            }
            mOperatorMenu.dismiss();
        }
    }

    public NewDownloadedAdapter(Context paramContext) {
        this.mContext = paramContext;
    }

    private void setPOPMenu(final Storage.Torrent paramTorrent, final View paramView) {
        paramView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 用于PopupWindow的View
                View contentView = LayoutInflater.from(paramView.getContext()).inflate(R.layout.task_item_pop_menu, null, false);
                // 创建PopupWindow对象，其中：
                // 第一个参数是用于PopupWindow中的View，第二个参数是PopupWindow的宽度，
                // 第三个参数是PopupWindow的高度，第四个参数指定PopupWindow能否获得焦点
                mOperatorMenu = new PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
                // 设置PopupWindow的背景
                mOperatorMenu.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                // 设置PopupWindow是否能响应外部点击事件
                mOperatorMenu.setOutsideTouchable(true);
                // 设置PopupWindow是否能响应点击事件
                mOperatorMenu.setTouchable(true);
                // 显示PopupWindow，其中：
                // 第一个参数是PopupWindow的锚点，第二和第三个参数分别是PopupWindow相对锚点的x、y偏移
                mOperatorMenu.showAsDropDown(paramView, 0, 0);

                MenuListener listener = new MenuListener(paramTorrent);
                contentView.findViewById(R.id.action_pause).setOnClickListener(listener);
                contentView.findViewById(R.id.action_play).setOnClickListener(listener);
                contentView.findViewById(R.id.action_delete).setOnClickListener(listener);
                contentView.findViewById(R.id.tv_open).setOnClickListener(listener);
            }
        });


    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(NewDownloadedAdapter.ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        Storage.Torrent torrent = mData.get(position);

        holder.mTaskName.setText(torrent.name());
        holder.mTaskSize.setText(Utils.formatSize(mContext, Libtorrent.torrentBytesLength(torrent.t)));
        holder.mProgressBar.setProgress(torrent.getProgress());
        setPOPMenu(torrent, holder.optionMenu);

        int torrentStatus = Libtorrent.torrentStatus(torrent.t);
        switch (torrentStatus) {
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

    public int getItemCount() {
        if (this.mData != null) {
            return this.mData.size();
        }
        return 0;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public NewDownloadedAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                              int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.download_item_view, parent, false);
        // set the view's size, margins, paddings and layout parameters
        return new ViewHolder(v);
    }

    public void setData(List<Storage.Torrent> paramList) {
        this.mData = paramList;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ProgressBar mProgressBar;
        TextView mStatus;
        TextView mTaskName;
        TextView mTaskSize;
        View optionMenu;

        ViewHolder(View v) {
            super(v);
            this.optionMenu = v.findViewById(R.id.option_menu);
            this.mProgressBar = (ProgressBar) v.findViewById(R.id.progressBar);
            this.mTaskName = (TextView) v.findViewById(R.id.tv_task_name);
            this.mTaskSize = (TextView) v.findViewById(R.id.tv_file_size);
            this.mStatus = (TextView) v.findViewById(R.id.tv_status);
        }
    }
}
