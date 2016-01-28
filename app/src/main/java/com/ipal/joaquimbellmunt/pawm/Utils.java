package com.ipal.joaquimbellmunt.pawm;


import android.app.Activity;
import android.app.AlertDialog;

public class Utils {
    public static void showResponse(final Activity activity, final String result) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(activity)
                        .setTitle(R.string.alert_dialog_title)
                        .setMessage(result)
//                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        // continue with delete
//                    }
//                })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });

    }
}
