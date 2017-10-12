package com.example.carlos.assignment_one;


import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by Carlos on 17/9/23.
 */

public class CGFragmentPagerAdapter extends FragmentPagerAdapter {

    private String[] mTitles = new String[]{"Log", "History", "Score","Settings"};
    private ArrayList<Fragment> mFragmentList = null;

    public CGFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }
    public CGFragmentPagerAdapter(FragmentManager mFragmentManager,
                                     ArrayList<Fragment> fragmentList) {
        super(mFragmentManager);
        mFragmentList = fragmentList;
    }
//    public CGFragmentPagerAdapter(FragmentManager mFragmentManager,
//                                     ArrayList<Fragment> fragmentList, String[] titles) {
//        super(mFragmentManager);
//        mFragmentList = fragmentList;
//        this.mTitles = titles;
//    }

    @Override
    public Fragment getItem(int position) {
        //if (position <mFragmentList.size()) {
            return mFragmentList.get(position);
        //}
        //return mFragmentList.get(0);
    }

    @Override
    public int getCount() {
        return mTitles.length;
    }


    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles[position];
    }

}
