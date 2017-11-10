package edu.np.ece.wetrack.tasks;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import edu.np.ece.wetrack.MainActivity;
import edu.np.ece.wetrack.R;
import edu.np.ece.wetrack.ResidentDetailActivity;
import edu.np.ece.wetrack.model.Resident;

/**
 * Created by hoanglong on 13-Feb-17.
 */

public class SendNotificationTask {
    public static void sendNotification(Context context, String content) {

        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String isNoti = sharedPref.getString("isNoti-WeTrack", "true");

        if (isNoti.equals("true")) {
            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(context)
                            .setContentTitle("Elderly Track")
                            .setContentText(content)
                            .setSmallIcon(R.drawable.icon_noti)
                            .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                                    R.mipmap.ic_launcher))
                            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                            .setStyle(new NotificationCompat.BigTextStyle()
                                    .bigText(content))
                            .setAutoCancel(true);

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            Intent intent = new Intent(context, MainActivity.class);

            stackBuilder.addNextIntent(intent);
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(
                            0,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
            builder.setContentIntent(resultPendingIntent);
            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(x++, builder.build());
        }
    }

    static int x = 999;

    public static void sendNotificationForDetected(Context context, Resident aResident, String msg) {
        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String isNoti = sharedPref.getString("isNoti-WeTrack", "true");
        String isLogin = sharedPref.getString("userToken-WeTrack", "");

        if (isNoti.equals("true") && !isLogin.equals("")) {
            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(context)
                            .setContentTitle("Elderly Track")
                            .setContentText(aResident.getFullname() + " " + msg)
                            .setSmallIcon(R.drawable.icon_noti)
                            .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                                    R.mipmap.ic_launcher))
                            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                            .setStyle(new NotificationCompat.BigTextStyle()
                                    .bigText(aResident.getFullname() + " " + msg))
                            .setAutoCancel(true);

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            Intent intent = new Intent(context, ResidentDetailActivity.class);
            intent.putExtra("patient", aResident);
            stackBuilder.addNextIntent(intent);
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(
                            aResident.getId(),
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
            builder.setContentIntent(resultPendingIntent);
            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(aResident.getId(), builder.build());
        }
    }


    public static void sendNotificationForFireBase(Context context, Resident aResident, String msg) {
        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String isNoti = sharedPref.getString("isNoti-WeTrack", "true");
        String isLogin = sharedPref.getString("userToken-WeTrack", "");

        if (isNoti.equals("true") && !isLogin.equals("")) {
            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(context)
                            .setContentTitle("Elderly Track")
                            .setContentText(msg)
                            .setSmallIcon(R.drawable.icon_noti)
                            .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                                    R.mipmap.ic_launcher))
                            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                            .setStyle(new NotificationCompat.BigTextStyle()
                                    .bigText(msg))
                            .setAutoCancel(true);

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            Intent intent = new Intent(context, ResidentDetailActivity.class);
            intent.putExtra("patient", aResident);
            stackBuilder.addNextIntent(intent);
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(
                            aResident.getId(),
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
            builder.setContentIntent(resultPendingIntent);
            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(aResident.getId(), builder.build());
        }
    }


}
