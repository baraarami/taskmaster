package taskmaster.service;

import com.google.firebase.messaging.FirebaseMessagingService;

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;


import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.amazonaws.mobileconnectors.pinpoint.targeting.notification.NotificationClient;
import com.amazonaws.mobileconnectors.pinpoint.targeting.notification.NotificationDetails;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.taskmaster.app.MainActivity;
import com.taskmaster.view.MainActivity;

import java.util.HashMap;

public class PushListenerService extends FirebaseMessagingService {
    public static final String TAG = PushListenerService.class.getSimpleName();

    public static final String ACTION_PUSH_NOTIFICATION = "push-notification";

    public static final String INTENT_SNS_NOTIFICATION_FROM = "from";
    public static final String INTENT_SNS_NOTIFICATION_DATA = "data";

    public static final String CHANNEL_ID = "CHANNEL_ID";


    @Override
    public void OnNewToken(String token){
        super.OnNewToken(token);

        Log.e(TAG, "Registering push notification token " + token );
        MainActivity.getPointManager(getApplicationContext()).getNotificationClient().registerDeviceToken(token);
    }

    @Override
    public void OnMessageReceived(RemoteMessage remoteMessage){
        super.OnMessageReceived(remoteMessage);
        Log.e(TAG, "Message"+ remoteMessage.getData() );


        final Notification notificationClinte = MainActivity.getPointManager(getApplicationContext()).getNotificationClient();

        final NotificationDetails notificationDetails = NotificationDetails.builder()
                .from(remoteMessage.getFrom))
                .mapData(remoteMessage.getData))
                .intentAction(NotificationClient.FCM_INTENT_ACTION)
                .build();

        NotificationClient.CampaignPushResult pushResult = notificationClinte.handelCampaignPush(notificationDetails);

        if (!NotificationClient.CampaignPushResult.NOT_HANDELD.equals(pushResult)){
            /**
             The push message was due to a Pinpoint campaign.
             If the app was in the background, a local notification was added
             in the notification center. If the app was in the foreground, an
             event was recorded indicating the app was in the foreground,
             for the demo, we will broadcast the notification to let the main
             activity display it in a dialog.
             */
            if (NotificationClient.CampaignPushResult.APP_IN_FOREGROUND.equals(pushResult)) {
                /* Create a message that will display the raw data of the campaign push in a dialog. */
                final HashMap<String, String> dataMap = new HashMap<>(remoteMessage.getData());
                broadcast(remoteMessage.getFrom(), dataMap);
            }
            return;
        }
    }

    private void broadcast(final String from, final HashMap<String, String> dataMap) {
        Intent intent = new Intent(ACTION_PUSH_NOTIFICATION);
        intent.putExtra(INTENT_SNS_NOTIFICATION_FROM, from);
        intent.putExtra(INTENT_SNS_NOTIFICATION_DATA, dataMap);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }



    /**
     * Helper method to extract push message from bundle.
     *
     * @param data bundle
     * @return message string from push notification
     */
    public static String getMessage(Bundle data) {
        return ((HashMap) data.get("data")).toString();
    }
}


