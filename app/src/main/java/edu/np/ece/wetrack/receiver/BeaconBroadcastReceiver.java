package edu.np.ece.wetrack.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by hoanglong on 19-Dec-16.
 */

public class BeaconBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
//        String action = intent.getAction();

//        Toast.makeText(context,"service started",Toast.LENGTH_SHORT).show();

//        Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage("com.example.hoanglong.wetrack");
//        if (launchIntent != null) {
//            context.startActivity(launchIntent);//null pointer check in case package name was not found
//        }

//        if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
//            final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
//                    BluetoothAdapter.ERROR);
//            switch (state) {
//                case BluetoothAdapter.STATE_OFF:
//                    break;
//                case BluetoothAdapter.STATE_TURNING_OFF:
//                    break;
//                case BluetoothAdapter.STATE_ON: {
//                    Toast.makeText(context, "bluetooth on", Toast.LENGTH_SHORT).show();
////                    context.startService
////                            (new Intent(context, BeaconMonitoringService.class));
//                    Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage("com.example.hoanglong.wetrack");
//                    if (launchIntent != null) {
//                        context.startActivity(launchIntent);//null pointer check in case package name was not found
//                    }
//                }
//                break;
//                case BluetoothAdapter.STATE_TURNING_ON:
//                    break;
//            }
//        }

    }


}
