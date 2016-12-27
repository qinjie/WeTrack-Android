package com.example.hoanglong.wetrack;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;
import org.altbeacon.beacon.startup.BootstrapNotifier;
import org.altbeacon.beacon.startup.RegionBootstrap;

import java.util.Collection;


/**
 * Created by hoanglong on 21-Dec-16.
 */

public class TestService extends Service implements BootstrapNotifier, BeaconConsumer {
    public static BeaconManager beaconManager;
    private static final String TAG = ".MyApplicationName";
    private RegionBootstrap regionBootstrap;
    private BackgroundPowerSaver backgroundPowerSaver;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        Toast.makeText(getBaseContext(), "test start", Toast.LENGTH_SHORT).show();

        // wake up the app when any beacon is seen (you can specify specific id
        // filers in the parameters below)

        Region region = new Region("MyRegion", null, null, null);
        regionBootstrap = new RegionBootstrap(this, region);

        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        beaconManager.bind(this);

        backgroundPowerSaver = new BackgroundPowerSaver(this);


        getBaseContext().startService
                (new Intent(getBaseContext(), BeaconScanService.class));
        Toast.makeText(getBaseContext(), "begin scan service", Toast.LENGTH_SHORT).show();

        super.onCreate();
    }

    @Override
    public void didEnterRegion(Region region) {
        // This call to disable will make it so the activity below only gets launched the first time a beacon is seen (until the next time the app is launched)
        // if you want the Activity to launch every single time beacons come into view, remove this call.
        getBaseContext().startService
                (new Intent(getBaseContext(), BeaconScanService.class));
        Toast.makeText(getBaseContext(), "enter region", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void didExitRegion(Region region) {
        Toast.makeText(getBaseContext(), "exit", Toast.LENGTH_SHORT).show();

        getBaseContext().stopService
                (new Intent(getBaseContext(), BeaconScanService.class));
    }

    @Override
    public void didDetermineStateForRegion(int i, Region region) {

    }

    @Override
    public void onBeaconServiceConnect() {
        Toast.makeText(getBaseContext(), "beacon connect", Toast.LENGTH_SHORT).show();
        beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
//                sendNotification("caacacacacacac");
//                getBaseContext().startService
//                        (new Intent(getBaseContext(), BeaconScanService.class));
            }});

        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {
            e.printStackTrace();
            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }


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

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(getBaseContext(), "on start command test", Toast.LENGTH_SHORT).show();

        getBaseContext().startService
                (new Intent(getBaseContext(), BeaconScanService.class));

        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
