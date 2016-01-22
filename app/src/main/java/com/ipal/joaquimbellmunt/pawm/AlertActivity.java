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
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

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
    private Button m2Button;
    private Button m3Button;
    private Button m4Button;
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
        m2Button = (Button) findViewById(R.id.button2);
        m3Button = (Button) findViewById(R.id.button3);
        m4Button = (Button) findViewById(R.id.button4);
        //Http Post
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendHttpPost("https://joaquim.ubismart.org/service/test");
            }
        });
        //Socket Emit
        m2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mSocket.emit("test", "Hello World");
            }
        });
        //Socket Rec
        m3Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        //Http Get
        m4Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //sendRequest("https://joaquim.ubismart.org/service/test");
                sendHttpGet("https://api.instagram.com/v1/tags/nofilter/media/recent?client_id=ec725a6a49ce4a4686cb2c8a1ed6413f&count=2");
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

    private void sendHttpGet(String url) {
        Request request = new Request.Builder()
                .url(url)
                .build();
        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException throwable) {
                throwable.printStackTrace();
            }

            @Override
            public void onResponse(final Response response) throws IOException {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

//                Headers responseHeaders = response.headers();
//                for (int i = 0; i < responseHeaders.size(); i++) {
//                    System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
//                }

                showResponse(response.body().string());

            }
        });
    }

    private void sendHttpPost(final String url) {
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run(){
                HttpClient mHttp = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(url);

                try {
                    HttpResponse response = mHttp.execute(httpPost);
                    // write response to log
                    Log.d("Http Post Response:", response.toString());
                } catch (ClientProtocolException e) {
                    // Log exception
                    e.printStackTrace();
                } catch (IOException e) {
                    // Log exception
                    e.printStackTrace();
                }
                //code to do the HTTP request
            }
        });
        thread.start();
    }

    private void showResponse(final String result) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(AlertActivity.this)
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
