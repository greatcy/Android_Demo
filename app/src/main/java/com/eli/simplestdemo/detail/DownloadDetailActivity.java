package com.eli.simplestdemo.detail;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.eli.simplestdemo.R;

/**
 * Created by chenjunheng on 2018/4/15.
 */

public class DownloadDetailActivity extends AppCompatActivity {
    public final static long KEY_TASK_ID = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.download_detail_activity);
    }
}
