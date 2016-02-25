package com.ipal.joaquimbellmunt.pawm;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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


public class MemoryActivity extends AppCompatActivity {
    private EditText mQuestion1EditText, mQuestion2EditText;
    private Gson mGson = new Gson();
    private OkHttpClient mClient = OkClient.getInstance();
    private Button send_form, menu_memory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory);
        mQuestion1EditText = (EditText) findViewById(R.id.edit_question_1);
        mQuestion2EditText = (EditText) findViewById(R.id.edit_question_1);
        send_form = (Button) findViewById(R.id.send_form);
        menu_memory = (Button) findViewById(R.id.menu_memory);


        send_form.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendForm("https://joaquim.ubismart.org/service/appBroker", "form");
                finish();
                Intent myIntent = new Intent(MemoryActivity.this, MenuActivity.class);
                MemoryActivity.this.startActivity(myIntent);
                Context context = getApplicationContext();
                CharSequence text = "Form sent to the server";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
        });

        menu_memory.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MemoryActivity.this, MenuActivity.class);
                startActivity(intent);
            }
        });
    }

    protected void onResume() {
        super.onResume();
        PawnIntApplication.activityResumed();
    }

    private void sendForm(String url, String action) {
        if (validateFields()) {
            Form form = new Form(mQuestion1EditText.getText().toString(), mQuestion2EditText.getText().toString());
            String json = mGson.toJson(form);
            String regId = Prefs.getString("regId", getString(R.string.not_found));
            RequestBody formBody = new FormBody.Builder()
                    .add("regId", regId)
                    .add("json", json)
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
                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response);

                    Utils.showResponse(MemoryActivity.this, response.body().string());

                }
            });

        } else {
            Toast.makeText(this, getString(R.string.fields_error), Toast.LENGTH_LONG).show();
        }
    }

    public void onStop() {
        super.onStop();
        PawnIntApplication.activitySttoped();
    }

    public void onPause() {
        super.onPause();
        PawnIntApplication.activityPaused();
    }

    private boolean validateFields() {
        return !"".equals(mQuestion1EditText.getText().toString()) && !"".equals(mQuestion2EditText.getText().toString());
    }

}
