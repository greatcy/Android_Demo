package com.eli.simplestdemo;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
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
import com.eli.downloadlib.Storage;
import com.eli.fileselector.FileSelectorActivity;
import com.eli.simplestdemo.downloaded.DownloadedFragment;
import com.eli.simplestdemo.downloading.DownloadingFragment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

//TODO 增加一个中间网速进度条 http://mobile.51cto.com/android-534640.htm
public class MainActivity extends AppCompatActivity {
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private IFragmentInterface mCurFragment;
    private static final int REFRESH_SEC = 1000;

    private Handler handler = new Handler();

    private Runnable mRefreshTicks = new Runnable() {
        @Override
        public void run() {
            Storage.getInstance(MainActivity.this).update();
            if (mCurFragment != null)
                mCurFragment.getAdapter().notifyDataSetChanged();
            handler.postDelayed(mRefreshTicks, REFRESH_SEC);
        }
    };

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
        View fab = findViewById(R.id.fab);

        FragmentPagerAdapter adapter =
                new FragmentPagerAdapter(getSupportFragmentManager());

        List<Fragment> fragments = new ArrayList<>();
        final DownloadedFragment df = new DownloadedFragment();
        final DownloadingFragment dif = new DownloadingFragment();
        fragments.add(dif);
        fragments.add(df);
        adapter.setFragments(fragments);
        mCurFragment = dif;

        mViewPager.setAdapter(adapter);

        mTabLayout.setupWithViewPager(mViewPager);

//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FileSelectorActivity.getTorrentFile(MainActivity.this, new FileSelectorActivity.ICallBack() {
//                    @Override
//                    public void onGetFile(File file) {
//                        if (file != null) {
//                            API.createTask(MainActivity.this, file, new API.ICreateTaskCallBack() {
//                                @Override
//                                public void onComplete() {
//                                    Toast.makeText(MainActivity.this, R.string.tips_create_task_complete, Toast.LENGTH_SHORT).show();
//                                    if (dif.getAdapter() != null) {
//                                        dif.getAdapter().notifyDataSetChanged();
//                                    }
//                                }
//                            });
//                        }
//                    }
//                });
//            }
//        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0)
                    MainActivity.this.mCurFragment = dif;
                else {
                    MainActivity.this.mCurFragment = df;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
//        mRefreshTicks.run();
    }
}