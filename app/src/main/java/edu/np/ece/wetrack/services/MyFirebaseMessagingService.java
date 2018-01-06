package edu.np.ece.wetrack.services;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import edu.np.ece.wetrack.model.Resident;

import static edu.np.ece.wetrack.BeaconScanActivation.patientList;
import static edu.np.ece.wetrack.tasks.SendNotificationTask.sendNotificationForFireBase;

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
        try {
            for (Resident aResident : patientList) {
                if (remoteMessage.getData().get("id").equals(String.valueOf(aResident.getId()))) {
                    if (aResident.getStatus() == 1) {
                        sendNotificationForFireBase(getBaseContext(), aResident, remoteMessage.getData().get("data"));
                        aResident.setStatus(0);
                    }
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}