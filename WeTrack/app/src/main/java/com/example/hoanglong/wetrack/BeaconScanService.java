package com.example.hoanglong.wetrack;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.ArrayAdapter;
import android.widget.Toast;



public class BeaconScanService extends Service {

    public static ArrayAdapter<String> adapterDevice;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(getApplicationContext(), "ahihi do ngok", Toast.LENGTH_SHORT).show();


        return Service.START_STICKY;
    }


}
