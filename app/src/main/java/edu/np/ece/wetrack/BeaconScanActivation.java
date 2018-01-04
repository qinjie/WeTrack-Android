package edu.np.ece.wetrack;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.crashlytics.android.Crashlytics;
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

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import edu.np.ece.wetrack.api.RetrofitUtils;
import edu.np.ece.wetrack.api.ServerAPI;
import edu.np.ece.wetrack.model.BeaconInfo;
import edu.np.ece.wetrack.model.BeaconLocation;
import edu.np.ece.wetrack.model.Resident;
import io.fabric.sdk.android.Fabric;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static edu.np.ece.wetrack.tasks.SendNotificationTask.sendNotification;
import static edu.np.ece.wetrack.tasks.SendNotificationTask.sendNotificationForDetected;


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
    public static List<Resident> detectedPatientList = new ArrayList<>();
    public static List<BeaconInfo> detectedBeaconList = new ArrayList<>();

    Location mLocation;
    LocationManager locationManager;

    ArrayList<Region> regionList = new ArrayList();
    final BootstrapNotifier tmp = this;
    private Handler mHandler;
    MainActivity forDisplay = new MainActivity();

    @Override
    public void onCreate() {
        super.onCreate();

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

        Fabric.with(this, new Crashlytics());


        mBeaconmanager = org.altbeacon.beacon.BeaconManager.getInstanceForApplication(getBaseContext());
        mBeaconmanager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));

        mBeaconmanager.setBackgroundMode(true);
        backgroundPowerSaver = new BackgroundPowerSaver(getBaseContext());

        mBeaconmanager.setBackgroundBetweenScanPeriod(25000l);
        mBeaconmanager.setBackgroundScanPeriod(20000l);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        serverAPI = RetrofitUtils.get().create(ServerAPI.class);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String token = sharedPref.getString("userToken-WeTrack", "");

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
                sendNotification(getBaseContext(), "Please turn on internet connection");
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

    }

    @Override
    public void didEnterRegion(Region region) {

        //Clear offline list if user doesn't turn on internet connection after 31 days
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        if (sharedPref.getLong("ExpiredDate", -1) < System.currentTimeMillis()) {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("listPatientsAndLocations-WeTrack2", "");
            editor.putLong("ExpiredDate", System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(44640));
            editor.apply();
        }

        //Setup to get location
        if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        //Setup to get date
        Date aDate = new Date();
        SimpleDateFormat curFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateObj = curFormatter.format(aDate);

        if (patientList != null && !patientList.equals("") && patientList.size() > 0 && mLocation != null) {

            String[] regionInfo = region.getUniqueId().split(";");

            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(getBaseContext(), Locale.getDefault());
            String fullAddress = "";

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

                        String userID = sharedPref.getString("userID-WeTrack", "");

                        if (!userID.equals("")) {
                            try {
                                addresses = geocoder.getFromLocation(mLocation.getLatitude(), mLocation.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

                                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                                String country = addresses.get(0).getCountryName();

                                fullAddress = address + ", " + country;
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            BeaconLocation aLocation = new BeaconLocation(aBeacon.getId(), Integer.parseInt(userID), mLocation.getLongitude(), mLocation.getLatitude(), dateObj, fullAddress);

                            Gson gson = new GsonBuilder()
                                    .setLenient()
                                    .create();
                            JsonObject obj = gson.toJsonTree(aLocation).getAsJsonObject();

                            String token = sharedPref.getString("userToken-WeTrack", "");
                            Call<JsonObject> call = serverAPI.sendBeaconLocation("Bearer " + token, "application/json", obj);
                            call.enqueue(new Callback<JsonObject>() {
                                @Override
                                public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                                    try {
                                        sendNotificationForDetected(getBaseContext(), patient, "is nearby.");

                                        detectedPatientList.add(patient);
                                        detectedBeaconList.add(aBeacon);
                                        if (MainActivity.beaconListAdapter != null) {
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
            }
        } else {
            if (mLocation == null) {
                sendNotification(getBaseContext(), "Please turn on location service");
            }
        }
    }

    @Override
    public void didExitRegion(Region region) {
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

                        if (MainActivity.beaconListAdapter != null) {
                            forDisplay.logToDisplay();
                        }
                    }
                }
            }
        } else {
            if (mLocation == null) {
                sendNotification(getBaseContext(), "Please turn on location service");
            }
        }
    }

    //this will re-run after every 20 seconds
    private int mInterval = 20000;
    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            String token = sharedPref.getString("userToken-WeTrack", "");

            String isScanning = sharedPref.getString("isScanning-WeTrack", "true");
            String isLogin = sharedPref.getString("userToken-WeTrack", "");

            if (isScanning.equals("true") && !isLogin.equals("")) {
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
                            String uuid = aBeacon.getUuid();
                            Identifier identifier = Identifier.parse(uuid);
                            Identifier identifier2 = Identifier.parse(String.valueOf(aBeacon.getMajor()));
                            Identifier identifier3 = Identifier.parse(String.valueOf(aBeacon.getMinor()));
                            Region region = new Region(aPatient.getId() + ";" + identifier + ";" + identifier2 + ";" + identifier3, identifier, identifier2, identifier3);

                            if (aPatient.getStatus() == 1 && aBeacon.getStatus() == 1 && aPatient.getBeacons() != null && aPatient.getBeacons().size() > 0) {
                                if (!regionList.contains(region)) {
                                    regionList.add(region);
                                }

                            } else {
                                List<Resident> residentToRemove = new ArrayList<>();
                                List<BeaconInfo> beaconToRemove = new ArrayList<>();

                                for (Resident aResident : detectedPatientList) {
                                    if (aResident.getId() == aPatient.getId()) {
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

                                if (regionList.contains(region)) {
                                    regionList.remove(region);
                                    try {
                                        mBeaconmanager.stopMonitoringBeaconsInRegion(region);
                                    } catch (RemoteException e) {
                                        e.printStackTrace();
                                    }
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

                    Geocoder geocoder;
                    List<Address> addresses;
                    geocoder = new Geocoder(getBaseContext(), Locale.getDefault());
                    String fullAddress = "";

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
                                                String userID = sharedPref.getString("userID-WeTrack", "");
                                                if (!userID.equals("")) {
                                                    try {
                                                        addresses = geocoder.getFromLocation(Double.parseDouble(patientInfoOffline[2]), Double.parseDouble(patientInfoOffline[1]), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                                                        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                                                        String city = addresses.get(0).getLocality();
                                                        fullAddress = address + ", " + city;

                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    }

                                                    BeaconLocation aLocation = new BeaconLocation(aBeacon.getId(), Integer.parseInt(userID), Double.parseDouble(patientInfoOffline[1]), Double.parseDouble(patientInfoOffline[2]), patientInfoOffline[3], fullAddress);

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
                                                        }
                                                    });

                                                }

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

                if (MainActivity.beaconListAdapter != null) {
                    forDisplay.logToDisplay();
                }

            } else {
                if (regionList != null && regionList.size() > 0) {
                    for (Region tmp : regionList) {
                        try {
                            mBeaconmanager.stopMonitoringBeaconsInRegion(tmp);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                }

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