package edu.np.ece.wetrack;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import edu.np.ece.wetrack.model.Resident;

/**
 * Created by hoanglong on 06-Dec-16.
 */

public class FragmentAdapter extends FragmentPagerAdapter {

    String userRole;

    public FragmentAdapter(FragmentManager fm, String userRole) {
        super(fm);
        this.userRole = userRole;
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
        if (userRole.equals("5")) {
            return 2;
        }
        return 3;
    }


    @Override
    public CharSequence getPageTitle(int position) {
        return String.valueOf(position);
    }



    public static class OpenEvent {
        public final int position;
        public final Resident patient;
        public final String from;

        public OpenEvent(int position, Resident patient, String from) {
            this.position = position;
            this.patient = patient;
            this.from = from;
        }
    }
}
