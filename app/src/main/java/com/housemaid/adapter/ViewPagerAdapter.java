package com.housemaid.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by fluper on 1/6/18.
 */

public class ViewPagerAdapter extends FragmentPagerAdapter {
    private ArrayList<Fragment> fragmentArrayList = new ArrayList<>();
    private ArrayList<String> tabtitle = new ArrayList<>();

    public void addfragment(Fragment fragment, String tabTitle) {
        this.fragmentArrayList.add(fragment);
        this.tabtitle.add(tabTitle);
    }


    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);

    }

    @Override
    public Fragment getItem(int position) {// Returns the fragment to display for that page
        return fragmentArrayList.get(position);
    }

    @Override
    public int getCount() {  // Returns total number of pages
        return fragmentArrayList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {// Returns the page title for the top indicator
        return tabtitle.get(position);
    }
}
