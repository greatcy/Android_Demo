package com.eli.simplestdemo;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import java.util.List;

/**
 * Created by chenjunheng on 2018/2/12.
 */

public class FragmentPagerAdapter extends android.support.v4.app.FragmentPagerAdapter {
    private List<Fragment> mFragments;

    FragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public void setFragments(List<Fragment> fragments) {
        this.mFragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        if (mFragments != null && mFragments.size() > position) {
            return mFragments.get(position);
        }
        return null;
    }

    @Override
    public int getCount() {
        if (mFragments != null)
            return mFragments.size();
        return 0;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        Fragment f = getItem(position);
        if (f != null) {
            return ((IFragmentInterface) f).getTitle();
        }
        return super.getPageTitle(position);
    }
}
