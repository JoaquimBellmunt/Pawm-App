package com.ipal.joaquimbellmunt.pawm;


import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;
import com.pixplicity.easyprefs.library.Prefs;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class MenuActivity extends AppCompatActivity {
    private static final String TAG = MenuActivity.class.getSimpleName();
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 1900;
    private Button Memory;
    private Button Logout;
    private OkHttpClient mClient = OkClient.getInstance();

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_menu);
        Memory = (Button) findViewById(R.id.memory);
        Logout = (Button) findViewById(R.id.logOut);
        //Logout.setEnabled(false);


        Memory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(MenuActivity.this, MemoryActivity.class);
                startActivity(intent);
                Context context = getApplicationContext();
                CharSequence text = "Wellcome to the Memory activity";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
        });
        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendLogOut("https://demo.ubismart.org/service/appBroker", "logOut",
                        Prefs.getString("Username", getString(R.string.not_found)),
                        Prefs.getString("Password", getString(R.string.not_found)),
                        Prefs.getString("Location", getString(R.string.not_found))
                );

                    Prefs.remove("Username");
                    Prefs.remove("Password");
                    Prefs.remove("Location");
                    finish();
                    Intent myIntent = new Intent(MenuActivity.this, LoginActivity.class);
                    MenuActivity.this.startActivity(myIntent);
                    Context context = getApplicationContext();
                    CharSequence text = "You have succefully logout from our server.";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();

            }
        });

        if (checkPlayServices()) {
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void sendLogOut(String url, String action, String username, String password, String location) {
        String regId = Prefs.getString("regId", getString(R.string.not_found));
        RequestBody formBody = new FormBody.Builder()
                .add("regId", regId)
                .add("action", action)
                .add("username", username)
                .add("password", password)
                .add("location", location)
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
    protected void onResume() {
        super.onResume();
        PawnIntApplication.activityResumed();
    }

    protected void onDestroy() {
        super.onDestroy();
        PawnIntApplication.activityDestroyed();
    }

    public void onStop() {
        super.onStop();
        PawnIntApplication.activitySttoped();
    }

    public void onPause() {
        super.onPause();
        PawnIntApplication.activityPaused();
    }
}

