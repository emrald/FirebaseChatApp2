package com.example.ti21.firebasechatapp2.fcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.service.notification.StatusBarNotification;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.ti21.firebasechatapp2.FirebaseChatMainApp;
import com.example.ti21.firebasechatapp2.R;
import com.example.ti21.firebasechatapp2.events.PushNotificationEvent;
import com.example.ti21.firebasechatapp2.ui.activities.ChatActivity;
import com.example.ti21.firebasechatapp2.utils.Constants;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    public static String uid;
    public static NotificationManager notificationManager;
    ArrayList<String> array = new ArrayList<String>();
    int Counter = 0;
    NotificationCompat.Builder notificationBuilder;
    Bitmap bitmap;
    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {


        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {


            Log.e(TAG, "Message data payload: " + remoteMessage.getData() + "..." + remoteMessage.getData().size());


            String title = remoteMessage.getData().get("title");
            String message = remoteMessage.getData().get("text");
            String username = remoteMessage.getData().get("username");
            uid = remoteMessage.getData().get("uid");
            String fcmToken = remoteMessage.getData().get("fcm_token");
            String url = remoteMessage.getData().get("url");

            array.add(uid);
            Log.e("SIZE.....", array.size() + "");
            // Don't show notification if chat activity is open.
            if (!FirebaseChatMainApp.isChatActivityOpen()) {
                sendNotification(title,
                        message,
                        username,
                        uid,
                        url,
                        fcmToken);
            } else {
                EventBus.getDefault().post(new PushNotificationEvent(title,
                        message,
                        username,
                        uid,
                        url,
                        fcmToken));
            }
        }
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     */
    private void sendNotification(String title,
                                  String message,
                                  String receiver,
                                  String receiverUid,
                                  String url,
                                  String firebaseToken
                                 ) {

        //  int ID=Integer.parseInt(receiverUid);
        //   Log.e("INT... ID...",ID+"");


        Log.e("Title..", title + "..." + url);
        Log.e("Message...", message + "..");
        Log.e("Receiver...", receiver + "....");


        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra(Constants.ARG_RECEIVER, receiver);
        intent.putExtra(Constants.ARG_RECEIVER_UID, receiverUid);
        intent.putExtra(Constants.ARG_FIREBASE_TOKEN, firebaseToken);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);


        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        if (message.equals("")) {
            try {
            URL myURL = new URL(url);
            Log.e("NULLLLLLLLLL", url+"\n"+myURL);


                bitmap = BitmapFactory.decodeStream(myURL.openConnection().getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("Eroororrr..",e.getMessage()+"");
            }
            notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_messaging)
                    .setContentTitle(title)
                 /*   .setLargeIcon(bitmap)*/
                   .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap))
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);

        } else {

            notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_messaging)
                    .setContentTitle(title)
                    .setContentText(message)
               /* .setAutoCancel(true)*/
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);
        }

        notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // notificationManager.getActiveNotifications();


        StatusBarNotification[] notifications =
                notificationManager.getActiveNotifications();

        Log.e("notifications..Length", notifications.length + "");

        Counter = notifications.length;
        Log.e("COunter..", Counter + "");
        for (StatusBarNotification notification : notifications) {
            if (notification.getId() == Counter) {
                Log.e("Exists", notification.getPackageName() + "\n" + notification.getNotification().extras.getString("android.title")
                        + "\n" + notification.getNotification().extras.getString("android.text") + "");
            }
        }


        notificationManager.notify(Counter, notificationBuilder.build());




     /*   notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);*/

        // notificationManager.notify(1, notificationBuilder.build());
    }
}