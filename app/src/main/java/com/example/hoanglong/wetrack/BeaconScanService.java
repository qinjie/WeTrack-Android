package com.example.hoanglong.wetrack;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

import com.example.hoanglong.wetrack.api.BeaconAPI;
import com.example.hoanglong.wetrack.api.BeaconLocation;
import com.example.hoanglong.wetrack.api.Patients;
import com.example.hoanglong.wetrack.api.RetrofitUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.hoanglong.wetrack.MainActivity.adapterDevice;


public class BeaconScanService extends Service implements BeaconConsumer {

    public static BeaconManager beaconManager;
    public static List<String> listBeacon = new ArrayList<String>();
    public static LinkedHashMap<String, Double> listBeaconAndRange = new LinkedHashMap<>();
    public static LinkedHashMap<String, Location> listPatientAndLocation = new LinkedHashMap<>();

    private BeaconAPI beaconAPI;
    List<Patients> patientList = null;

    Location mLocation;
    LocationManager locationManager;

    List<BeaconLocation> locationList = new ArrayList<>();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }


    @Override
    public void onCreate() {
        super.onCreate();
        beaconManager = org.altbeacon.beacon.BeaconManager.getInstanceForApplication(this);

        // By default the AndroidBeaconLibrary will only find AltBeacons.  If you wish to make it
        // find a different type of beacon, you must specify the byte layout for that beacon's
        // advertisement with a line like below.  The example shows how to find a beacon with the
        // same byte layout as AltBeacon but with a beaconTypeCode of 0xaabb.  To find the proper
        // layout expression for other beacon types, do a web search for "setBeaconLayout"
        // including the quotes.
        //
        beaconManager.getBeaconParsers().clear();
        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        beaconManager.bind(this);

        beaconAPI = RetrofitUtils.get().create(BeaconAPI.class);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                mLocation = location;
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

    }

    @Override
    public void onBeaconServiceConnect() {
        final MainActivity test = new MainActivity();

        beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                if (beacons.size() > 0) {
//                    beaconManager.setBackgroundScanPeriod(TimeUnit.SECONDS.toMillis(1));
//                    beaconManager.setBackgroundBetweenScanPeriod(1000);
                    Beacon firstBeacon = beacons.iterator().next();
                    double range = Math.round(firstBeacon.getDistance() * 1000.0) / 1000.0;

                    String line = firstBeacon.getId1() + " | " + firstBeacon.getId2() + " | " + firstBeacon.getId3() + " (" + firstBeacon.getBluetoothAddress() + ")";

                    if (!listBeacon.contains(line)) {
                        listBeacon.add(line);
                    }

                    listBeaconAndRange.put(line, range);
                    if (adapterDevice != null) {
                        test.logToDisplay();
                    }

                    beaconAPI.getPatientList("Bearer wRe82EIau4STc35oVBF8XyAfF2UVJM8u").enqueue(new Callback<List<Patients>>() {
                        @Override
                        public void onResponse(Call<List<Patients>> call, Response<List<Patients>> response) {
                            try {
                                patientList = response.body();
                            } catch (Exception e) {
                            }
                        }

                        @Override
                        public void onFailure(Call<List<Patients>> call, Throwable t) {
                            sendNotification("Please turn on internet connection");
                        }
                    });

                    String nearbyBeaconIdentifiers = firstBeacon.getId1().toString() + firstBeacon.getId2().toString() + firstBeacon.getId3().toString();
                    if (patientList != null && patientList.size() > 0) {
                        for (Patients patients : patientList) {
                            try {
                                if (patients.getPatientBeacon() != null) {
                                    String patientBeaconIdentifiers = patients.getPatientBeacon().get(0).getUuid() + patients.getPatientBeacon().get(0).getMajor() + patients.getPatientBeacon().get(0).getMinor();
                                    if (patientBeaconIdentifiers.equals(nearbyBeaconIdentifiers) && patients.getStatus() == 1 && mLocation != null) {

                                        sendNotification(patients.getFullname());

                                        BeaconLocation aLocation = new BeaconLocation(patients.getPatientBeacon().get(0).getId(), patients.getId(), mLocation.getLongitude(), mLocation.getLatitude());
                                        Gson gson = new GsonBuilder()
                                                .setLenient()
                                                .create();
                                        JsonObject obj = gson.toJsonTree(aLocation).getAsJsonObject();

                                        beaconAPI = RetrofitUtils.get().create(BeaconAPI.class);
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
                            } catch (Exception e) {
//                                sendNotification(e.getMessage(),99);
                            }
                        }
                    }
                }
            }
        });

        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {
            sendNotification(e.getMessage());
        }

        beaconManager.addMonitorNotifier(new MonitorNotifier() {
            @Override
            public void didEnterRegion(Region region) {
                Log.i("Monitor", "I just saw an beacon for the first time!");
            }

            @Override
            public void didExitRegion(Region region) {
                Log.i("Monitor", "I no longer see an beacon");
            }

            @Override
            public void didDetermineStateForRegion(int state, Region region) {
                Log.i("Monitor", "I have just switched from seeing/not seeing beacons: " + state);
            }
        });

        try {
            beaconManager.startMonitoringBeaconsInRegion(new Region("myMonitoringUniqueId", null, null, null));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    private void sendNotification(String name) {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setContentTitle(name)
                        .setContentText(mLocation.getLongitude() + " | " + mLocation.getLatitude())
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


}
