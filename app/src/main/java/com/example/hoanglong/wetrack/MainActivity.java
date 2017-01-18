package com.example.hoanglong.wetrack;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.hoanglong.wetrack.BeaconScanService.listBeacon;
import static com.example.hoanglong.wetrack.BeaconScanService.listBeaconAndRange;

//import static com.example.hoanglong.wetrack.BeaconScanService.beaconManager;
//import static com.example.hoanglong.wetrack.BeaconScanService.listBeacon;
import static com.example.hoanglong.wetrack.BeaconScanActivation.detectedBeaconList;
import static com.example.hoanglong.wetrack.BeaconScanActivation.detectedPatientList;


public class MainActivity extends AppCompatActivity {
    public static final int REQUEST_ENABLE_LOCATION = 1994;
    //    public static ArrayAdapter<String> adapterDevice;
    public static BeaconListAdapter adapterDevice;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.tabLayout)
    TabLayout tabLayout;

    @BindView(R.id.viewpager)
    ViewPager viewPager;

    @BindView(R.id.btnSearch)
    FloatingActionButton btnSearch;

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
        getSupportFragmentManager().beginTransaction().replace(R.id.activity_tab_layout, HomeFragment.newInstance("Welcome to We Track\nfgdf\n" +
                "fgdf\n" +
                "fgdf\n" +
                "fgdf\n" +
                "fgdf\n" +
                "fgdf\n" +
                "fgdf\n" +
                "fgdf\n" +
                "fgdf\n" +
                "fgdf\n" +
                "fgdf\n" +
                "fgdf\n" +
                "fgdf\n" +
                "fgdf\n" +
                "fgdf\n" +
                "fgdf\n" +
                "fgdf\n" +
                "fgdf\n" +
                "fgdf\n" +
                "fgdf\n" +
                "fgdf\n" +
                "fgdf\n" +
                "fgdf\n" +
                "fgdf\n" +
                "fgdf\n" +
                "fgdf\n" +
                "fgdf\n" +
                "fgdf\n" +
                "fgdf\n" +
                "fgdf\n" +
                "fgdf\n" +
                "fgdf\n" +
                "fgdf\n" +
                "fgdf\n" +
                "fgdf\n" +
                "fgdf\n" +
                "fgdf\n" +
                "fgdf\n" +
                "fgdf\n" +
                "fgdf\n" +
                "fgdf\n" +
                "fgdf\n" +
                "fgdf\n" +
                "fgdf\n" +
                "fgdf")).commit();


//        Intent in = new Intent(getBaseContext(), BeaconScanService.class);
//        getBaseContext().startService(in);

        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_ENABLE_LOCATION);


        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.singapore)
                .addProfiles(
                        new ProfileDrawerItem().withName("Long Pham")
                                .withEmail("longpham@gmail.com").withIcon(getResources().getDrawable(R.drawable.my_avt))
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        return false;
                    }
                })
                .build();

        PrimaryDrawerItem home = new PrimaryDrawerItem().withIdentifier(0).withName("Homepage").withIcon(R.drawable.ic_home_black);
        PrimaryDrawerItem info = new PrimaryDrawerItem().withIdentifier(1).withName("Change email and password").withIcon(R.drawable.ic_account);
        PrimaryDrawerItem myBeaconList = new PrimaryDrawerItem().withIdentifier(2).withName("My beacon list").withIcon(R.drawable.ic_list);
        SecondaryDrawerItem setting = new SecondaryDrawerItem().withIdentifier(3).withName("Setting").withIcon(R.drawable.ic_settings);
        SecondaryDrawerItem logout = new SecondaryDrawerItem().withIdentifier(4).withName("Logout").withIcon(R.drawable.ic_power);

        Drawer result = new DrawerBuilder()
                .withActivity(this)
                .withAccountHeader(headerResult)
                .withToolbar(toolbar)
                .addDrawerItems(home,
                        info,
                        myBeaconList,
                        new DividerDrawerItem(),
                        setting,
                        logout)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        // do something with the clicked item :D
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
                    getSupportFragmentManager().beginTransaction().replace(R.id.activity_tab_layout, HomeFragment.newInstance("Home")).commit();
                } else {
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

        switch (requestCode) {

            case REQUEST_ENABLE_LOCATION: {
//                Intent in = new Intent(getBaseContext(), BeaconMonitoringService.class);
//                getBaseContext().startService(in);
//                Toast.makeText(getBaseContext(),"hahaxxxx",Toast.LENGTH_SHORT).show();
            }
            break;

        }
        super.onActivityResult(requestCode, resultCode, data);
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
                adapterDevice.add(detectedPatientList,detectedBeaconList);
//                adapterDevice.setBeacons(listBeacon);
            }
        });
    }


}
