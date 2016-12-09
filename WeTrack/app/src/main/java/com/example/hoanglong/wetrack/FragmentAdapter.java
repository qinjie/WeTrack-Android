package com.example.hoanglong.wetrack;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by hoanglong on 06-Dec-16.
 */

public class FragmentAdapter extends FragmentPagerAdapter {

    public FragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: // Fragment # 0 - This will show FirstFragment
                return HomeFragment.newInstance(String.valueOf(position));
            case 1: // Fragment # 0 - This will show FirstFragment different title
                return BeaconListFragment.newInstance(String.valueOf(position));
        }

        return HomeFragment.newInstance(String.valueOf(position));
    }


    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return String.valueOf(position);
    }
}
