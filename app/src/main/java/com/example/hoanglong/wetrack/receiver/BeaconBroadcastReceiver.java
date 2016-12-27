package com.example.hoanglong.wetrack.receiver;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.widget.Toast;

import com.example.hoanglong.wetrack.BeaconScanService;
import com.example.hoanglong.wetrack.TestService;

import org.altbeacon.beacon.BeaconConsumer;

import java.io.IOException;

/**
 * Created by hoanglong on 19-Dec-16.
 */

public class BeaconBroadcastReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        context.startService
                (new Intent(context, BeaconScanService.class));
        Toast.makeText(context,"service started",Toast.LENGTH_SHORT).show();

        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            String str = device.getName() + "|" + device.getAddress();
            Toast.makeText(context,str,Toast.LENGTH_SHORT).show();


        }

    }


}
