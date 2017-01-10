package com.example.hoanglong.wetrack.receiver;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.widget.Toast;

import com.example.hoanglong.wetrack.BeaconMonitoringService;
import com.example.hoanglong.wetrack.BeaconScanActivation;
import com.example.hoanglong.wetrack.BeaconScanService;
import com.example.hoanglong.wetrack.TestService;

import org.altbeacon.beacon.BeaconConsumer;

import java.io.IOException;

/**
 * Created by hoanglong on 19-Dec-16.
 */

public class BeaconBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();


//        Toast.makeText(context,"service started",Toast.LENGTH_SHORT).show();

        if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
            final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                    BluetoothAdapter.ERROR);
            switch (state) {
                case BluetoothAdapter.STATE_OFF:
                    break;
                case BluetoothAdapter.STATE_TURNING_OFF:
                    break;
                case BluetoothAdapter.STATE_ON: {
                    Toast.makeText(context, "bluetooth on", Toast.LENGTH_SHORT).show();
                    context.startService
                            (new Intent(context, BeaconMonitoringService.class));
                }
                break;
                case BluetoothAdapter.STATE_TURNING_ON:
                    break;
            }
        }

    }


}
