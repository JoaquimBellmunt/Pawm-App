package com.ipal.joaquimbellmunt.pawm;


import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
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
    private Button ack;
    private Button menu;
    private Button alert;
    private TextView title, question;

    private ImageButton mimgButton;
    private Gson mGson = new Gson();
    private OkHttpClient mClient = OkClient.getInstance();


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_alert);
        mimgButton = (ImageButton) findViewById(R.id.photo);
        ack = (Button) findViewById(R.id.button);
        menu = (Button) findViewById(R.id.button2);
        alert = (Button) findViewById(R.id.button3);
        title = (TextView) findViewById(R.id.textView);
        question = (TextView) findViewById(R.id.textView2);


        ack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendHttp("https://joaquim.ubismart.org/service/appBroker", "appAck");
                finish();
                Intent myIntent = new Intent(AlertActivity.this, MenuActivity.class);
                AlertActivity.this.startActivity(myIntent);
                Context context = getApplicationContext();
                CharSequence text = "Help is on his way!";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
        });
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AlertActivity.this, MenuActivity.class);
                startActivity(intent);
            }
        });
        alert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendHttp("https://joaquim.ubismart.org/service/appBroker", "appAlert");
                finish();
                Intent myIntent = new Intent(AlertActivity.this, MenuActivity.class);
                AlertActivity.this.startActivity(myIntent);
                Context context = getApplicationContext();
                CharSequence text = "Thank you for your cooperation";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
        });



        mimgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Alert alert = mGson.fromJson(bundle.getString("data"), Alert.class);
            if (alert.getServiceName().equals("MedicationReminder")) {
                mimgButton.setImageResource(R.drawable.medication_reminder);
                title.setText("Action Reminder: Medication");
                question.setText("Have you taken your medicine yet?");
            }
            if (alert.getServiceName().equals("WaterReminder")) {
                mimgButton.setImageResource(R.drawable.water_glass);
                title.setText("Action Reminder: Hydration");
                question.setText("Have you drunk anything?");
            }
            if (alert.getServiceName().equals("FallAlert")) {
                mimgButton.setImageResource(R.drawable.fall);
                title.setText("Alert: FALL!!!");
                question.setText("Do you need assistance?");
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

    private void sendHttp(String url, String action) {
        String regId = Prefs.getString("regId", getString(R.string.not_found));
        RequestBody formBody = new FormBody.Builder()
                .add("regId", regId)
                .add("action", action)
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
        PawnIntApplication.activityDestroyed();
    }

    public void onStop() {
        super.onStop();
        PawnIntApplication.activitySttoped();
    }

    public void onResume() {
        super.onResume();
        PawnIntApplication.activityResumed();
    }

    public void onPause() {
        super.onPause();
        PawnIntApplication.activityPaused();
    }
}
