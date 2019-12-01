package com.kuchingitsolution.asus.eventmanagement.my_event;

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

public class GetEventAsyncList extends AsyncTask<String,Integer,String> {

    private OkHttpClient client;
    private Session session;
    private String type = "";
    private int page;
    private LocalBroadcastManager localBroadcastManager;

    public GetEventAsyncList(Context context,String type, int page) {
        localBroadcastManager = LocalBroadcastManager.getInstance(context);
        session = new Session(context);
        this.type = type;
        this.page = page;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        OkHttpClient.Builder b = new OkHttpClient.Builder();
        b.readTimeout(300, TimeUnit.SECONDS);
        b.writeTimeout(400, TimeUnit.SECONDS);
        b.retryOnConnectionFailure(false); // Don't retry the connection (prevent twice entry)
        client = b.build();
        Log.d("user id here", session.getKeyValue(Session.USER_ID));
    }

    @Override
    protected String doInBackground(String... url) {

        MultipartBody.Builder multipartBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("user_id", session.getKeyValue(Session.USER_ID))
                .addFormDataPart("page", String.valueOf(page))
                .addFormDataPart("category_id", String.valueOf(session.getIdValue("category_id")));

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

        // Create intent with action
        Intent localIntent = new Intent(type);
        localIntent.putExtra("result", s);

        // Send local broadcast
        localBroadcastManager.sendBroadcast(localIntent);
    }
}
