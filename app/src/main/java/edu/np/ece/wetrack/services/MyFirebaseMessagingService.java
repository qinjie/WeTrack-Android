package edu.np.ece.wetrack.services;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import static edu.np.ece.wetrack.tasks.SendNotificationTask.sendNotification;

/**
 * Created by hoanglong on 08-Feb-17.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FCM Service";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO: Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated.
//        Toast.makeText(getBaseContext(),remoteMessage.getNotification().getBody(),Toast.LENGTH_SHORT).show();
        try {
            sendNotification(getBaseContext(), remoteMessage.getNotification().getBody());
            Log.d(TAG, "From: " + remoteMessage.getFrom());
            Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}