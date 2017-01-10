package com.example.hoanglong.wetrack;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

import com.example.hoanglong.wetrack.api.BeaconAPI;
import com.example.hoanglong.wetrack.api.BeaconLocation;
import com.example.hoanglong.wetrack.api.RetrofitUtils;
import com.example.hoanglong.wetrack.utils.Patients;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by hoanglong on 06-Jan-17.
 */

public class BeaconMonitoringService extends Service implements BeaconConsumer {
    public static BeaconManager beaconManager;

    private BeaconAPI beaconAPI;
    List<Patients> patientList = null;

    Location mLocation;
    LocationManager locationManager;

    ArrayList<Region> regionList = new ArrayList();
    ArrayList<Region> toRemove = new ArrayList();
    ArrayList<Region> toAdd = new ArrayList();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //TODO
        //check lai vu allow location
        Toast.makeText(getBaseContext(), "after hahayyyyyyyy", Toast.LENGTH_SHORT).show();
        onCreate();
        return Service.START_STICKY;
    }

    @Override
    public void onCreate() {

        super.onCreate();

        mHandler = new Handler();
        startRepeatingTask();

        beaconManager = org.altbeacon.beacon.BeaconManager.getInstanceForApplication(this);
//        // By default the AndroidBeaconLibrary will only find AltBeacons.  If you wish to make it
//        // find a different type of beacon, you must specify the byte layout for that beacon's
//        // advertisement with a line like below.  The example shows how to find a beacon with the
//        // same byte layout as AltBeacon but with a beaconTypeCode of 0xaabb.  To find the proper
//        // layout expression for other beacon types, do a web search for "setBeaconLayout"
//        // including the quotes.

        beaconManager.getBeaconParsers().clear();
        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        beaconManager.bind(this);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

//        LocationListener locationListener = new LocationListener() {
//            public void onLocationChanged(Location location) {
//                mLocation = location;
//            }
//
//            public void onStatusChanged(String provider, int status, Bundle extras) {
//                Log.i("locationListener", "changed" + provider);
//            }
//
//            public void onProviderEnabled(String provider) {
//                Log.i("locationListener", "enabled" + provider);
//            }
//
//            public void onProviderDisabled(String provider) {
//                Log.i("locationListener", "disabled" + provider);
//            }
//        };
//
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
//
//        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, locationListener);
//        mLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        Toast.makeText(getBaseContext(), "monitoring create", Toast.LENGTH_SHORT).show();

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
                sendNotification("Please turn on internet connection yoloooooooo");
                Gson gson = new Gson();
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                String jsonPatients = sharedPref.getString("patientList-WeTrack", "");
                Type type = new TypeToken<List<Patients>>() {
                }.getType();
                patientList = gson.fromJson(jsonPatients, type);
            }
        });

    }


    @Override
    public void onBeaconServiceConnect() {

        //Run after delay 5 second because we need to wait the service connect to API
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                sendNotification(beaconManager.getMonitoredRegions().size() + " ; ");

                final Region region3 = new Region("AnyBeacon", null, null, null);

                try {
                    beaconManager.stopMonitoringBeaconsInRegion(region3);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }


                if (patientList != null && !patientList.equals("") && patientList.size() > 0) {
                    for (Patients aPatient : patientList) {
                        if (aPatient.getStatus() == 1 && aPatient.getPatientBeacon().get(0).getStatus() == 1 && aPatient.getPatientBeacon() != null && aPatient.getPatientBeacon().size() > 0) {
                            String uuid = aPatient.getPatientBeacon().get(0).getUuid();
                            Identifier identifier = Identifier.parse(uuid);
                            Identifier identifier2 = Identifier.parse(String.valueOf(aPatient.getPatientBeacon().get(0).getMajor()));
                            Region region = new Region(aPatient.getId() + ";" + identifier, identifier, identifier2, null);
                            regionList.add(region);
                        }
                    }
                }


                beaconManager.addMonitorNotifier(new MonitorNotifier() {
                    @Override
                    public void didEnterRegion(Region region) {
                    }

                    @Override
                    public void didExitRegion(Region region) {
                    }

                    @Override
                    public void didDetermineStateForRegion(int status, Region region) {
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
                                sendNotification("Please turn on internet connection");
                                Gson gson = new Gson();
                                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                                String jsonPatients = sharedPref.getString("patientList-WeTrack", "");
                                Type type = new TypeToken<List<Patients>>() {
                                }.getType();
                                patientList = gson.fromJson(jsonPatients, type);
                            }
                        });


                        if (patientList != null && !patientList.equals("") && patientList.size() > 0) {
                            for (Patients aPatient : patientList) {
                                if (aPatient.getPatientBeacon() != null && aPatient.getPatientBeacon().size() > 0) {
                                    String uuid = aPatient.getPatientBeacon().get(0).getUuid();
                                    Identifier identifier = Identifier.parse(uuid);
                                    Region regionx = new Region(aPatient.getId() + ";" + identifier, identifier, null, null);

                                    if (aPatient.getStatus() == 0 || aPatient.getPatientBeacon().get(0).getStatus() == 0) {
                                        toRemove.add(regionx);
                                    }
                                    if (aPatient.getStatus() == 1 && aPatient.getPatientBeacon().get(0).getStatus() == 1 && !regionList.contains(regionx) && !toAdd.contains(regionx)) {
                                        toAdd.add(regionx);
                                    }

                                }

                            }
                        }

                        //TODO
                        LocationListener locationListener = new LocationListener() {
                            public void onLocationChanged(Location location) {
                                mLocation = location;
                            }

                            public void onStatusChanged(String provider, int status, Bundle extras) {
                                Log.i("locationListener", "changed" + provider);
                            }

                            public void onProviderEnabled(String provider) {
                                Log.i("locationListener", "enabled" + provider);
                            }

                            public void onProviderDisabled(String provider) {
                                Log.i("locationListener", "disabled" + provider);
                            }
                        };

                        if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }

                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, locationListener);
                        mLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                        Date aDate = new Date();
                        SimpleDateFormat curFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String dateObj = curFormatter.format(aDate);

                        sendNotification(beaconManager.getMonitoredRegions().size() + " xxx " + patientList.size() + " yyy " + regionList.size() + " lll " + patientList.get(0).getFullname());

                        if (patientList != null && !patientList.equals("") && patientList.size() > 0 && status == 1 && !region.equals(region3) && mLocation != null) {

                            String[] regionInfo = region.getUniqueId().split(";");
                            Log.i("Service monitoring", regionInfo[0] + " | " + regionInfo[1]);

                            for (Patients patient : patientList) {
                                if (regionInfo[0].equals(patient.getId() + "") && regionInfo[1].equals(patient.getPatientBeacon().get(0).getUuid())) {

                                    if (!checkInternetOn()) {
                                        String firstBeaconIdentifiers = regionInfo[1] + patient.getPatientBeacon().get(0).getMajor() + patient.getPatientBeacon().get(0).getMinor();
                                        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                                        SharedPreferences.Editor editor = sharedPref.edit();
                                        String oldData = sharedPref.getString("listPatientsAndLocations-WeTrack2", "");
                                        editor.putString("listPatientsAndLocations-WeTrack2", firstBeaconIdentifiers + "," + mLocation.getLongitude() + "," + mLocation.getLatitude() + "," + dateObj + ";" + oldData);
                                        editor.putLong("ExpiredDate", System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(44640));
                                        editor.commit();
                                    }

                                    BeaconLocation aLocation = new BeaconLocation(patient.getPatientBeacon().get(0).getId(), patient.getId(), mLocation.getLongitude(), mLocation.getLatitude(), dateObj);

                                    Gson gson = new GsonBuilder()
                                            .setLenient()
                                            .create();
                                    JsonObject obj = gson.toJsonTree(aLocation).getAsJsonObject();

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
                                        }
                                    });
                                }
                            }
                        } else {
                            if (mLocation == null) {
                                sendNotification("Please turn on location service");
                            }
                        }


                        if (checkInternetOn()) {
                            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                            SharedPreferences.Editor editor = sharedPref.edit();
                            String savedData = sharedPref.getString("listPatientsAndLocations-WeTrack2", "");

                            if (!savedData.equals("") && patientList != null) {
                                final String[] patientOffline = savedData.split(";");
                                for (int i = 0; i < patientOffline.length; i++) {
                                    String[] patientInfoOffline = patientOffline[i].split(",");
                                    if (savedData.contains(patientOffline[i] + ";")) {
                                        savedData = savedData.replace(patientOffline[i] + ";", "");
                                    }

                                    if (patientList.size() > 0) {
                                        for (final Patients patient : patientList) {
                                            if (patient.getPatientBeacon() != null && patient.getPatientBeacon().size() > 0) {
                                                String patientBeaconIdentifiers = patient.getPatientBeacon().get(0).getUuid() + patient.getPatientBeacon().get(0).getMajor() + patient.getPatientBeacon().get(0).getMinor();
                                                if (patientInfoOffline[0].equals(patientBeaconIdentifiers) && patient.getStatus() == 1 && patient.getPatientBeacon().get(0).getStatus() == 1) {
                                                    BeaconLocation aLocation = new BeaconLocation(patient.getPatientBeacon().get(0).getId(), 696555, Double.parseDouble(patientInfoOffline[1]), Double.parseDouble(patientInfoOffline[2]), patientInfoOffline[3]);
                                                    Gson gson = new GsonBuilder()
                                                            .setLenient()
                                                            .create();
                                                    JsonObject obj = gson.toJsonTree(aLocation).getAsJsonObject();

                                                    sendNotification(patient.getFullname() + "offline");


//                                        beaconAPI = RetrofitUtils.get().create(BeaconAPI.class);
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
                                                            sendNotification("Please turn on internet connection");
                                                        }
                                                    });

                                                }

                                            }
                                        }
                                    }
                                }
                                editor.putString("listPatientsAndLocations-WeTrack2", savedData);
                                editor.commit();
                            }

                        }

                    }
                });


                try {
                    for (Region aRegion : regionList) {
                        beaconManager.startMonitoringBeaconsInRegion(aRegion);
                    }
                } catch (RemoteException e) {
                }

            }
        }, 5000);


    }

    //Setup for calling the monitoring service after 60 second
    private Handler mHandler;
    private int mInterval = 60000;
    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            for (Region aRegion : regionList) {
                beaconManager.requestStateForRegion(aRegion);
            }
            regionList.removeAll(toRemove);
            regionList.addAll(toAdd);
            try {
                for (Region aRegion : toAdd) {
                    beaconManager.startMonitoringBeaconsInRegion(aRegion);
                }
                for (Region aRegion : toRemove) {
                    beaconManager.stopMonitoringBeaconsInRegion(aRegion);
                }
            } catch (RemoteException e) {
            }
            toRemove.clear();
            toAdd.clear();
            mHandler.postDelayed(mStatusChecker, mInterval);
        }
    };

    void startRepeatingTask() {
        mStatusChecker.run();
    }

    void stopRepeatingTask() {
        mHandler.removeCallbacks(mStatusChecker);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopRepeatingTask();
    }

    private void sendNotification(String name) {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setContentTitle(name)
                        .setContentText(name)
                        .setSmallIcon(R.drawable.icon).setAutoCancel(true);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntent(new Intent(this, MainActivity.class));
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        builder.setContentIntent(resultPendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());
    }


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
