package com.eli.simplestdemo;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.eli.downloadlib.API;
import com.eli.fileselector.FileSelectorActivity;
import com.eli.simplestdemo.downloaded.DownloadedFragment;
import com.eli.simplestdemo.downloading.DownloadingFragment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private ViewPager mViewPager;
    private TabLayout mTabLayout;

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

    private void init() {
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        View fab = findViewById(R.id.fab);

        FragmentPagerAdapter adapter =
                new FragmentPagerAdapter(getSupportFragmentManager());

        List<Fragment> fragments = new ArrayList<>();
        DownloadedFragment df = new DownloadedFragment();
        final DownloadingFragment dif = new DownloadingFragment();
        fragments.add(dif);
        fragments.add(df);
        adapter.setFragments(fragments);

        mViewPager.setAdapter(adapter);

        mTabLayout.setupWithViewPager(mViewPager);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileSelectorActivity.getTorrentFile(MainActivity.this, new FileSelectorActivity.ICallBack() {
                    @Override
                    public void onGetFile(File file) {
                        if (file != null) {
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
                    }
                });
            }
        });
    }
}
