package com.ipal.joaquimbellmunt.pawm;


import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;
import com.pixplicity.easyprefs.library.Prefs;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class AlertActivity extends AppCompatActivity {
    private static final String TAG = AlertActivity.class.getSimpleName();
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 1900;
    private Button mButton;
    private Button m2Button;
    private Button m3Button;
    private Button m4Button;
    private ImageButton mimgButton;
    private Gson mGson = new Gson();
    private OkHttpClient mClient = OkClient.getInstance();
    private Boolean allowUnsafeSsl = true;
    private Socket mSocket;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    private Emitter.Listener onNewMessage = new Emitter.Listener() {

        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(AlertActivity.this, "Message", Toast.LENGTH_LONG).show();
                    JSONObject data = (JSONObject) args[0];
                    String username;
                    String message;
                    try {
                        username = data.getString("username");
                        message = data.getString("message");
                    } catch (JSONException e) {
                        return;
                    }
                    Utils.showResponse(AlertActivity.this, message);
                }
            });
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_alert);
        mimgButton = (ImageButton) findViewById(R.id.photo);
        mButton = (Button) findViewById(R.id.button);
        m2Button = (Button) findViewById(R.id.button2);
        m3Button = (Button) findViewById(R.id.button3);
        m4Button = (Button) findViewById(R.id.button4);
        //Http Post
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendHttp("https://joaquim.ubismart.org/service/test");
            }
        });
        //Socket Emit
        m2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(AlertActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        //Socket Rec
        m3Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AlertActivity.this, MemoryActivity.class);
                startActivity(intent);
                //Intent intent = new Intent(AlertActivity.this, MemoryActivity.class);
            }
        });
        //Http Get
        m4Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendHttp("https://joaquim.ubismart.org/service/test");
            }
        });
        mimgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mimgButton.setImageDrawable(null);
            }
        });


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
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void sendHttp(String url) {
        String regId = Prefs.getString("regId", getString(R.string.not_found));
        RequestBody formBody = new FormBody.Builder()
                .add("search", regId)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();
        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                Utils.showResponse(AlertActivity.this, response.body().string());

            }
        });
    }


    private void sendEmit() {
        mSocket.emit("message", "test");

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

        mSocket.disconnect();
        //mSocket.off("new message", onNewMessage);
    }
}
