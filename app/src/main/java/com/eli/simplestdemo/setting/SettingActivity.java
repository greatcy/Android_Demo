package com.eli.simplestdemo.setting;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.eli.downloadlib.API;
import com.eli.fileselector.OpenFileDialog;
import com.eli.simplestdemo.Const;
import com.eli.simplestdemo.MainActivity;
import com.eli.simplestdemo.R;

import java.io.File;

/**
 * Created by chenjunheng on 2018/4/10.
 */

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.action_setting);
        }

        //init set location path
        ViewGroup vg = (ViewGroup) findViewById(R.id.setting_location_path);
        TextView tvTitle = (TextView) vg.findViewById(R.id.tv_title);
        TextView tvSubtitle = (TextView) vg.findViewById(R.id.tv_subtitle);
        tvTitle.setText(getString(R.string.setting_download_location_title));
        tvSubtitle.setText(getString(R.string.setting_download_location));
        vg.setOnClickListener(this);

        //init open dialog
        vg = (ViewGroup) findViewById(R.id.setting_open_dialog);
        tvTitle = (TextView) vg.findViewById(R.id.tv_title);
        tvSubtitle = (TextView) vg.findViewById(R.id.tv_subtitle);
        tvTitle.setText(getString(R.string.setting_open_select_dlg_title));
        tvSubtitle.setText(getString(R.string.setting_open_select_dlg));
        Switch switchWidget = (Switch) vg.findViewById(R.id.sw);
        switchWidget.setChecked(SettingConfig.getInstance().isTorrentDlg());
        switchWidget.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                SettingConfig.getInstance().setTorrentDlg(isChecked);
            }
        });

        //init wifi only
        vg = (ViewGroup) findViewById(R.id.setting_wifi_only);
        tvTitle = (TextView) vg.findViewById(R.id.tv_title);
        tvSubtitle = (TextView) vg.findViewById(R.id.tv_subtitle);
        tvTitle.setText(getString(R.string.setting_download_only_wifi_title));
        tvSubtitle.setText(getString(R.string.setting_download_only_wifi));
        switchWidget = (Switch) vg.findViewById(R.id.sw);
        switchWidget.setChecked(SettingConfig.getInstance().isOnlyWifi());
        switchWidget.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                SettingConfig.getInstance().setOnlyWifi(isChecked);
            }
        });


        //init keep screen on
        vg = (ViewGroup) findViewById(R.id.setting_keep_screen_on);
        tvTitle = (TextView) vg.findViewById(R.id.tv_title);
        tvSubtitle = (TextView) vg.findViewById(R.id.tv_subtitle);
        tvTitle.setText(getString(R.string.setting_keep_screen_on_title));
        tvSubtitle.setText(getString(R.string.setting_keep_screen_on));
        switchWidget = (Switch) vg.findViewById(R.id.sw);
        switchWidget.setChecked(SettingConfig.getInstance().isKeepScreenOn());
        switchWidget.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                SettingConfig.getInstance().setKeepScreenOn(isChecked);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.setting_location_path:
                final OpenFileDialog dialog = new OpenFileDialog(SettingActivity.this, OpenFileDialog.DIALOG_TYPE.FOLDER_DIALOG, false);
                dialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface d, int which) {
                        File f = dialog.getCurrentPath();
                        if (f != null && f.exists()) {
                            Log.d(Const.LOG_TAG, "select file path:" + f.getAbsolutePath());

                        }
                    }
                });
                dialog.show();
                break;
        }
    }
}
