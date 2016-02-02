package com.ipal.joaquimbellmunt.pawm;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.content.Context;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;


import com.google.android.gms.gcm.GcmListenerService;

public class MyGcmListenerService extends GcmListenerService {
    public static String MESSAGE = "message";

    private static final String TAG = "MyGcmListenerService";
    //private GCMNotificationIntentService mGCMNotif = new GCMNotificationIntentService();

    public void onMessageReceived(String from, Bundle data) {
        Log.d(TAG, "From: " + from);
        sendNotification(data);
    }

    private void sendNotification(Bundle data) {
        Intent intent = new Intent(this, AlertActivity.class);
        intent.putExtras(data);
        //this.startActivity(intent);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 123 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        android.support.v4.app.NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.amupadh_logo)
                .setContentTitle("Pawn Notification")
                .setContentText("New Message")
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}