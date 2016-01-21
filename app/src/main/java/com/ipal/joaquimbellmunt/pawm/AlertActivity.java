package com.ipal.joaquimbellmunt.pawm;


import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.gson.Gson;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

//import com.github.nkzawa.socketio.client.IO;
//import com.github.nkzawa.socketio.client.Socket;
//import com.github.nkzawa.emitter.Emitter;


public class AlertActivity extends AppCompatActivity {
    private static final String TAG = AlertActivity.class.getSimpleName();
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 1900;
    private BroadcastReceiver mBroadcastReceiver;

    private Button mButton;
    private ImageButton mimgButton;
    private Gson mGson = new Gson();
    private Socket mSocket;
    private OkHttpClient mClient = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //mSocket.on("new message", onNewMessage);
        //mSocket.connect();
        setContentView(R.layout.activity_alert);
        mimgButton = (ImageButton) findViewById(R.id.photo);
        mButton = (Button) findViewById(R.id.button);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //sendRequest("http://joaquim.ubismart.org/service/test");
                sendRequest("https://api.instagram.com/v1/tags/nofilter/media/recent?client_id=ec725a6a49ce4a4686cb2c8a1ed6413f&count=2");
                //mSocket.emit("test", "Hello World");
            }
        });
        mimgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mimgButton.setImageDrawable(null);
            }
        });

        try {
            mSocket = IO.socket("http://joaquim.ubismart.org");
        } catch (URISyntaxException e) {
            this.finish();
            return;
        }

        mSocket.on("test", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                if (args.length > 0) {
                    AlertActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //mAdapter.add((String) args[0]);
                            //mAdapter.notifyDataSetChanged();
                            //mListaMensagens.smoothScrollToPosition(mAdapter.getCount() - 1);
                        }
                    });
                }
            }
        });

        mSocket.connect();


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Alert alert = mGson.fromJson(bundle.getString("data"), Alert.class);
            if (alert.getServiceName().equals("MedicationReminder")) {
                mimgButton.setImageResource(R.drawable.medication_reminder);
            }
            if (alert.getServiceName().equals("WaterReminder")) {
                mimgButton.setImageResource(R.drawable.water_glass);
            }
        }


        if (checkPlayServices()) {
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
    }

    private void sendRequest(String url) {
        Request request = new Request.Builder()
                .url(url)
                .build();
        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException throwable) {
                throwable.printStackTrace();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

//                Headers responseHeaders = response.headers();
//                for (int i = 0; i < responseHeaders.size(); i++) {
//                    System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
//                }

                showResponse(response.body().string());
            }
        });
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */

//    private Socket mSocket;
//
//    {
//        try {
//            mSocket = IO.socket("https://joaquim.ubismart.org/service/test");
//        } catch (URISyntaxException e) {
//        }
//    }
    private void showResponse(String result) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.alert_dialog_title)
                .setMessage(result)
//                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        // continue with delete
//                    }
//                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        });
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i("Test", "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    protected void onDestroy() {
        super.onDestroy();

        if (mSocket != null)
            mSocket.disconnect();
    }
}
