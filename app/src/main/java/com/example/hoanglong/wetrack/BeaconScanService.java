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
import android.os.IBinder;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

import com.example.hoanglong.wetrack.api.ServerAPI;
import com.example.hoanglong.wetrack.model.BeaconLocation;
import com.example.hoanglong.wetrack.api.RetrofitUtils;
import com.example.hoanglong.wetrack.model.Resident;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
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

    private ServerAPI serverAPI;
    List<Resident> patientList = null;

    Location mLocation;
    LocationManager locationManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        Toast.makeText(getBaseContext(), "on start command scan", Toast.LENGTH_SHORT).show();
        return Service.START_STICKY;
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


    @Override
    public void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }


    @Override
    public void onCreate() {

        super.onCreate();

//        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
//        SharedPreferences.Editor editor = sharedPref.edit();
//        editor.putString("listPatientsAndLocations-WeTrack", "");

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

        serverAPI = RetrofitUtils.get().create(ServerAPI.class);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

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

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, locationListener);
        mLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);


        Toast.makeText(getBaseContext(), "scan create", Toast.LENGTH_SHORT).show();


    }

    @Override
    public void onBeaconServiceConnect() {
        Toast.makeText(getBaseContext(), "scan found", Toast.LENGTH_SHORT).show();

        final MainActivity test = new MainActivity();

//        Intent it= new Intent(getApplicationContext(),BeaconScanActivation.class);
//        it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////        startActivity(it);
//        startService(it);
//        BeaconScanActivation tmp = new BeaconScanActivation();
//        tmp.();


        beaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                if (beacons.size() > 0) {
                    Beacon firstBeacon = beacons.iterator().next();
                    double range = Math.round(firstBeacon.getDistance() * 1000.0) / 1000.0;

                    Date aDate = new Date();
                    SimpleDateFormat curFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String dateObj = curFormatter.format(aDate);

                    String line = firstBeacon.getId1() + "," + firstBeacon.getId2() + "," + firstBeacon.getId3() + "," + firstBeacon.getBluetoothAddress();

                    serverAPI.getPatientList().enqueue(new Callback<List<Resident>>() {

                        @Override
                        public void onResponse(Call<List<Resident>> call, Response<List<Resident>> response) {
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
                        public void onFailure(Call<List<Resident>> call, Throwable t) {
                            sendNotification("Please turn on internet connection");
                            Gson gson = new Gson();
                            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                            String jsonPatients = sharedPref.getString("patientList-WeTrack", "");
                            Type type = new TypeToken<List<Resident>>() {
                            }.getType();
                            patientList = gson.fromJson(jsonPatients, type);
                        }
                    });


                    if (!listBeacon.contains(line)) {
                        listBeacon.add(line);
                    }

                    listBeaconAndRange.put(line, range);
                    if (adapterDevice != null) {
                        test.logToDisplay();
                    }

                    if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }


                    if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                        mLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                        if (!checkInternetOn()) {
                            for (Beacon firstBeacon1 : beacons) {
                                String firstBeaconIdentifiers = firstBeacon1.getId1().toString() + firstBeacon1.getId2().toString() + firstBeacon1.getId3().toString();

                                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                                SharedPreferences.Editor editor = sharedPref.edit();
                                String oldData = sharedPref.getString("listPatientsAndLocations-WeTrack2", "");
                                editor.putString("listPatientsAndLocations-WeTrack2", firstBeaconIdentifiers + "," + mLocation.getLongitude() + "," + mLocation.getLatitude() + "," + dateObj + ";" + oldData);
                                editor.putLong("ExpiredDate", System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(44640));

                                editor.commit();
                            }
                        }

                        if (patientList != null && patientList.size() > 0) {
                            for (final Resident resident : patientList) {
                                for (Beacon firstBeacon1 : beacons) {
                                    String nearbyBeaconIdentifiers = firstBeacon1.getId1().toString() + firstBeacon1.getId2().toString() + firstBeacon1.getId3().toString();
                                    try {
                                        if (resident.getPatientBeacon() != null && resident.getPatientBeacon().size() > 0) {
                                            String patientBeaconIdentifiers = resident.getPatientBeacon().get(0).getUuid() + resident.getPatientBeacon().get(0).getMajor() + resident.getPatientBeacon().get(0).getMinor();
                                            if (patientBeaconIdentifiers.equals(nearbyBeaconIdentifiers) && resident.getStatus() == 1) {

                                                BeaconLocation aLocation = new BeaconLocation(resident.getPatientBeacon().get(0).getId(), resident.getId(), mLocation.getLongitude(), mLocation.getLatitude(), dateObj);

                                                Gson gson = new GsonBuilder()
                                                        .setLenient()
                                                        .create();
                                                JsonObject obj = gson.toJsonTree(aLocation).getAsJsonObject();

                                                sendNotification(resident.getFullname());

                                                serverAPI = RetrofitUtils.get().create(ServerAPI.class);
                                                Call<JsonObject> call = serverAPI.sendBeaconLocation("Bearer wRe82EIau4STc35oVBF8XyAfF2UVJM8u", "application/json", obj);
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
                                        sendNotification("Please turn on location serviceuuuuuuuu");
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    } else {
                        sendNotification("Please turn on location service");
                    }

                }

                if (checkInternetOn()) {
                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                    SharedPreferences.Editor editor = sharedPref.edit();
                    String savedData = sharedPref.getString("listPatientsAndLocations-WeTrack2", "");

                    if (!savedData.equals("") && patientList != null) {
                        final String[] patients = savedData.split(";");
                        for (int i = 0; i < patients.length; i++) {
                            String[] patientInfo = patients[i].split(",");
                            if (savedData.contains(patients[i] + ";")) {
                                savedData = savedData.replace(patients[i] + ";", "");
                            }

                            if (patientList.size() > 0) {
                                for (final Resident patient : patientList) {
                                    if (patient.getPatientBeacon() != null && patient.getPatientBeacon().size() > 0) {
                                        String patientBeaconIdentifiers = patient.getPatientBeacon().get(0).getUuid() + patient.getPatientBeacon().get(0).getMajor() + patient.getPatientBeacon().get(0).getMinor();
                                        if (patientInfo[0].equals(patientBeaconIdentifiers) && patient.getStatus() == 1) {
                                            BeaconLocation aLocation = new BeaconLocation(patient.getPatientBeacon().get(0).getId(), 696, Double.parseDouble(patientInfo[1]), Double.parseDouble(patientInfo[2]), patientInfo[3]);
                                            Gson gson = new GsonBuilder()
                                                    .setLenient()
                                                    .create();
                                            JsonObject obj = gson.toJsonTree(aLocation).getAsJsonObject();

                                            sendNotification(patient.getFullname() + "offline");


//                                        serverAPI = RetrofitUtils.get().create(ServerAPI.class);
                                            Call<JsonObject> call = serverAPI.sendBeaconLocation("Bearer wRe82EIau4STc35oVBF8XyAfF2UVJM8u", "application/json", obj);
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


}
