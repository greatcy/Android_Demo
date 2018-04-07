package com.eli.simplestdemo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.eli.downloadlib.API;
import com.eli.simplestdemo.downloaded.DownloadedFragment;
import com.eli.simplestdemo.downloading.DownloadingFragment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

//TODO 增加一个中间网速进度条 http://mobile.51cto.com/android-534640.htm
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ViewPager mViewPager;
    private TabLayout mTabLayout;

    final DownloadedFragment df = new DownloadedFragment();
    final DownloadingFragment dif = new DownloadingFragment();

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
        switch (item.getItemId()) {
            case R.id.action_settings:

                return true;
            case R.id.action_about:

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
                //TODO add file selector
//                FileSelectorActivity.getTorrentFile(MainActivity.this, new FileSelectorActivity.ICallBack() {
//                    @Override
//                    public void onGetFile(File file) {
//                        if (file != null) {
//                            //TODO
//                        } else {
//                            //TODO test
//                            file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+
//                                    File.separator+
//                                    "test.torrent");
//                            if (file.exists()) {
//                                API.createTask(MainActivity.this, file, new API.ICreateTaskCallBack() {
//                                    @Override
//                                    public void onComplete() {
//                                        Toast.makeText(MainActivity.this, R.string.tips_create_task_complete, Toast.LENGTH_SHORT).show();
//                                        if (dif.getAdapter() != null) {
//                                            dif.getAdapter().notifyDataSetChanged();
//                                        }
//                                    }
//                                });
//                            }
//                        }
//                    }
//                });

                final File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +
                        File.separatorChar + "test.torrent");

                if (file.exists()) {
                    requestRWPermission(new Runnable() {
                        @Override
                        public void run() {
                            API.createTask(MainActivity.this, file, new API.ICreateTaskCallBack() {
                                @Override
                                public void onComplete() {
                                    Toast.makeText(MainActivity.this, R.string.tips_create_task_complete, Toast.LENGTH_SHORT).show();
                                    if (dif.getAdapter() != null) {
                                        dif.getAdapter().notifyDataSetChanged();
                                    }
                                }
                            });
                        }
                    });
                }
                break;
            case R.id.torrent_magnet_button:

                break;

        }
    }

    private final int REQUEST_PERMISSION_CODE = 100;
    private Runnable mGrantedPermissionHolder;

    private void requestRWPermission(Runnable holder) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            int readPermissioin = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            int writePerission = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            if (readPermissioin != PackageManager.PERMISSION_GRANTED ||
                    writePerission != PackageManager.PERMISSION_GRANTED) {
                this.mGrantedPermissionHolder = holder;
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE
                        , Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_CODE);
            }
            else{
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
}
