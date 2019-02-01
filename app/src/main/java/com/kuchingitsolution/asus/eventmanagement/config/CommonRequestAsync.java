package com.kuchingitsolution.asus.eventmanagement.config;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;

public class CommonRequestAsync extends AsyncTask<String,Integer,String>  {

    private OkHttpClient client;
    private Session session;
    private JSONObject form_data;
    private LocalBroadcastManager localBroadcastManager;

    @SuppressLint("StaticFieldLeak")
    private Context context;

    public CommonRequestAsync(Context context, JSONObject form_data){
        this.context = context;
        this.form_data = form_data;
        localBroadcastManager = LocalBroadcastManager.getInstance(context);
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
                .setType(MultipartBody.FORM);

        Iterator<String> item = form_data.keys();
        while (item.hasNext()){
            String key = item.next();
            String value = form_data.optString(key);
            multipartBody.addFormDataPart(key,value);
            Log.d(key + " is here", "value is here " + value);
        }

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
        Log.d("search result", " : " + s);

        // Create intent with action
        Intent localIntent = new Intent(form_data.optString("type"));
        localIntent.putExtra("result", s);
        localIntent.putExtra("type", form_data.optString("callback"));

        // Send local broadcast
        localBroadcastManager.sendBroadcast(localIntent);

    }
}
