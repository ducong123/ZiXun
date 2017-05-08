package com.nainaiwang.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.nainaiwang.bean.HomePageChannels;

import java.util.List;

/**
 * Created by Administrator on 2016/12/28.
 */
public class HomePageChannelAdapter extends FragmentPagerAdapter {

    private List<Fragment> fragmentList;
    private List<HomePageChannels> stringList;

    public HomePageChannelAdapter(FragmentManager fm, List<Fragment> fragmentList, List<HomePageChannels> stringList) {
        super(fm);
        this.fragmentList = fragmentList;
        this.stringList = stringList;
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return stringList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return stringList.get(position).getName();
    }
}
