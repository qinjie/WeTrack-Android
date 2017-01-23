package com.example.hoanglong.wetrack;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.hoanglong.wetrack.BeaconScanActivation.detectedBeaconList;
import static com.example.hoanglong.wetrack.BeaconScanActivation.detectedPatientList;
import static com.example.hoanglong.wetrack.BeaconScanActivation.missingPatientList;

//import static com.example.hoanglong.wetrack.BeaconScanService.beaconManager;
//import static com.example.hoanglong.wetrack.BeaconScanService.listBeacon;


public class MainActivity extends AppCompatActivity {
    public static final int REQUEST_ENABLE_LOCATION = 1994;
    //    public static ArrayAdapter<String> adapterDevice;
    public static BeaconListAdapter adapterDevice;

    public static HomeAdapter homeAdapter;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.tabLayout)
    TabLayout tabLayout;

    @BindView(R.id.viewpager)
    ViewPager viewPager;

    @BindView(R.id.btnSearch)
    FloatingActionButton btnSearch;

    Drawer result;

    FragmentPagerAdapter adapterViewPager;

    BluetoothAdapter bluetoothAdapter;

    private int[] icons = new int[]{
            R.drawable.ic_home,
            R.drawable.ic_place,
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


//        Intent in = new Intent(getBaseContext(), BeaconScanService.class);
//        getBaseContext().startService(in);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;

//        final TypedArray styledAttributes = getBaseContext().getTheme().obtainStyledAttributes(
//                new int[]{android.R.attr.actionBarSize});
//        int mActionBarSize = (int) styledAttributes.getDimension(0, 0);
//        styledAttributes.recycle();

        //Get height of actionbar
        TypedValue tv = new TypedValue();
        getBaseContext().getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true);
        int actionBarHeight = getResources().getDimensionPixelSize(tv.resourceId);

        // Gets layout
        CoordinatorLayout layout = (CoordinatorLayout) findViewById(R.id.activity_tab_layout);
        // Gets the layout params that will allow you to resize the layout
        ViewGroup.LayoutParams params = layout.getLayoutParams();
        // Changes the height and width to the specified *pixels*
        Resources r = getResources();
//        int px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, r.getDisplayMetrics());
        params.height = height - (actionBarHeight * 4 / 3 + actionBarHeight * 2 / 19);
        layout.setLayoutParams(params);

        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_ENABLE_LOCATION);


        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.singapore)
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        return false;
                    }
                })
                .build();

        PrimaryDrawerItem home = new PrimaryDrawerItem().withIdentifier(0).withName("Homepage").withIcon(R.drawable.ic_home_black);
        PrimaryDrawerItem faq = new PrimaryDrawerItem().withIdentifier(1).withName("FAQ").withIcon(R.drawable.ic_help);
        PrimaryDrawerItem about = new PrimaryDrawerItem().withIdentifier(2).withName("About").withIcon(R.drawable.ic_info);
        SecondaryDrawerItem setting = new SecondaryDrawerItem().withIdentifier(3).withName("Setting").withIcon(R.drawable.ic_settings);
//        SecondaryDrawerItem logout = new SecondaryDrawerItem().withIdentifier(4).withName("Exit").withIcon(R.drawable.ic_power);


        result = new DrawerBuilder()
                .withActivity(this)
                .withAccountHeader(headerResult)
                .withToolbar(toolbar)
                .addDrawerItems(home,
                        faq,
                        about,
                        new DividerDrawerItem(),
                        setting)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        switch (position) {
                            case 1: {
//                                Intent intent = new Intent(getBaseContext(), MainActivity.class);
//                                startActivityForResult(intent, 0);
                                getSupportFragmentManager().beginTransaction().replace(R.id.activity_tab_layout, HomeFragment.newInstance("About")).commit();
                                result.closeDrawer();
                                TabLayout.Tab tab = tabLayout.getTabAt(0);
                                tab.select();
                                toolbar.setTitle("Missing Residents");
                                tabLayout.setVisibility(View.VISIBLE);
                                btnSearch.setVisibility(View.VISIBLE);
                            }
                            break;
                            case 2: {
                                getSupportFragmentManager().beginTransaction().replace(R.id.activity_tab_layout, FAQFragment.newInstance("About")).commit();
                                result.closeDrawer();
                                toolbar.setTitle("FAQ");
                                tabLayout.setVisibility(View.GONE);
                                btnSearch.setVisibility(View.GONE);
                            }
                            break;
                            case 3: {
                                getSupportFragmentManager().beginTransaction().replace(R.id.activity_tab_layout, AboutFragment.newInstance("About")).commit();
                                result.closeDrawer();
                                toolbar.setTitle("About");
                                tabLayout.setVisibility(View.GONE);
                                btnSearch.setVisibility(View.GONE);
                            }
                            break;

                        }

                        return true;
                    }
                })
                .build();

        //tablayout
        adapterViewPager = new FragmentAdapter(getSupportFragmentManager());
        adapterViewPager.getItem(0);
        viewPager.setAdapter(adapterViewPager);
        tabLayout.setupWithViewPager(viewPager);


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if (position == 0) {
                    toolbar.setTitle("Missing Residents");
                    getSupportFragmentManager().beginTransaction().replace(R.id.activity_tab_layout, HomeFragment.newInstance("Home")).commit();
                } else {
                    toolbar.setTitle("Nearby Residents");
                    getSupportFragmentManager().beginTransaction().replace(R.id.activity_tab_layout, BeaconListFragment.newInstance("Beacon List")).commit();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            tabLayout.getTabAt(i).setIcon(icons[i]);
            tabLayout.getTabAt(i).setText(null);
        }


        Intent detailIntent = getIntent();
        if (detailIntent != null) {
            Bundle b = detailIntent.getExtras();
            if (b != null) {
                boolean tmp = b.getBoolean("isFromDetailActivity", false);
                if (tmp) {
                    TabLayout.Tab tab = tabLayout.getTabAt(1);
                    tab.select();
                    toolbar.setTitle("Nearby Residents");
                    getSupportFragmentManager().beginTransaction().replace(R.id.activity_tab_layout, BeaconListFragment.newInstance("hihi")).commit();
                    detailIntent.putExtra("isFromDetailActivity", false);
                } else {
                    TabLayout.Tab tab = tabLayout.getTabAt(0);
                    tab.select();
                    toolbar.setTitle("Missing Residents");
                    getSupportFragmentManager().beginTransaction().replace(R.id.activity_tab_layout, HomeFragment.newInstance("Home1")).commit();
                }
            } else {
                TabLayout.Tab tab = tabLayout.getTabAt(0);
                tab.select();
                toolbar.setTitle("Missing Residents");
                getSupportFragmentManager().beginTransaction().replace(R.id.activity_tab_layout, HomeFragment.newInstance("Home1")).commit();
            }
        } else {
            TabLayout.Tab tab = tabLayout.getTabAt(0);
            tab.select();
            toolbar.setTitle("Missing Residents");
            getSupportFragmentManager().beginTransaction().replace(R.id.activity_tab_layout, HomeFragment.newInstance("Home1")).commit();
        }

        IntentFilter intentFilter = new IntentFilter();

        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);

        registerReceiver(searchDevices, intentFilter);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initBluetooth();
                if (!bluetoothAdapter.isEnabled()) {
                    btnSearch.setImageResource(R.drawable.ic_play_arrow);
                } else {
                    bluetoothAdapter.disable();
                    btnSearch.setImageResource(R.drawable.ic_pause);
                }

            }
        });


        if (!bluetoothAdapter.isEnabled()) {
            btnSearch.setImageResource(R.drawable.ic_play_arrow);
        } else {
            btnSearch.setImageResource(R.drawable.ic_pause);
        }


    }

    private void initBluetooth() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!bluetoothAdapter.isEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, 9);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        switch (requestCode) {
//
//            case 101: {
//                getSupportFragmentManager().beginTransaction().replace(R.id.activity_tab_layout, BeaconListFragment.newInstance("Beacon List")).commit();
//
//            }
//            break;
//
//        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {

            case REQUEST_ENABLE_LOCATION: {
//                Intent in = new Intent(getBaseContext(), BeaconMonitoringService.class);
//                getBaseContext().startService(in);
//                Toast.makeText(getBaseContext(),"hahayyyyyyyy",Toast.LENGTH_SHORT).show();
            }
            break;

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private BroadcastReceiver searchDevices = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        btnSearch.setImageResource(R.drawable.ic_play_arrow);
                        adapterDevice.notifyDataSetChanged();
                        homeAdapter.notifyDataSetChanged();
                        break;

                    case BluetoothAdapter.STATE_ON:
                        btnSearch.setImageResource(R.drawable.ic_pause);

                        break;
                }
            }


        }
    };


    @Override
    public void onResume() {
        /*register broadcast*/
        IntentFilter intentFilter = new IntentFilter();

        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);

        registerReceiver(searchDevices, intentFilter);

        if (!bluetoothAdapter.isEnabled()) {
            btnSearch.setImageResource(R.drawable.ic_play_arrow);
        } else {
            btnSearch.setImageResource(R.drawable.ic_pause);
        }


//        listDevice.clear();
//        adapterDevice.notifyDataSetChanged();
//        bluetoothAdapter.startDiscovery();
        super.onResume();
    }

    @Override
    protected void onPause() {
        unregisterReceiver(searchDevices);
        super.onPause();
    }

    public void logToDisplay() {
        runOnUiThread(new Runnable() {
            public void run() {
//                adapterDevice.notifyDataSetChanged();
//                listBeacon.add("hj");
                adapterDevice.add(detectedPatientList, detectedBeaconList);
//                homeAdapter.add(missingPatientList);

//                adapterDevice.setBeacons(listBeacon);
            }
        });
    }

    public void logToDisplay2() {
        runOnUiThread(new Runnable() {
            public void run() {
//                adapterDevice.notifyDataSetChanged();
//                listBeacon.add("hj");
                homeAdapter.add(missingPatientList);
//                adapterDevice.setBeacons(listBeacon);
            }
        });
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        if (isRefeshing) {
//            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
//            isRefeshing = false;
//        }
//        return super.onTouchEvent(event);
//    }
}
