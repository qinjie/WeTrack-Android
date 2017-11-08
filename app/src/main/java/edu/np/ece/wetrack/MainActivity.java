package edu.np.ece.wetrack;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

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
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.lang.reflect.Type;

import butterknife.BindView;
import butterknife.ButterKnife;
import edu.np.ece.wetrack.api.RetrofitUtils;
import edu.np.ece.wetrack.api.ServerAPI;
import edu.np.ece.wetrack.model.EmailInfo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static edu.np.ece.wetrack.BeaconScanActivation.detectedBeaconList;
import static edu.np.ece.wetrack.BeaconScanActivation.detectedPatientList;
//import static edu.np.ece.wetrack.BeaconScanActivation.missingPatientList;

//import static BeaconScanService.beaconManager;
//import static BeaconScanService.listBeacon;


public class MainActivity extends AppCompatActivity {

    private ServerAPI serverAPI;

    public static final int REQUEST_ENABLE_LOCATION = 1994;

    public static BeaconListAdapter beaconListAdapter;


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
    AccountHeader headerResult;


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
        String userRole = sharedPref.getString("userRole-WeTrack", "");
        try{
            if (!userRole.equals("5")) {
                if (account == null || account.getAvatarUrl() == null || account.getAvatarUrl() == "") {
                    profile = new ProfileDrawerItem().withName(account.getName()).withEmail(account.getEmail()).withIcon(R.drawable.default_avt);
                } else {
                    profile = new ProfileDrawerItem().withName(account.getName()).withEmail(account.getEmail()).withIcon(account.getAvatarUrl());
                }
            } else {
                profile = new ProfileDrawerItem().withName(account.getName()).withEmail(account.getEmail()).withIcon(R.drawable.default_avt);

            }

            headerResult.removeProfile(0);
            headerResult.addProfile(profile, 0);
        }catch(Exception e){
            e.printStackTrace();
        }



        adapterViewPager = new FragmentAdapter(getSupportFragmentManager(), userRole);
        adapterViewPager.getItem(0);
        viewPager.setAdapter(adapterViewPager);
        tabLayout.setupWithViewPager(viewPager);

        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            tabLayout.getTabAt(i).setIcon(icons[i]);
            tabLayout.getTabAt(i).setText(null);
        }

        tabLayout.setVisibility(View.VISIBLE);

        result.setSelection(0);
        result.closeDrawer();

        Intent detailIntent = getIntent();

        if (detailIntent != null) {


            String tmp = detailIntent.getStringExtra("whatParent");
            if (tmp != null) {

                if (tmp.equals("home")) {
                    TabLayout.Tab tab = tabLayout.getTabAt(0);
                    tab.select();
                    toolbar.setTitle("Missing Residents");
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, HomeFragment.newInstance("Home")).commit();
                }

                if (tmp.equals("detectedList")) {
                    TabLayout.Tab tab = tabLayout.getTabAt(1);
                    tab.select();
                    toolbar.setTitle("Nearby Residents");
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, BeaconListFragment.newInstance("Detected")).commit();
                    detailIntent.putExtra("isFromDetailActivity", false);
                }

                if (tmp.equals("relativeList")) {
                    TabLayout.Tab tab = tabLayout.getTabAt(2);
                    tab.select();
                    toolbar.setTitle("Relatives");
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, RelativesFragment.newInstance("Relative")).commit();
                }

            }
        } else {
            TabLayout.Tab tab = tabLayout.getTabAt(0);
            tab.select();
            toolbar.setTitle("Missing Residents");
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, HomeFragment.newInstance("Home1")).commit();
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        serverAPI = RetrofitUtils.get().create(ServerAPI.class);

        //initial google sign in
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleApiClient.connect();


        //[Start adjust the height of content with tabLayout for multiple device]
        //get height of screen
//        DisplayMetrics displaymetrics = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
//        final int height = displaymetrics.heightPixels;
//
//        //Get height of actionbar
//        TypedValue tv = new TypedValue();
//        getBaseContext().getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true);
//        final int actionBarHeight = getResources().getDimensionPixelSize(tv.resourceId);
//
//        // Gets layout
//        final CoordinatorLayout layout = (CoordinatorLayout) findViewById(R.id.activity_tab_layout);
//        // Gets the layout params that will allow you to resize the layout
//        final ViewGroup.LayoutParams params = layout.getLayoutParams();
//        // Changes the height and width to the specified *pixels*
//        params.height = height - (actionBarHeight * 4 / 3 + actionBarHeight * 4 / 19);
//        layout.setLayoutParams(params);
        //[End of adjusting]

        //Asking user for access location service
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_ENABLE_LOCATION);

        setSupportActionBar(toolbar);

        //Setup for account display on drawer
        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.singapore)
                .withSelectionListEnabledForSingleProfile(false)
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        return false;
                    }
                })
                .build();


        //Create item for drawer
        PrimaryDrawerItem home = new PrimaryDrawerItem().withIdentifier(0).withName("Homepage").withIcon(R.drawable.ic_home_black);
        PrimaryDrawerItem faq = new PrimaryDrawerItem().withIdentifier(1).withName("FAQ").withIcon(R.drawable.ic_help);
        PrimaryDrawerItem about = new PrimaryDrawerItem().withIdentifier(2).withName("About the app").withIcon(R.drawable.ic_info);
        PrimaryDrawerItem aboutUs = new PrimaryDrawerItem().withIdentifier(3).withName("About us").withIcon(R.drawable.ic_supervisor);
        SecondaryDrawerItem setting = new SecondaryDrawerItem().withIdentifier(4).withName("Setting").withIcon(R.drawable.ic_settings);
        SecondaryDrawerItem logout = new SecondaryDrawerItem().withIdentifier(5).withName("Logout").withIcon(R.drawable.ic_power);


        //Setup drawer
        result = new DrawerBuilder()
                .withActivity(this)
                .withTranslucentStatusBar(true)
                .withAccountHeader(headerResult)
                .withToolbar(toolbar)
                .addDrawerItems(home,
                        faq,
                        about,
                        aboutUs,
                        new DividerDrawerItem(),
                        setting,
                        logout)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        switch (position) {
                            case 1: {
                                //Adjust the size of content and tablayout back to normal
                                tabLayout.setVisibility(View.VISIBLE);

//                                params.height = height - (actionBarHeight * 4 / 3 + actionBarHeight * 2 / 19);
//                                layout.setLayoutParams(params);

                                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, HomeFragment.newInstance("Home")).commit();
                                result.closeDrawer();
                                TabLayout.Tab tab = tabLayout.getTabAt(0);
                                tab.select();
                                toolbar.setTitle("Missing Residents");
//                                btnSearch.setVisibility(View.VISIBLE);
                            }
                            break;
                            case 2: {
                                //Hide tablayout and increase height of content
                                tabLayout.setVisibility(View.GONE);

//                                params.height = height + (actionBarHeight * 4 / 3 + actionBarHeight * 2 / 19);
//                                layout.setLayoutParams(params);

                                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, FAQFragment.newInstance("FAQ")).commit();
                                result.closeDrawer();
                                toolbar.setTitle("FAQ");
//                                btnSearch.setVisibility(View.GONE);
                            }
                            break;
                            case 3: {
//                                params.height = height + (actionBarHeight * 4 / 3 + actionBarHeight * 2 / 19);
//                                layout.setLayoutParams(params);
//                                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, AboutFragment.newInstance("About")).commit();
//                                result.closeDrawer();
//                                toolbar.setTitle("About");
//                                tabLayout.setVisibility(View.GONE);

                                Intent intent = new Intent(getBaseContext(), AboutActivity.class);
                                intent.putExtra("fromWhat", "home");
                                startActivity(intent);
                                finish();

//                                btnSearch.setVisibility(View.GONE);
                            }
                            break;
                            case 4: {
//                                params.height = height + (actionBarHeight * 4 / 3 + actionBarHeight * 2 / 19);
//                                layout.setLayoutParams(params);
//                                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, AboutFragment.newInstance("About")).commit();
//                                result.closeDrawer();
//                                toolbar.setTitle("About");
//                                tabLayout.setVisibility(View.GONE);

                                Intent intent = new Intent(getBaseContext(), AboutUsActivity.class);
                                intent.putExtra("fromWhat", "home");
                                startActivity(intent);
                                finish();
//                                btnSearch.setVisibility(View.GONE);
                            }
                            break;
                            case 6: {
                                Intent intent = new Intent(getBaseContext(), SettingActivity.class);
                                intent.putExtra("fromWhat", "home");
                                startActivity(intent);
                                finish();
                            }
                            break;
                            case 7: {
                                Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                                        new ResultCallback<Status>() {
                                            @Override
                                            public void onResult(Status status) {
                                                result.setSelection(0);
                                                result.closeDrawer();
                                                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

                                                //Notify to server to remove registered device token of account
                                                String deviceToken = sharedPref.getString("deviceToken-WeTrack", "");
                                                String userID = sharedPref.getString("userID-WeTrack", "");
                                                JsonParser parser = new JsonParser();
                                                JsonObject obj = parser.parse("{\"token\": \"" + deviceToken + "\",\"user_id\": \"" + userID + "\"}").getAsJsonObject();
                                                serverAPI.deleteToken(obj).enqueue(new Callback<JsonObject>() {
                                                    @Override
                                                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                                                    }

                                                    @Override
                                                    public void onFailure(Call<JsonObject> call, Throwable t) {

                                                    }
                                                });

                                                //Remove all info of current account
                                                SharedPreferences.Editor editor = sharedPref.edit();
                                                editor.putString("userToken-WeTrack", "");
                                                editor.putString("userID-WeTrack", "");
                                                editor.putString("userRole-WeTrack", "");

                                                editor.commit();

                                                detectedBeaconList.clear();
                                                detectedPatientList.clear();

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
                if (position == 0 && result.getCurrentSelectedPosition() == 1) {
                    toolbar.setTitle("Missing Residents");
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, HomeFragment.newInstance("Home")).commit();
                }
                if (position == 1) {
                    toolbar.setTitle("Nearby Residents");
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, BeaconListFragment.newInstance("Beacon List")).commit();
                }
                if (position == 2) {
                    toolbar.setTitle("Relatives");
//                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, RelativesFragment.newInstance("Relatives List")).commit();
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, adapterViewPager.getItem(2)).commit();

                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });


//        String token = sharedPref.getString("userToken-WeTrack", "");
//        String userRole = sharedPref.getString("userRole-WeTrack", "");

//        adapterViewPager = new FragmentAdapter(getSupportFragmentManager(), userRole);
//        adapterViewPager.getItem(0);
//        viewPager.setAdapter(adapterViewPager);
//        tabLayout.setupWithViewPager(viewPager);
//
//        for (int i = 0; i < tabLayout.getTabCount(); i++) {
//            tabLayout.getTabAt(i).setIcon(icons[i]);
//            tabLayout.getTabAt(i).setText(null);
//        }

//        result.setSelection(0);
//        result.closeDrawer();

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

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String isScanning = sharedPref.getString("isScanning-WeTrack", "true");

        if (isScanning.equals("true")) {
            initBluetooth();
        }
        displayLocationSettingsRequest(getBaseContext());
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle item selection
//        switch (item.getItemId()) {
//            case R.id.menu_1:
//                Toast.makeText(getBaseContext(), "this is about us", Toast.LENGTH_SHORT).show();
//                return true;
//
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }

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
                        break;

                    case BluetoothAdapter.STATE_ON:
                        btnSearch.setImageResource(R.drawable.ic_pause);

                        break;
                }
            }


        }
    };

    //Make nearby beacon list become static
    public void logToDisplay() {
        runOnUiThread(new Runnable() {
            public void run() {
                beaconListAdapter.add(detectedPatientList, detectedBeaconList);
            }
        });
    }

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

        EventBus.getDefault().register(this);
        super.onResume();

    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onPause() {
        EventBus.getDefault().unregister(this);
        unregisterReceiver(broadcastReceiver);
        super.onPause();
    }


    final int EDIT_USER = 69;

    //Subscribe action for Event Bus
    @Subscribe
    public void onEvent(FragmentAdapter.OpenEvent event) {
//        EventBus.getDefault().unregister(this);
        Intent intent = new Intent(this, ResidentDetailActivity.class);
        intent.putExtra("patient", event.patient);
        intent.putExtra("position", event.position);
        intent.putExtra("fromWhat", event.from);
        startActivityForResult(intent, EDIT_USER);
        finish();
    }
}
