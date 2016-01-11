package com.ipal.joaquimbellmunt.pawm;


import android.content.Intent;
import android.os.Bundle;
import android.content.Context;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;


import com.google.android.gms.gcm.GcmListenerService;

public class MyGcmListenerService extends GcmListenerService {
    public static String MESSAGE = "message";

    private static final String TAG = "MyGcmListenerService";
    //private GCMNotificationIntentService mGCMNotif = new GCMNotificationIntentService();

    public void onMessageReceived(String from, Bundle data) {
        Log.d(TAG, "From: " + from);
        Intent intent = new Intent(MESSAGE);
        intent.putExtras(data);
        //GCMNotificationIntentService.class.getName();
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}