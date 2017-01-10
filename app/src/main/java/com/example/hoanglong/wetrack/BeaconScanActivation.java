package com.example.hoanglong.wetrack;

import android.Manifest;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.hoanglong.wetrack.api.BeaconAPI;
import com.example.hoanglong.wetrack.api.BeaconLocation;
import com.example.hoanglong.wetrack.api.RetrofitUtils;
import com.example.hoanglong.wetrack.utils.PatientList;
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

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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


    private static final String REGION_UUID = "23a01af0-232a-4518-9c0e-323fb773f5ef";
    private static final String REGION_UUID2 = "b9407f30-f5f8-466e-aff9-25556b57fe6d";

    private BeaconAPI beaconAPI;

    List<Patients> patientList = null;
    ArrayList regionList = new ArrayList<Region>();

    Location mLocation;
    LocationManager locationManager;

    @Override
    public void onCreate() {
        super.onCreate();

        Toast.makeText(getBaseContext(), "onCreate beaconScanActivation", Toast.LENGTH_SHORT).show();

//        beaconAPI = RetrofitUtils.get().create(BeaconAPI.class);

//        Gson gson = new Gson();
//        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
//        String jsonPatients = sharedPref.getString("patientList-WeTrack", "");
//        Type type = new TypeToken<List<Patients>>() {
//        }.getType();
//        patientList = gson.fromJson(jsonPatients, type);
//
//        if (patientList != null && !patientList.equals("") && patientList.size() > 0) {
//            for (Patients aPatient : patientList) {
//                String uuid = aPatient.getPatientBeacon().get(0).getUuid();
//                Identifier identifier = Identifier.parse(uuid);
//                Region region = new Region(aPatient.getId() + ";" + identifier, identifier, null, null);
//                regionList.add(region);
//            }
//        }

//        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//
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

//          wake up the app when any beacon is seen (you can specify specific id
//          filers in the parameters below)
//        Identifier identifier = Identifier.parse(REGION_UUID);
//        Region region = new Region("MyRegion", identifier, null, null);
//        regionList.add(region);
//
//        Identifier identifier2 = Identifier.parse(REGION_UUID2);
//        Region region2 = new Region("AnyBeacon", identifier2, null, null);
//        regionList.add(region2);

        Region region3 = new Region("AnyBeacon", null, null, null);

        regionBootstrap = new RegionBootstrap(this, region3);

        mBeaconmanager = org.altbeacon.beacon.BeaconManager.getInstanceForApplication(this);
        mBeaconmanager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));

        backgroundPowerSaver = new BackgroundPowerSaver(this);

        mBeaconmanager.setBackgroundBetweenScanPeriod(120000l);

        this.startService
                (new Intent(this, BeaconMonitoringService.class));

    }

    @Override
    public void didDetermineStateForRegion(int status, Region region) {
        Log.i("Activation-determine", region.getUniqueId() + " : " + status);
//        Toast.makeText(getBaseContext(), "state region", Toast.LENGTH_SHORT).show();

//        Date aDate = new Date();
//        SimpleDateFormat curFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        String dateObj = curFormatter.format(aDate);
//
//        if (patientList != null && !patientList.equals("") && patientList.size() > 0 && status == 1) {
//
//            String[] patientInfo = region.getUniqueId().split(";");
//            Log.i("con cuuuuuuu", patientInfo[0]+" | " + patientInfo[1]);
//            for (Patients patients : patientList) {
//                if (patientInfo[0].equals(patients.getId() + "") && patientInfo[1].equals(patients.getPatientBeacon().get(0).getUuid())) {
//
//                    BeaconLocation aLocation = new BeaconLocation(patients.getPatientBeacon().get(0).getId(), 11111, mLocation.getLongitude(), mLocation.getLatitude(), dateObj);
//
//                    Gson gson = new GsonBuilder()
//                            .setLenient()
//                            .create();
//                    JsonObject obj = gson.toJsonTree(aLocation).getAsJsonObject();
//
//
//                    Call<JsonObject> call = beaconAPI.sendBeaconLocation("Bearer wRe82EIau4STc35oVBF8XyAfF2UVJM8u", "application/json", obj);
//                    call.enqueue(new Callback<JsonObject>() {
//                        @Override
//                        public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
//                            try {
//
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//
//                        @Override
//                        public void onFailure(Call<JsonObject> call, Throwable error) {
//                        }
//                    });
//                }
//
//            }
//        }

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


//        Date aDate = new Date();
//        SimpleDateFormat curFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        String dateObj = curFormatter.format(aDate);
//
//        if (patientList != null && !patientList.equals("") && patientList.size() > 0 ) {
//
//            String[] patientInfo = region.getUniqueId().split(";");
//            Log.i("con cuuuuuuu", patientInfo[0]+" | " + patientInfo[1]);
//            for (Patients patients : patientList) {
//                if (patientInfo[0].equals(patients.getId() + "") && patientInfo[1].equals(patients.getPatientBeacon().get(0).getUuid())) {
//
//                    BeaconLocation aLocation = new BeaconLocation(patients.getPatientBeacon().get(0).getId(), 11111, mLocation.getLongitude(), mLocation.getLatitude(), dateObj);
//
//                    Gson gson = new GsonBuilder()
//                            .setLenient()
//                            .create();
//                    JsonObject obj = gson.toJsonTree(aLocation).getAsJsonObject();
//
//
//                    Call<JsonObject> call = beaconAPI.sendBeaconLocation("Bearer wRe82EIau4STc35oVBF8XyAfF2UVJM8u", "application/json", obj);
//                    call.enqueue(new Callback<JsonObject>() {
//                        @Override
//                        public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
//                            try {
//
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//
//                        @Override
//                        public void onFailure(Call<JsonObject> call, Throwable error) {
//                        }
//                    });
//                }
//
//            }
//        }


//        Toast.makeText(getBaseContext(), "enter region beaconScanActivation", Toast.LENGTH_SHORT).show();
//        Log.d("yolooooooo", "Got a didEnterRegion call: " + region.getUniqueId());
//         This call to disable will make it so the activity below only gets launched the first time a beacon is seen (until the next time the app is launched)
//         if you want the Activity to launch every single time beacons come into view, remove this call.
//        regionBootstrap.disable();
//        Intent intent = new Intent(this, MainActivity.class);

//         IMPORTANT: in the AndroidManifest.xml definition of this activity, you must set android:launchMode="singleInstance" or you will get two instances
//         created when a user launches the activity manually and it gets launched from here.
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        this.startActivity(intent);

        this.startService
                (new Intent(this, BeaconMonitoringService.class));

    }

    @Override
    public void didExitRegion(Region arg0) {
        Toast.makeText(getBaseContext(), "exit beaconScanActivation", Toast.LENGTH_SHORT).show();
        this.stopService
                (new Intent(this, BeaconMonitoringService.class));
    }


}