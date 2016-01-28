package com.ipal.joaquimbellmunt.pawm;

import android.app.AlertDialog;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory);

        Button button = (Button) findViewById(R.id.button_send);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendForm("https://joaquim.ubismart.org/service/test");
            }
        });
        mQuestion1EditText = (EditText) findViewById(R.id.edit_question_1);
        mQuestion2EditText = (EditText) findViewById(R.id.edit_question_1);

    }

    private void sendForm(String url) {
        if (validateFields()) {
            Form form = new Form(mQuestion1EditText.getText().toString(), mQuestion2EditText.getText().toString());
            String json = mGson.toJson(form);
            String token = Prefs.getString("regId", getString(R.string.not_found));
            RequestBody formBody = new FormBody.Builder()
                    .add("search", token)
                    .add("json", json)
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

    private boolean validateFields() {
        return !"".equals(mQuestion1EditText.getText().toString()) && !"".equals(mQuestion2EditText.getText().toString());
    }

}
