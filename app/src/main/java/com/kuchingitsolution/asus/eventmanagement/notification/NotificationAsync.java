package com.kuchingitsolution.asus.eventmanagement.notification;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.Log;

import com.kuchingitsolution.asus.eventmanagement.config.Api_post_get_method;
import com.kuchingitsolution.asus.eventmanagement.config.Session;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;

public class NotificationAsync extends AsyncTask<String,Integer,String> {

    private OkHttpClient client;
    private Session session;
    private String type = "";
    private LocalBroadcastManager localBroadcastManager;

    @SuppressLint("StaticFieldLeak")
    private Context context;

    NotificationAsync(Context context,String type) {
        this.context = context;
        localBroadcastManager = LocalBroadcastManager.getInstance(context);
        session = new Session(context);
        this.type = type;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        OkHttpClient.Builder b = new OkHttpClient.Builder();
        b.readTimeout(300, TimeUnit.SECONDS);
        b.writeTimeout(400, TimeUnit.SECONDS);
        b.retryOnConnectionFailure(false); // Don't retry the connection (prevent twice entry)
        client = b.build();

    }

    @Override
    protected String doInBackground(String... url) {

        MultipartBody.Builder multipartBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("user_id", session.getKeyValue(Session.USER_ID));

        MultipartBody requestBody = multipartBody.build();

        try {
            return Api_post_get_method.POST(client, url[0], requestBody);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Log.d("result here: ", s + " ");

        // Create intent with action
        Intent localIntent = new Intent(type);
        localIntent.putExtra("result", s);

        // Send local broadcast
        localBroadcastManager.sendBroadcast(localIntent);

    }
}
