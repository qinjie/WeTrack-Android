package edu.np.ece.wetrack.tasks;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
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
    public static void sendNotification(Context context, String name) {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context)
                        .setContentTitle("We Track")
                        .setContentText(name)
                        .setSmallIcon(R.drawable.icon_noti)
                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                                R.drawable.icon))
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
        notificationManager.notify(999, builder.build());
    }

    public static void sendNotificationForDetected(Context context,Resident aResident, String msg) {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context)
                        .setContentTitle("We Track")
                        .setContentText(aResident.getFullname() + " " + msg)
                        .setSmallIcon(R.drawable.icon_noti)
                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                                R.drawable.icon))
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
