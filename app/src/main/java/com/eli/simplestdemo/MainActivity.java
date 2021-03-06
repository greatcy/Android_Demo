package com.eli.simplestdemo;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.eli.downloadlib.API;
import com.eli.downloadlib.Storage;
import com.eli.fileselector.OpenFileDialog;
import com.eli.simplestdemo.about.AboutActivity;
import com.eli.simplestdemo.download.MagnetCreator;
import com.eli.simplestdemo.download.widget.DialProgress;
import com.eli.simplestdemo.downloaded.DownloadedFragment;
import com.eli.simplestdemo.downloading.DownloadingFragment;
import com.eli.simplestdemo.setting.SettingActivity;
import com.eli.simplestdemo.setting.SettingConfig;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import libtorrent.Libtorrent;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ViewPager mViewPager;
    private TabLayout mTabLayout;

    final DownloadedFragment df = new DownloadedFragment();
    final DownloadingFragment dif = new DownloadingFragment();

    private FloatingActionsMenu fam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        API.init(this, new Runnable() {
            @Override
            public void run() {
                init();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.normal_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.action_settings:
                intent = new Intent(this, SettingActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_about:
                intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                return true;
            case R.id.action_exit:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void init() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mTabLayout = (TabLayout) findViewById(R.id.tabs);

        View addTorrent = findViewById(R.id.torrent_add_button);
        addTorrent.setOnClickListener(this);

        View addMagnet = findViewById(R.id.torrent_magnet_button);
        addMagnet.setOnClickListener(this);

        fam = (FloatingActionsMenu) findViewById(R.id.fab);
        DialProgress mSpeedProgress = (DialProgress) findViewById(R.id.dial_progress_bar);
        UIRefreshAgent.getInstance(this).setTotalSeepProgress(mSpeedProgress);

        initViewpager();
    }

    private void initViewpager() {
        FragmentPagerAdapter adapter =
                new FragmentPagerAdapter(getSupportFragmentManager());

        List<Fragment> fragments = new ArrayList<>();
        fragments.add(dif);
        fragments.add(df);
        adapter.setFragments(fragments);
        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.torrent_add_button:
                requestRWPermission(new Runnable() {
                    @Override
                    public void run() {
                        final OpenFileDialog dialog = new OpenFileDialog(MainActivity.this, OpenFileDialog.DIALOG_TYPE.FILE_DIALOG, false);
                        //TODO set default folder
                        dialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface d, int which) {
                                File f = dialog.getCurrentPath();
                                if (f != null && f.exists()) {
                                    Log.d(Const.LOG_TAG, "select file path:" + f.getAbsolutePath());
                                    API.createTask(MainActivity.this, f, new API.ICreateTaskCallBack() {
                                        @Override
                                        public void onComplete() {
                                            Toast.makeText(MainActivity.this, R.string.tips_create_task_complete, Toast.LENGTH_SHORT).show();
                                            if (dif.getAdapter() != null) {
                                                dif.getAdapter().notifyDataSetChanged();
                                            }
                                        }
                                    });
                                }
                            }
                        });
                        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                Log.d(Const.LOG_TAG, "onCancel");
                            }
                        });
                        dialog.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d(Const.LOG_TAG, "setNegativeButton onCancel");
                            }
                        });
                        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface d) {
                                Log.d(Const.LOG_TAG, "onDismiss");
                            }
                        });
                        dialog.show();
                    }
                });
                break;
            case R.id.torrent_magnet_button:
                requestRWPermission(new Runnable() {
                    @Override
                    public void run() {
                        MagnetCreator creator = new MagnetCreator();
                        creator.createMagnet(MainActivity.this, new MagnetCreator.ICallBack() {
                            @Override
                            public void onCreateMagnet(String magnetUrl) {
                                Log.d(Const.LOG_TAG, "magnet url: " + magnetUrl);
                                if (!TextUtils.isEmpty(magnetUrl)) {
                                    List<String> m = Storage.getInstance(MainActivity.this).splitMagnets(magnetUrl);
                                    for (String s : m) {
                                        Storage.getInstance(MainActivity.this).addMagnet(s);
                                    }
                                    Toast.makeText(MainActivity.this, R.string.tips_create_task_complete, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
                break;
        }
        fam.collapse();
    }

    private Runnable mGrantedPermissionHolder;

    private void requestRWPermission(Runnable holder) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            int readPermissioin = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            int writePerission = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            if (readPermissioin != PackageManager.PERMISSION_GRANTED ||
                    writePerission != PackageManager.PERMISSION_GRANTED) {
                this.mGrantedPermissionHolder = holder;
                int REQUEST_PERMISSION_CODE = 100;
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE
                        , Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_CODE);
            } else {
                holder.run();
            }
        } else {
            mGrantedPermissionHolder.run();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        boolean getPermission = true;

        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                getPermission = false;
            }
        }

        if (getPermission && mGrantedPermissionHolder != null) {
            mGrantedPermissionHolder.run();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (SettingConfig.getInstance().isKeepScreenOn()) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }
}
