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
import com.squareup.okhttp.ws.WebSocketCall;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;


import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

import java.net.URI;
import java.util.concurrent.TimeUnit;


import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.github.nkzawa.emitter.Emitter;

import org.json.JSONException;
import org.json.JSONObject;


public class AlertActivity extends AppCompatActivity {
    private static final String TAG = AlertActivity.class.getSimpleName();
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 1900;
    private Button mButton;
    private Button m2Button;
    private Button m3Button;
    private Button m4Button;
    private ImageButton mimgButton;
    private Gson mGson = new Gson();
    private OkHttpClient mClient;
    private Boolean allowUnsafeSsl = true;
    private Socket mSocket;

    {
        try {
            //mSocket = IO.socket("https://joaquim.ubismart.org");
            mSocket = IO.socket("http://chat.socket.io");
        } catch (URISyntaxException e) {
        }
    }

    private Emitter.Listener onNewMessage = new Emitter.Listener() {

        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String username;
                    String message;
                    try {
                        username = data.getString("username");
                        message = data.getString("message");
                    } catch (JSONException e) {
                        return;
                    }
                    showResponse(message);
                }
            });
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mClient = getUnsafeOkHttpClient();
        mSocket.on("new message", onNewMessage);
        mSocket.connect();

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
                mSocket.emit("new message", "test");
                //mSocket.emit("service/test", "test");
                //sendEmit();
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
    }

    private OkHttpClient getUnsafeOkHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        ;
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext;

            sslContext = SSLContext.getInstance("SSL");

            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            builder.sslSocketFactory(sslSocketFactory);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

        return builder.build();
    }

    private void sendHttp(String url) {
        RequestBody formBody = new FormBody.Builder()
                .add("search", String.valueOf(R.string.RegId))
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

                showResponse(response.body().string());

            }
        });
    }

    private void sendEmit() {
        mSocket.emit("service/test", "test");
        //mSocket.connect();
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

        mSocket.disconnect();
        //mSocket.off("new message", onNewMessage);
    }
}
