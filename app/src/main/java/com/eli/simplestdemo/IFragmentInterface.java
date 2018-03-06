package com.eli.simplestdemo;

import android.support.v7.widget.RecyclerView;
import android.widget.BaseAdapter;

/**
 * Created by chenjunheng on 2018/2/13.
 */

public interface IFragmentInterface {
    String getTitle();

    RecyclerView.Adapter getAdapter();
}
