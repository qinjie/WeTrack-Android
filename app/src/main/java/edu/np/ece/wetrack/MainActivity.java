package edu.np.ece.wetrack;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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

import java.lang.reflect.Type;

import butterknife.BindView;
import butterknife.ButterKnife;
import edu.np.ece.wetrack.model.EmailInfo;

import static edu.np.ece.wetrack.BeaconScanActivation.detectedBeaconList;
import static edu.np.ece.wetrack.BeaconScanActivation.detectedPatientList;
//import static edu.np.ece.wetrack.BeaconScanActivation.missingPatientList;

//import static BeaconScanService.beaconManager;
//import static BeaconScanService.listBeacon;


public class MainActivity extends AppCompatActivity {
    public static final int REQUEST_ENABLE_LOCATION = 1994;
    //    public static ArrayAdapter<String> beaconListAdapter;
    public static BeaconListAdapter beaconListAdapter;

//    public static HomeAdapter homeAdapter;

//    public static RelativesAdapter relativeAdapter;

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
            R.drawable.ic_group
    };

    GoogleApiClient mGoogleApiClient;

    @Override
    protected void onStart() {
        super.onStart();

        Gson gson = new Gson();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String jsonPatients = sharedPref.getString("userAccount-WeTrack", "");
        Type type = new TypeToken<EmailInfo>() {
        }.getType();
        EmailInfo account = gson.fromJson(jsonPatients, type);
        IProfile profile;
        String token = sharedPref.getString("userToken-WeTrack", "");
        if (!token.equals("anonymous")) {
            profile = new ProfileDrawerItem().withName(account.getName()).withEmail(account.getEmail()).withIcon(account.getAvatarUrl());
        } else {
            profile = new ProfileDrawerItem().withName(account.getName()).withEmail(account.getEmail()).withIcon(R.drawable.default_avt);

        }
        headerResult.removeProfile(0);
        headerResult.addProfile(profile, 0);



        //tablayout
        adapterViewPager = new FragmentAdapter(getSupportFragmentManager(),token);
        adapterViewPager.getItem(0);
        viewPager.setAdapter(adapterViewPager);
        tabLayout.setupWithViewPager(viewPager);

        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            tabLayout.getTabAt(i).setIcon(icons[i]);
            tabLayout.getTabAt(i).setText(null);
        }
//        if (token.equals("anonymous") && tabLayout.getTabCount() == 3) {
//
//
//            tabLayout.removeTabAt(2);
//        } else {
//            if (tabLayout.getTabCount() == 2) {
//                tabLayout.newTab().setIcon(R.drawable.ic_group);
//            }
//        }
    }

    AccountHeader headerResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // [END configure_signin]

        // [START build_client]
        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleApiClient.connect();


        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;


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


//        Gson gson = new Gson();
//        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
//        String jsonPatients = sharedPref.getString("userAccount-WeTrack", "");
//        Type type = new TypeToken<EmailInfo>() {
//        }.getType();
//        EmailInfo account = gson.fromJson(jsonPatients, type);

        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.singapore)
                .withSelectionListEnabledForSingleProfile(false)
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        Toast.makeText(getBaseContext(), "ahihi", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                })
                .build();


        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("hahahah");

        PrimaryDrawerItem home = new PrimaryDrawerItem().withIdentifier(0).withName("Homepage").withIcon(R.drawable.ic_home_black);
        PrimaryDrawerItem faq = new PrimaryDrawerItem().withIdentifier(1).withName("FAQ").withIcon(R.drawable.ic_help);
        PrimaryDrawerItem about = new PrimaryDrawerItem().withIdentifier(2).withName("About the app").withIcon(R.drawable.ic_info);
        SecondaryDrawerItem setting = new SecondaryDrawerItem().withIdentifier(3).withName("Setting").withIcon(R.drawable.ic_settings);
        SecondaryDrawerItem logout = new SecondaryDrawerItem().withIdentifier(4).withName("Logout").withIcon(R.drawable.ic_power);


        result = new DrawerBuilder()
                .withActivity(this)
                .withTranslucentStatusBar(true)
                .withAccountHeader(headerResult)
                .withToolbar(toolbar)
                .addDrawerItems(home,
                        faq,
                        about,
                        new DividerDrawerItem(),
                        setting,
                        logout)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        switch (position) {
                            case 1: {

                                getSupportFragmentManager().beginTransaction().replace(R.id.activity_tab_layout, HomeFragment.newInstance("Home")).commit();
                                result.closeDrawer();
                                TabLayout.Tab tab = tabLayout.getTabAt(0);
                                tab.select();
                                toolbar.setTitle("Missing Residents");
                                tabLayout.setVisibility(View.VISIBLE);
                                btnSearch.setVisibility(View.VISIBLE);
                            }
                            break;
                            case 2: {
                                getSupportFragmentManager().beginTransaction().replace(R.id.activity_tab_layout, FAQFragment.newInstance("FAQ")).commit();
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
                            case 5: {

                            }
                            break;
                            case 6: {
                                Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                                        new ResultCallback<Status>() {
                                            @Override
                                            public void onResult(Status status) {
                                                result.setSelection(0);
                                                result.closeDrawer();
                                                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                                                SharedPreferences.Editor editor = sharedPref.edit();
                                                editor.putString("userToken-WeTrack", "");
                                                editor.putString("userID-WeTrack", "");
                                                editor.commit();
                                                Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                                                startActivity(intent);
                                            }
                                        });
                            }
                            break;

                        }

                        return true;
                    }
                })
                .build();



        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if (position == 0) {
                    toolbar.setTitle("Missing Residents");
                    getSupportFragmentManager().beginTransaction().replace(R.id.activity_tab_layout, HomeFragment.newInstance("Home")).commit();
                }
                if (position == 1){
                    toolbar.setTitle("Nearby Residents");
                    getSupportFragmentManager().beginTransaction().replace(R.id.activity_tab_layout, BeaconListFragment.newInstance("Beacon List")).commit();
                }
                if (position == 2){
                    toolbar.setTitle("Relatives");
                    getSupportFragmentManager().beginTransaction().replace(R.id.activity_tab_layout, RelativesFragment.newInstance("Relatives List")).commit();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });


        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String token = sharedPref.getString("userToken-WeTrack", "");
        adapterViewPager = new FragmentAdapter(getSupportFragmentManager(),token);
        adapterViewPager.getItem(0);
        viewPager.setAdapter(adapterViewPager);
        tabLayout.setupWithViewPager(viewPager);

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

        registerReceiver(broadcastReceiver, intentFilter);

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

        initBluetooth();

        displayLocationSettingsRequest(getBaseContext());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_1:
                Toast.makeText(getBaseContext(), "this is about us", Toast.LENGTH_SHORT).show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initBluetooth() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!bluetoothAdapter.isEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, 9);

        }
    }

    final int REQUEST_CHECK_SETTINGS = 0x1;

    private void displayLocationSettingsRequest(Context context) {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
//                        Log.i(TAG, "All location settings are satisfied.");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
//                        Log.i(TAG, "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");

                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            status.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
//                            Log.i(TAG, "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
//                        Log.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        break;
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {

            case REQUEST_ENABLE_LOCATION: {
            }
            break;

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        btnSearch.setImageResource(R.drawable.ic_play_arrow);
                        beaconListAdapter.notifyDataSetChanged();
//                        homeAdapter.notifyDataSetChanged();
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

        registerReceiver(broadcastReceiver, intentFilter);

        if (!bluetoothAdapter.isEnabled()) {
            btnSearch.setImageResource(R.drawable.ic_play_arrow);
        } else {
            btnSearch.setImageResource(R.drawable.ic_pause);
        }


//        listDevice.clear();
//        beaconListAdapter.notifyDataSetChanged();
//        bluetoothAdapter.startDiscovery();
        super.onResume();
    }

    @Override
    protected void onPause() {
        unregisterReceiver(broadcastReceiver);
        super.onPause();
    }

    public void logToDisplay() {
        runOnUiThread(new Runnable() {
            public void run() {
//                beaconListAdapter.notifyDataSetChanged();
//                listBeacon.add("hj");
                beaconListAdapter.add(detectedPatientList, detectedBeaconList);
//                homeAdapter.add(missingPatientList);

//                beaconListAdapter.setBeacons(listBeacon);
            }
        });
    }

//    public void logToDisplay2() {
//        runOnUiThread(new Runnable() {
//            public void run() {
////                beaconListAdapter.notifyDataSetChanged();
////                listBeacon.add("hj");
//                homeAdapter.add(missingPatientList);
////                beaconListAdapter.setBeacons(listBeacon);
//            }
//        });
//    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        if (isRefeshing) {
//            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
//            isRefeshing = false;
//        }
//        return super.onTouchEvent(event);
//    }
}
