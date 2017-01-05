package com.example.hoanglong.wetrack;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;
import org.altbeacon.beacon.startup.BootstrapNotifier;
import org.altbeacon.beacon.startup.RegionBootstrap;

/**
 * Created by hoanglong on 21-Dec-16.
 */

public class BeaconScanActivation extends Application implements BootstrapNotifier {
    private static final String TAG = ".MyApplicationName";
    private RegionBootstrap regionBootstrap;
    private BackgroundPowerSaver backgroundPowerSaver;
    private BeaconManager mBeaconmanager;

    @Override
    public void onCreate() {
//        super.onCreate();
//        Log.d(TAG, "App started up");
//
//        // To detect proprietary beacons, you must add a line like below corresponding to your beacon
//        // type.  Do a web search for "setBeaconLayout" to get the proper expression.
//        // beaconManager.getBeaconParsers().add(new BeaconParser().
//        //        setBeaconLayout("m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
//        beaconManager.getBeaconParsers().add(new BeaconParser().
//                setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
//        beaconManager.setBackgroundMode(true);
//        // wake up the app when any beacon is seen (you can specify specific id filers in the parameters below)
//        Region region = new Region("com.example.myapp.boostrapRegion", null, null, null);
//        regionBootstrap = new RegionBootstrap(this, region);
//        try {
//            beaconManager.startMonitoringBeaconsInRegion(region);
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }


        Toast.makeText(getBaseContext(), "onCreate beaconScanActivation", Toast.LENGTH_SHORT).show();

        // wake up the app when any beacon is seen (you can specify specific id
        // filers in the parameters below)

        Region region = new Region("MyRegion", null, null, null);
        regionBootstrap = new RegionBootstrap(this, region);

        mBeaconmanager = org.altbeacon.beacon.BeaconManager.getInstanceForApplication(this);
        mBeaconmanager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));

        backgroundPowerSaver = new BackgroundPowerSaver(this);

        mBeaconmanager.setBackgroundBetweenScanPeriod(120000l);

        this.startService
                (new Intent(this, BeaconScanService.class));
        super.onCreate();

    }

    @Override
    public void didDetermineStateForRegion(int arg0, Region arg1) {
        // Don't care
    }

    @Override
    public void didEnterRegion(Region arg0) {
        //TODO
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        if (sharedPref.getLong("ExpiredDate", -1) < System.currentTimeMillis()) {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.clear();
            editor.apply();
        }

        Toast.makeText(getBaseContext(), "enter region beaconScanActivation", Toast.LENGTH_SHORT).show();

        Log.d(TAG, "Got a didEnterRegion call");
        // This call to disable will make it so the activity below only gets launched the first time a beacon is seen (until the next time the app is launched)
        // if you want the Activity to launch every single time beacons come into view, remove this call.
//        regionBootstrap.disable();
//        Intent intent = new Intent(this, MainActivity.class);

        // IMPORTANT: in the AndroidManifest.xml definition of this activity, you must set android:launchMode="singleInstance" or you will get two instances
        // created when a user launches the activity manually and it gets launched from here.
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        this.startActivity(intent);

        this.startService
                (new Intent(this, BeaconScanService.class));

    }

    @Override
    public void didExitRegion(Region arg0) {
        Toast.makeText(getBaseContext(), "exit beaconScanActivation", Toast.LENGTH_SHORT).show();
        this.stopService
                (new Intent(this, BeaconScanService.class));
    }


}