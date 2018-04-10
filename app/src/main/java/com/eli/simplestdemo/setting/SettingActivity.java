package com.eli.simplestdemo.setting;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.eli.simplestdemo.R;

/**
 * Created by chenjunheng on 2018/4/10.
 */

public class SettingActivity extends AppCompatActivity implements View.OnClickListener{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        myToolbar.setTitle(R.string.action_setting);
        setSupportActionBar(myToolbar);

        //init set location path
        ViewGroup vg= (ViewGroup) findViewById(R.id.setting_location_path);
        TextView tvTitle= (TextView) vg.findViewById(R.id.tv_title);
        TextView tvSubtitle= (TextView) vg.findViewById(R.id.tv_subtitle);
        tvTitle.setText(getString(R.string.setting_download_location_title));
        tvSubtitle.setText(getString(R.string.setting_download_location));
        vg.setOnClickListener(this);

        //init open dialog
        vg= (ViewGroup) findViewById(R.id.setting_open_dialog);
        tvTitle= (TextView) vg.findViewById(R.id.tv_title);
        tvSubtitle= (TextView) vg.findViewById(R.id.tv_subtitle);
        tvTitle.setText(getString(R.string.setting_open_select_dlg_title));
        tvSubtitle.setText(getString(R.string.setting_open_select_dlg));
        Switch switchWidget= (Switch) vg.findViewById(R.id.sw);
        switchWidget.setChecked(SettingConfig.getInstance().isTorrentDlg());
        switchWidget.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                SettingConfig.getInstance().setTorrentDlg(isChecked);
            }
        });

        //init wifi only
        vg= (ViewGroup) findViewById(R.id.setting_wifi_only);
        tvTitle= (TextView) vg.findViewById(R.id.tv_title);
        tvSubtitle= (TextView) vg.findViewById(R.id.tv_subtitle);
        tvTitle.setText(getString(R.string.setting_download_only_wifi_title));
        tvSubtitle.setText(getString(R.string.setting_download_only_wifi));
        switchWidget= (Switch) vg.findViewById(R.id.sw);
        switchWidget.setChecked(SettingConfig.getInstance().isOnlyWifi());
        switchWidget.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                SettingConfig.getInstance().setOnlyWifi(isChecked);
            }
        });


        //init keep screen on
        vg= (ViewGroup) findViewById(R.id.setting_keep_screen_on);
        tvTitle= (TextView) vg.findViewById(R.id.tv_title);
        tvSubtitle= (TextView) vg.findViewById(R.id.tv_subtitle);
        tvTitle.setText(getString(R.string.setting_keep_screen_on_title));
        tvSubtitle.setText(getString(R.string.setting_keep_screen_on));
        switchWidget= (Switch) vg.findViewById(R.id.sw);
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
        switch (view.getId()){
            case R.id.setting_location_path:
                //TODO open dialog
                break;
        }
    }
}
