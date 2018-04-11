package com.eli.simplestdemo.downloading;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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
 * Created by chenjunheng on 2018/2/12.
 */
class DownloadingAdapter extends RecyclerView.Adapter<DownloadingAdapter.ViewHolder> {

    private Context mContext;
    private List<Storage.Torrent> mData;
    private PopupWindow mOperatorMenu;

    private class MenuListener implements View.OnClickListener {
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
            }
            mOperatorMenu.dismiss();
        }
    }

    public DownloadingAdapter(Context paramContext) {
        this.mContext = paramContext;
    }

    private void setPOPMenu(final Storage.Torrent paramTorrent, final View paramView) {
        paramView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 用于PopupWindow的View
                View contentView = LayoutInflater.from(paramView.getContext()).inflate(R.layout.downloading_item_menu, null, false);
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

                DownloadingAdapter.MenuListener listener = new DownloadingAdapter.MenuListener(paramTorrent);
                contentView.findViewById(R.id.action_pause).setOnClickListener(listener);
                contentView.findViewById(R.id.action_play).setOnClickListener(listener);
                contentView.findViewById(R.id.action_delete).setOnClickListener(listener);
            }
        });


    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(DownloadingAdapter.ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        Storage.Torrent torrent = mData.get(position);

        holder.mTaskName.setText(torrent.name());
        holder.mTaskSize.setText(Utils.formatSize(mContext, Libtorrent.torrentBytesLength(torrent.t)));
        holder.mProgressBar.setProgress(torrent.getProgress());
        setPOPMenu(torrent, holder.optionMenu);
        holder.mDownloadStatus.setText(torrent.status());

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
    public DownloadingAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                            int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.downloading_item_view, parent, false);
        // set the view's size, margins, paddings and layout parameters
        return new DownloadingAdapter.ViewHolder(v);
    }

    public void setData(List<Storage.Torrent> paramList) {
        this.mData = paramList;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ProgressBar mProgressBar;
        TextView mDownloadStatus;
        TextView mTaskName;
        TextView mTaskSize;
        View optionMenu;

        ViewHolder(View v) {
            super(v);
            this.optionMenu = v.findViewById(R.id.option_menu);
            this.mProgressBar = (ProgressBar) v.findViewById(R.id.progressBar);
            this.mTaskName = (TextView) v.findViewById(R.id.tv_task_name);
            this.mTaskSize = (TextView) v.findViewById(R.id.tv_file_size);
            this.mDownloadStatus = (TextView) v.findViewById(R.id.tv_speed);
        }
    }
}