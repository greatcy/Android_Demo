package com.eli.simplestdemo.downloaded;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.eli.simplestdemo.R;
import com.eli.simplestdemo.ActionModeCallback;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by chenjunheng on 2018/2/12.
 */
class DownloadedAdapter extends RecyclerView.Adapter<DownloadedAdapter.ViewHolder> implements ActionModeCallback.ActionModeReCallBack {
    private static final String TAG = DownloadedAdapter.class.getSimpleName();

    //TODO actionMode 应该归属Activity
    private String[] mDataSet;
    private ActionMode actionMode;
    private Set<Integer> mSelectedItemPos = new HashSet<>();
    private boolean isInActionMode;

    @Override
    public void onExitActionMode() {
        this.actionMode = null;
        this.isInActionMode = false;
        mSelectedItemPos.clear();
        Log.d(TAG, "onExitActionMode ");
    }

    @Override
    public void onEnterActionMode() {
        isInActionMode = true;
        Log.d(TAG, "onEnterActionMode ");
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mTaskName;
        public CheckBox mCheckBox;
        public ProgressBar pb;
        public View optionMenu;

        public ViewHolder(View v) {
            super(v);
            mTaskName = (TextView) v.findViewById(R.id.tv_task_name);
            mCheckBox = (CheckBox) v.findViewById(R.id.checkBox);
            pb = (ProgressBar) v.findViewById(R.id.progressBar);
            optionMenu = v.findViewById(R.id.option_menu);
            pb.setProgress(88);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public DownloadedAdapter(String[] myDataset) {
        mDataSet = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public DownloadedAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                           int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_text_view, parent, false);
        // set the view's size, margins, paddings and layout parameters
        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        Log.d(TAG, "onBindViewHolder " + position);

        holder.mTaskName.setText(mDataSet[position]);
        if (isInActionMode) {
            holder.mCheckBox.setVisibility(View.VISIBLE);
            if (mSelectedItemPos.contains(position)) {
                holder.mCheckBox.setChecked(true);
            } else {
//                holder.mCheckBox.setChecked(false);
            }
        } else {
            holder.mCheckBox.setVisibility(View.INVISIBLE);
//            holder.mCheckBox.setChecked(false);
        }

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (actionMode != null) {
                    return false;
                }

                Log.d(TAG, "onBindViewHolder onLongClick actionMode == null" + position);
                isInActionMode = true;
                mSelectedItemPos.add(position);
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                ActionModeCallback tamc = new ActionModeCallback(DownloadedAdapter.this);
                actionMode = activity.startActionMode(tamc);
                holder.mCheckBox.setChecked(true);
                return false;
            }
        });

        holder.optionMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 用于PopupWindow的View
                View contentView = LayoutInflater.from(holder.optionMenu.getContext()).inflate(R.layout.task_item_pop_menu, null, false);
                // 创建PopupWindow对象，其中：
                // 第一个参数是用于PopupWindow中的View，第二个参数是PopupWindow的宽度，
                // 第三个参数是PopupWindow的高度，第四个参数指定PopupWindow能否获得焦点
                PopupWindow window = new PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
                // 设置PopupWindow的背景
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                // 设置PopupWindow是否能响应外部点击事件
                window.setOutsideTouchable(true);
                // 设置PopupWindow是否能响应点击事件
                window.setTouchable(true);
                // 显示PopupWindow，其中：
                // 第一个参数是PopupWindow的锚点，第二和第三个参数分别是PopupWindow相对锚点的x、y偏移
                window.showAsDropDown(holder.optionMenu, 0, 0);
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataSet.length;
    }
}