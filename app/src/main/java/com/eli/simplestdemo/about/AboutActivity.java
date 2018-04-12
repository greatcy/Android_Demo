package com.eli.simplestdemo.about;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.widget.TextView;

import com.eli.simplestdemo.BuildConfig;
import com.eli.simplestdemo.R;

/**
 * Created by chenjunheng on 2018/4/12.
 */

public class AboutActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_activity);

        TextView versionInfo = (TextView) findViewById(R.id.tv_about_v);
        String versionValue = String.format(getString(R.string.about_format), getString(R.string.app_name), BuildConfig.VERSION_NAME);

        versionInfo.setText(versionValue);
    }
}
