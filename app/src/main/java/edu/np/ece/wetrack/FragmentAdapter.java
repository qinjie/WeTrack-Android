package edu.np.ece.wetrack;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by hoanglong on 06-Dec-16.
 */

public class FragmentAdapter extends FragmentPagerAdapter {

    String user;

    public FragmentAdapter(FragmentManager fm, String user) {
        super(fm);
        this.user = user;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: // Fragment # 0 - This will show FirstFragment
                return HomeFragment.newInstance("");
            case 1:
                return BeaconListFragment.newInstance("");
            case 2:
                return RelativesFragment.newInstance("");
            default:
                return HomeFragment.newInstance("");

        }

    }


    @Override
    public int getCount() {
        if (user.equals("anonymous")) {
            return 2;
        }
        return 3;
    }


    @Override
    public CharSequence getPageTitle(int position) {
        return String.valueOf(position);
    }
}
