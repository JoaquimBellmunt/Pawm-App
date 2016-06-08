package com.ipal.joaquimbellmunt.pawm;


import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
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
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = AlertActivity.class.getSimpleName();
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 1900;
    private Button update;
    private Button menu;
    private Button alert;
    private TextView title, question;
    private Spinner mLocationView;
    private Spinner mHouseView;
    private List<String> list;

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

        setContentView(R.layout.activity_register);

        update = (Button) findViewById(R.id.update);

        defaultLocations();
        defaultHouses();


        mLocationView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                Toast.makeText(arg0.getContext(), "Location: " + arg0.getItemAtPosition(arg2).toString(), Toast.LENGTH_SHORT).show();
                Prefs.putString("Location", arg0.getItemAtPosition(arg2).toString());
            }


            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        mHouseView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                Toast.makeText(arg0.getContext(), "Location: " + arg0.getItemAtPosition(arg2).toString(), Toast.LENGTH_SHORT).show();
                Prefs.putString("Location", arg0.getItemAtPosition(arg2).toString());
            }


            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        update.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick (View v){
            sendHttp("https://demo.ubismart.org/service/appBroker", "appRegister");
            finish();
            Intent myIntent = new Intent(RegisterActivity.this, MenuActivity.class);
            RegisterActivity.this.startActivity(myIntent);
            Context context = getApplicationContext();
            CharSequence text = "Help is on his way!";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
        }

        );


        Bundle bundle = getIntent().getExtras();
        if(bundle!=null)

        {
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


        if(

        checkPlayServices()

        )

        {
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client=new GoogleApiClient.Builder(this).

        addApi(AppIndex.API)

        .

        build();
    }

    private void defaultLocations() {
        mLocationView = (Spinner) findViewById(R.id.spinnerLocation);
        list = new ArrayList<String>();
        mLocationView = (Spinner) this.findViewById(R.id.spinnerLocation);
        list.add("kitchen");
        list.add("bedroom");
        list.add("bathroom");
        list.add("Mobile");
        ArrayAdapter<String> adaptador = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mLocationView.setAdapter(adaptador);
    }

    private void defaultHouses() {
        mHouseView = (Spinner) findViewById(R.id.spinnerHouse);
        list = new ArrayList<String>();
        mHouseView = (Spinner) this.findViewById(R.id.spinnerHouse);
        list.add("kitchen");
        list.add("bedroom");
        list.add("bathroom");
        list.add("Mobile");
        ArrayAdapter<String> adaptador = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mLocationView.setAdapter(adaptador);
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

                Utils.showResponse(RegisterActivity.this, response.body().string());

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

    private boolean sendInfo(String email, String password, String location, String house, String url, String action) {
        String regId = Prefs.getString("regId", null);
        RequestBody formBody = new FormBody.Builder()
                .add("regId", regId)
                .add("action", action)
                .add("username", email)
                .add("password", password)
                        //.add("location", location)
                .add("os","Android")
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();
        try {
            Response response = mClient.newCall(request).execute();
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            //Utils.showResponse(LoginActivity.this, response.body().string());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
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
