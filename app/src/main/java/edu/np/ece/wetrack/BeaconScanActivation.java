package edu.np.ece.wetrack;

import android.Manifest;
import android.app.Application;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerUIUtils;

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

import edu.np.ece.wetrack.api.RetrofitUtils;
import edu.np.ece.wetrack.api.ServerAPI;
import edu.np.ece.wetrack.model.BeaconInfo;
import edu.np.ece.wetrack.model.BeaconLocation;
import edu.np.ece.wetrack.model.Resident;
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

    private ServerAPI serverAPI;
    public static List<Resident> patientList = new ArrayList<>();
    public static List<Resident> missingPatientList = new ArrayList<>();
    public static List<Resident> detectedPatientList = new ArrayList<>();
    public static List<BeaconInfo> detectedBeaconList = new ArrayList<>();

    Location mLocation;
    LocationManager locationManager;

    ArrayList<Region> regionList = new ArrayList();
    ArrayList<Region> toRemove = new ArrayList();
    ArrayList<Region> toAdd = new ArrayList();
    final BootstrapNotifier tmp = this;
    private Handler mHandler;
    MainActivity forDisplay = new MainActivity();

    @Override
    public void onCreate() {
        super.onCreate();

        Toast.makeText(getBaseContext(), "onCreate beaconScanActivation", Toast.LENGTH_SHORT).show();


        DrawerImageLoader.init(new AbstractDrawerImageLoader() {
            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder, String tag) {
                Glide.with(imageView.getContext()).load(uri).placeholder(placeholder).into(imageView);
            }

            @Override
            public void cancel(ImageView imageView) {
                Glide.clear(imageView);
            }

            @Override
            public Drawable placeholder(Context ctx, String tag) {
                //define different placeholders for different imageView targets
                //default tags are accessible via the DrawerImageLoader.Tags
                //custom ones can be checked via string. see the CustomUrlBasePrimaryDrawerItem LINE 111
                if (DrawerImageLoader.Tags.PROFILE.name().equals(tag)) {
                    return DrawerUIUtils.getPlaceHolder(ctx);
                } else if (DrawerImageLoader.Tags.ACCOUNT_HEADER.name().equals(tag)) {
                    return new IconicsDrawable(ctx).iconText(" ").backgroundColorRes(com.mikepenz.materialdrawer.R.color.primary).sizeDp(56);
                } else if ("customUrlItem".equals(tag)) {
                    return new IconicsDrawable(ctx).iconText(" ").backgroundColorRes(R.color.md_red_500).sizeDp(56);
                }

                //we use the default one for
                //DrawerImageLoader.Tags.PROFILE_DRAWER_ITEM.name()

                return super.placeholder(ctx, tag);
            }
        });



        mBeaconmanager = org.altbeacon.beacon.BeaconManager.getInstanceForApplication(getBaseContext());
        mBeaconmanager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));

        mBeaconmanager.setBackgroundMode(true);
        backgroundPowerSaver = new BackgroundPowerSaver(getBaseContext());

        mBeaconmanager.setBackgroundBetweenScanPeriod(25000l);
        mBeaconmanager.setBackgroundScanPeriod(20000l);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        serverAPI = RetrofitUtils.get().create(ServerAPI.class);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String token= sharedPref.getString("userToken-WeTrack", "");

        serverAPI.getPatientList("Bearer " + token).enqueue(new Callback<List<Resident>>() {
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
//                sendNotification(t.getMessage());
                Gson gson = new Gson();
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                String jsonPatients = sharedPref.getString("patientList-WeTrack", "");
                Type type = new TypeToken<List<Resident>>() {
                }.getType();
                patientList = gson.fromJson(jsonPatients, type);
            }
        });

        mHandler = new Handler();
        startRepeatingTask();

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

        //TODO
        for (Region allRegion : mBeaconmanager.getMonitoredRegions()) {
            if (!regionList.contains(allRegion)) {
                try {
                    mBeaconmanager.stopMonitoringBeaconsInRegion(allRegion);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

        }

        if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        Date aDate = new Date();
        SimpleDateFormat curFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateObj = curFormatter.format(aDate);

        if (patientList != null && !patientList.equals("") && patientList.size() > 0 && mLocation != null) {

            String[] regionInfo = region.getUniqueId().split(";");

            for (final Resident patient : patientList) {
                for (final BeaconInfo aBeacon : patient.getBeacons()) {
                    if (regionInfo[0].equals(patient.getId() + "") && regionInfo[1].equals(aBeacon.getUuid().toLowerCase()) && regionInfo[2].equals(String.valueOf(aBeacon.getMajor())) && regionInfo[3].equals(String.valueOf(aBeacon.getMinor())) && region.getId2().toString().equals(String.valueOf(aBeacon.getMajor())) && patient.getStatus() == 1 && aBeacon.getStatus() == 1) {

                        if (!checkInternetOn()) {
                            String firstBeaconIdentifiers = regionInfo[1] + aBeacon.getMajor() + aBeacon.getMinor();
                            SharedPreferences sharedPref2 = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                            SharedPreferences.Editor editor = sharedPref2.edit();
                            String oldData = sharedPref.getString("listPatientsAndLocations-WeTrack2", "");
                            editor.putString("listPatientsAndLocations-WeTrack2", firstBeaconIdentifiers + "," + mLocation.getLongitude() + "," + mLocation.getLatitude() + "," + dateObj + ";" + oldData);
                            editor.putLong("ExpiredDate", System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(44640));
                            editor.commit();
                        }

                        BeaconLocation aLocation = new BeaconLocation(aBeacon.getId(), 68, mLocation.getLongitude(), mLocation.getLatitude(), dateObj);


                        Gson gson = new GsonBuilder()
                                .setLenient()
                                .create();
                        JsonObject obj = gson.toJsonTree(aLocation).getAsJsonObject();


                        String token= sharedPref.getString("userToken-WeTrack", "");
                        Call<JsonObject> call = serverAPI.sendBeaconLocation("Bearer " + token, "application/json", obj);
                        call.enqueue(new Callback<JsonObject>() {
                            @Override
                            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                                try {
                                    sendNotification2(patient, "is nearby.");

//                                    if(detectedBeaconList.contains(a))
                                    detectedPatientList.add(patient);
                                    detectedBeaconList.add(aBeacon);
                                    if (MainActivity.adapterDevice != null) {

                                        forDisplay.logToDisplay();
                                    }
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

//        sendNotification("exit: " + region.getUniqueId() + " | " + mBeaconmanager.getMonitoredRegions().size());

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        Date aDate = new Date();
        SimpleDateFormat curFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateObj = curFormatter.format(aDate);

        if (patientList != null && !patientList.equals("") && patientList.size() > 0 && mLocation != null) {

            String[] regionInfo = region.getUniqueId().split(";");

            for (final Resident patient : patientList) {
                for (final BeaconInfo aBeacon : patient.getBeacons()) {
                    if (regionInfo[0].equals(patient.getId() + "") && regionInfo[1].equals(aBeacon.getUuid().toLowerCase()) && regionInfo[2].equals(String.valueOf(aBeacon.getMajor())) && regionInfo[3].equals(String.valueOf(aBeacon.getMinor())) && region.getId2().toString().equals(String.valueOf(aBeacon.getMajor())) && patient.getStatus() == 1 && aBeacon.getStatus() == 1) {

                        if (!checkInternetOn()) {
                            String firstBeaconIdentifiers = regionInfo[1] + aBeacon.getMajor() + aBeacon.getMinor();
                            SharedPreferences sharedPref2 = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                            SharedPreferences.Editor editor = sharedPref2.edit();
                            String oldData = sharedPref.getString("listPatientsAndLocations-WeTrack2", "");
                            editor.putString("listPatientsAndLocations-WeTrack2", firstBeaconIdentifiers + "," + mLocation.getLongitude() + "," + mLocation.getLatitude() + "," + dateObj + ";" + oldData);
                            editor.putLong("ExpiredDate", System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(44640));
                            editor.commit();
                        }

                        BeaconLocation aLocation = new BeaconLocation(aBeacon.getId(), 68, mLocation.getLongitude(), mLocation.getLatitude(), dateObj);

                        sendNotification2(patient, "is out of range.");

                        List<Resident> residentToRemove = new ArrayList<>();
                        List<BeaconInfo> beaconToRemove = new ArrayList<>();

                        for (Resident aResident : detectedPatientList) {
                            if (aResident.getId() == patient.getId()) {
                                residentToRemove.add(aResident);
                                break;
                            }
                        }

                        for (BeaconInfo removeBeacon : detectedBeaconList) {
                            if (removeBeacon.getId() == aBeacon.getId()) {
                                beaconToRemove.add(removeBeacon);
                                break;
                            }
                        }

                        detectedPatientList.removeAll(residentToRemove);
                        detectedBeaconList.removeAll(beaconToRemove);

                        residentToRemove.clear();
                        beaconToRemove.clear();


                        if (MainActivity.adapterDevice != null) {

                            forDisplay.logToDisplay();

                        }

                        Gson gson = new GsonBuilder()
                                .setLenient()
                                .create();
                        JsonObject obj = gson.toJsonTree(aLocation).getAsJsonObject();

                        String token= sharedPref.getString("userToken-WeTrack", "");
                        Call<JsonObject> call = serverAPI.sendBeaconLocation("Bearer " + token, "application/json", obj);
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

            }
        } else {
            if (mLocation == null) {
                sendNotification("Please turn on location service");
            }
        }
    }


    private void sendNotification(String name) {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setContentTitle("We Track")
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
        notificationManager.notify(999, builder.build());
    }

    private void sendNotification2(Resident aResident, String msg) {
//        Resident r = new Resident(aResident);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setContentTitle("We Track")
                        .setContentText(aResident.getFullname() + " " + msg)
                        .setSmallIcon(R.drawable.icon).setAutoCancel(true);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        Intent intent = new Intent(this, ResidentDetailActivity.class);
//        intent.putExtra("patient", r);
        intent.putExtra("patient", aResident);
//        intent.putExtra("fromNotification", aResident.getId()+"");
        stackBuilder.addNextIntent(intent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        aResident.getId(),
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        builder.setContentIntent(resultPendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(aResident.getId(), builder.build());
    }

    private int mInterval = 10000;
    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {

            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            String token= sharedPref.getString("userToken-WeTrack", "");

            serverAPI.getPatientList("Bearer " + token).enqueue(new Callback<List<Resident>>() {
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

                    t.printStackTrace();
                    Gson gson = new Gson();
                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                    String jsonPatients = sharedPref.getString("patientList-WeTrack", "");
                    Type type = new TypeToken<List<Resident>>() {
                    }.getType();
                    patientList = gson.fromJson(jsonPatients, type);
                }
            });



            if (patientList != null && !patientList.equals("") && patientList.size() > 0 && tmp != null) {
                for (Resident aPatient : patientList) {
                    for (BeaconInfo aBeacon : aPatient.getBeacons()) {
                        if (aPatient.getStatus() == 1 && aBeacon.getStatus() == 1 && aPatient.getBeacons() != null && aPatient.getBeacons().size() > 0) {

                            String uuid = aBeacon.getUuid();
                            Identifier identifier = Identifier.parse(uuid);
                            Identifier identifier2 = Identifier.parse(String.valueOf(aBeacon.getMajor()));
                            Identifier identifier3 = Identifier.parse(String.valueOf(aBeacon.getMinor()));
                            Region region = new Region(aPatient.getId() + ";" + identifier + ";" + identifier2 + ";" + identifier3, identifier, identifier2, identifier3);
                            if (!regionList.contains(region)) {
                                regionList.add(region);
                            }
                        }
                    }

                }
            }

            regionBootstrap = new RegionBootstrap(tmp, regionList);


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
                            for (final Resident patient : patientList) {
                                for (BeaconInfo aBeacon : patient.getBeacons()) {
                                    if (patient.getBeacons() != null && patient.getBeacons().size() > 0) {
                                        String patientBeaconIdentifiers = aBeacon.getUuid() + aBeacon.getMajor() + aBeacon.getMinor();
                                        if (patientInfoOffline[0].equals(patientBeaconIdentifiers) && patient.getStatus() == 1 && aBeacon.getStatus() == 1) {
                                            BeaconLocation aLocation = new BeaconLocation(aBeacon.getId(), 68, Double.parseDouble(patientInfoOffline[1]), Double.parseDouble(patientInfoOffline[2]), patientInfoOffline[3]);

                                            Gson gson = new GsonBuilder()
                                                    .setLenient()
                                                    .create();
                                            JsonObject obj = gson.toJsonTree(aLocation).getAsJsonObject();

                                            Call<JsonObject> call = serverAPI.sendBeaconLocation("Bearer " + token, "application/json", obj);
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

//            sendNotification(mBeaconmanager.getMonitoredRegions().size() + " | " + detectedBeaconList.size());

            if (MainActivity.adapterDevice != null) {
                forDisplay.logToDisplay();
            }

            mHandler.postDelayed(mStatusChecker, mInterval);

        }
    };

    void startRepeatingTask() {
        mStatusChecker.run();
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