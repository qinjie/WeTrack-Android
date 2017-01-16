package com.example.hoanglong.wetrack;

import android.Manifest;
import android.app.Application;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

import com.example.hoanglong.wetrack.api.BeaconAPI;
import com.example.hoanglong.wetrack.api.BeaconLocation;
import com.example.hoanglong.wetrack.api.RetrofitUtils;
import com.example.hoanglong.wetrack.utils.Beacons;
import com.example.hoanglong.wetrack.utils.Patients;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;
import org.altbeacon.beacon.startup.BootstrapNotifier;
import org.altbeacon.beacon.startup.RegionBootstrap;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by hoanglong on 21-Dec-16.
 */

public class BeaconScanActivation extends Application implements BootstrapNotifier {
    private static final String TAG = ".MyApplicationName";
    private RegionBootstrap regionBootstrap;
    private BackgroundPowerSaver backgroundPowerSaver;
    private BeaconManager mBeaconmanager;

    private BeaconAPI beaconAPI;
    List<Patients> patientList = null;

    Location mLocation;
    LocationManager locationManager;

    ArrayList<Region> regionList = new ArrayList();
    ArrayList<Region> toRemove = new ArrayList();
    ArrayList<Region> toAdd = new ArrayList();
    final BootstrapNotifier tmp = this;
    private Handler mHandler;

    @Override
    public void onCreate() {
        super.onCreate();

        Toast.makeText(getBaseContext(), "onCreate beaconScanActivation", Toast.LENGTH_SHORT).show();


//        Identifier identifier = Identifier.parse(REGION_UUID2);
//        Identifier identifierx = Identifier.parse("58949");
//        Region region = new Region("green", identifier, identifierx, null);
//        regionList.add(region);
//
//        Identifier identifier2 = Identifier.parse(REGION_UUID2);
//        Identifier identifiery = Identifier.parse("52689");
//        Region region2 = new Region("blue", identifier2, identifiery, null);
//        regionList.add(region2);

//        Region region3 = new Region("AnyBeacon", null, null, null);
//        regionBootstrap = new RegionBootstrap(this, region3);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        beaconAPI = RetrofitUtils.get().create(BeaconAPI.class);
        beaconAPI.getPatientList().enqueue(new Callback<List<Patients>>() {
            @Override
            public void onResponse(Call<List<Patients>> call, Response<List<Patients>> response) {
                try {
                    patientList = response.body();

                    Gson gson = new Gson();
                    String jsonPatients = gson.toJson(patientList);
                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("patientList-WeTrack", jsonPatients);
                    editor.commit();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<List<Patients>> call, Throwable t) {
                sendNotification("Please turn on internet connection 1");
                Gson gson = new Gson();
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                String jsonPatients = sharedPref.getString("patientList-WeTrack", "");
                Type type = new TypeToken<List<Patients>>() {
                }.getType();
                patientList = gson.fromJson(jsonPatients, type);
            }
        });


        mHandler = new Handler();
        startRepeatingTask();

//        tmp = this;

//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {


//            }
//        }, 10000);


//        this.startService
//                (new Intent(this, BeaconMonitoringService.class));

    }

    @Override
    public void didDetermineStateForRegion(int status, Region region) {
        Log.i("Activation-determine", region.getUniqueId() + " : " + status);

    }

    @Override
    public void didEnterRegion(Region region) {
        //TODO
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        if (sharedPref.getLong("ExpiredDate", -1) < System.currentTimeMillis()) {
            SharedPreferences.Editor editor = sharedPref.edit();
//            editor.clear();
            editor.putString("listPatientsAndLocations-WeTrack2", "");
            editor.putLong("ExpiredDate", System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(44640));
            editor.apply();
        }


//        sendNotification("enter: " + region.getUniqueId() + " | " + mBeaconmanager.getMonitoredRegions().size());

//        this.startService
//                (new Intent(this, BeaconMonitoringService.class));
        if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);


        Date aDate = new Date();
        SimpleDateFormat curFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateObj = curFormatter.format(aDate);


        if (patientList != null && !patientList.equals("") && patientList.size() > 0 && mLocation != null) {

            String[] regionInfo = region.getUniqueId().split(";");
            Log.i("Service monitoring", regionInfo[0] + " | " + regionInfo[1]);

            for (final Patients patient : patientList) {
                for (Beacons aBeacon : patient.getPatientBeacon()) {
                    if (regionInfo[0].equals(patient.getId() + "") && regionInfo[1].equals(aBeacon.getUuid().toLowerCase()) && patient.getStatus() == 1 && aBeacon.getStatus() == 1) {

                        if (!checkInternetOn()) {
                            String firstBeaconIdentifiers = regionInfo[1] + aBeacon.getMajor() + aBeacon.getMinor();
                            SharedPreferences sharedPref2 = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                            SharedPreferences.Editor editor = sharedPref2.edit();
                            String oldData = sharedPref.getString("listPatientsAndLocations-WeTrack2", "");
                            editor.putString("listPatientsAndLocations-WeTrack2", firstBeaconIdentifiers + "," + mLocation.getLongitude() + "," + mLocation.getLatitude() + "," + dateObj + ";" + oldData);
                            editor.putLong("ExpiredDate", System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(44640));
                            editor.commit();
                        }

                        BeaconLocation aLocation = new BeaconLocation(aBeacon.getId(), patient.getId(), mLocation.getLongitude(), mLocation.getLatitude(), dateObj);

                        Gson gson = new GsonBuilder()
                                .setLenient()
                                .create();
                        JsonObject obj = gson.toJsonTree(aLocation).getAsJsonObject();

                        Call<JsonObject> call = beaconAPI.sendBeaconLocation("Bearer wRe82EIau4STc35oVBF8XyAfF2UVJM8u", "application/json", obj);
                        call.enqueue(new Callback<JsonObject>() {
                            @Override
                            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                                try {
//                                    sendNotification(patient.getFullname() + " | " + mBeaconmanager.getMonitoredRegions().size() + " | " + regionList.size());
                                    sendNotification("Sending info of " + patient.getFullname() + " successfully");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(Call<JsonObject> call, Throwable error) {
                            }
                        });
                    }
                }

            }
        } else {
            if (mLocation == null) {
                sendNotification("Please turn on location service");
            }
        }

    }

    @Override
    public void didExitRegion(Region region) {

        sendNotification("exit: " + region.getUniqueId() + " | " + mBeaconmanager.getMonitoredRegions().size());

//        this.stopService
//                (new Intent(this, BeaconMonitoringService.class));
    }


    private void sendNotification(String name) {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setContentTitle(name)
                        .setContentText(name)
                        .setSmallIcon(R.drawable.icon).setAutoCancel(true);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        Intent intent = new Intent(this, MainActivity.class);

        stackBuilder.addNextIntent(intent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        builder.setContentIntent(resultPendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(x++, builder.build());
    }

    int x = 0;


    private int mInterval = 20000;
    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {

            beaconAPI.getPatientList().enqueue(new Callback<List<Patients>>() {
                @Override
                public void onResponse(Call<List<Patients>> call, Response<List<Patients>> response) {
                    try {
                        patientList = response.body();

                        Gson gson = new Gson();
                        String jsonPatients = gson.toJson(patientList);
                        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("patientList-WeTrack", jsonPatients);
                        editor.commit();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<List<Patients>> call, Throwable t) {
//                    sendNotification("Please turn on internet connection 1");
                    Gson gson = new Gson();
                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                    String jsonPatients = sharedPref.getString("patientList-WeTrack", "");
                    Type type = new TypeToken<List<Patients>>() {
                    }.getType();
                    patientList = gson.fromJson(jsonPatients, type);
                }
            });

            if (patientList != null && !patientList.equals("") && patientList.size() > 0 && tmp != null) {
                for (Patients aPatient : patientList) {
                    for (Beacons aBeacon : aPatient.getPatientBeacon()) {
                        if (aPatient.getStatus() == 1 && aBeacon.getStatus() == 1 && aPatient.getPatientBeacon() != null && aPatient.getPatientBeacon().size() > 0) {
                            //if change region in this part, remember also change region below
                            String uuid = aBeacon.getUuid();
                            Identifier identifier = Identifier.parse(uuid);
                            Identifier identifier2 = Identifier.parse(String.valueOf(aBeacon.getMajor()));
                            Region region = new Region(aPatient.getId() + ";" + identifier, identifier, identifier2, null);
                            if (!regionList.contains(region)) {
                                regionList.add(region);
                            }
                        }
                    }

                }
            }


            regionBootstrap = new RegionBootstrap(tmp, regionList);

            mBeaconmanager = org.altbeacon.beacon.BeaconManager.getInstanceForApplication(getBaseContext());
            mBeaconmanager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));

            mBeaconmanager.setBackgroundMode(true);
            backgroundPowerSaver = new BackgroundPowerSaver(getBaseContext());

            mBeaconmanager.setBackgroundBetweenScanPeriod(60000l);
            mBeaconmanager.setBackgroundScanPeriod(10000l);

//            LocationListener locationListener = new LocationListener() {
//                public void onLocationChanged(Location location) {
//                    mLocation = location;
//                }
//
//                public void onStatusChanged(String provider, int status, Bundle extras) {
//                    Log.i("locationListener", "changed" + provider);
//                }
//
//                public void onProviderEnabled(String provider) {
//                    Log.i("locationListener", "enabled" + provider);
//                }
//
//                public void onProviderDisabled(String provider) {
//                    Log.i("locationListener", "disabled" + provider);
//                }
//            };

//            if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                return;
//            }
//
//            mLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            if (checkInternetOn()) {
                SharedPreferences sharedPref3 = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                SharedPreferences.Editor editor = sharedPref3.edit();
                String savedData = sharedPref3.getString("listPatientsAndLocations-WeTrack2", "");

                if (!savedData.equals("") && patientList != null) {
                    final String[] patientOffline = savedData.split(";");
                    for (int i = 0; i < patientOffline.length; i++) {
                        String[] patientInfoOffline = patientOffline[i].split(",");
                        if (savedData.contains(patientOffline[i] + ";")) {
                            savedData = savedData.replace(patientOffline[i] + ";", "");
                        }

                        if (patientList.size() > 0) {
                            for (final Patients patient : patientList) {
                                for (Beacons aBeacon : patient.getPatientBeacon()) {
                                    if (patient.getPatientBeacon() != null && patient.getPatientBeacon().size() > 0) {
                                        String patientBeaconIdentifiers = aBeacon.getUuid() + aBeacon.getMajor() + aBeacon.getMinor();
                                        if (patientInfoOffline[0].equals(patientBeaconIdentifiers) && patient.getStatus() == 1 && aBeacon.getStatus() == 1) {
                                            BeaconLocation aLocation = new BeaconLocation(aBeacon.getId(), patient.getId(), Double.parseDouble(patientInfoOffline[1]), Double.parseDouble(patientInfoOffline[2]), patientInfoOffline[3]);
                                            Gson gson = new GsonBuilder()
                                                    .setLenient()
                                                    .create();
                                            JsonObject obj = gson.toJsonTree(aLocation).getAsJsonObject();

                                            sendNotification(patient.getFullname() + " (offline): " + patientInfoOffline[3]);

                                            Call<JsonObject> call = beaconAPI.sendBeaconLocation("Bearer wRe82EIau4STc35oVBF8XyAfF2UVJM8u", "application/json", obj);
                                            call.enqueue(new Callback<JsonObject>() {
                                                @Override
                                                public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                                                    try {

                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<JsonObject> call, Throwable error) {
//                                                    sendNotification("Please turn on internet connection");
                                                }
                                            });

                                        }

                                    }
                                }

                            }
                        }
                    }
                    editor.putString("listPatientsAndLocations-WeTrack2", savedData);
                    editor.commit();
                }
            }

            mHandler.postDelayed(mStatusChecker, mInterval);

        }
    };

    void startRepeatingTask() {
        mStatusChecker.run();
    }

//    void stopRepeatingTask() {
//        mHandler.removeCallbacks(mStatusChecker);
//    }

    //Check the internet activity on or not
    //Return true if ON. False if OFF
    public boolean checkInternetOn() {
        ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        if (netInfo == null) {
            return false;
        } else {
            return true;
        }

    }
}