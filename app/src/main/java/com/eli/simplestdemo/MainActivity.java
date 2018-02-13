package com.eli.simplestdemo;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.eli.simplestdemo.downed.DownloadedFragment;
import com.eli.simplestdemo.downloading.DownloadingFragment;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private ViewPager mViewPager;
    private TabLayout mTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mViewPager = findViewById(R.id.viewpager);
        mTabLayout = findViewById(R.id.tabs);

        BaseFragmentPagerAdapter adapter =
                new BaseFragmentPagerAdapter(getSupportFragmentManager());

        List<Fragment> fragments = new ArrayList<>();
        DownloadedFragment df=new DownloadedFragment();
        DownloadingFragment dif=new DownloadingFragment();
        fragments.add(df);
        fragments.add(dif);
        adapter.setFragments(fragments);

        mViewPager.setAdapter(adapter);

        mTabLayout.setupWithViewPager(mViewPager);
    }
}
