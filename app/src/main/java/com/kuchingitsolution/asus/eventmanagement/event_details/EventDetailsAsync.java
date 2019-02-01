package com.kuchingitsolution.asus.eventmanagement.event_details;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;

import com.kuchingitsolution.asus.eventmanagement.config.Api_post_get_method;
import com.kuchingitsolution.asus.eventmanagement.config.Config;

import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;

public class EventDetailsAsync extends AsyncTask<String,Integer,String> {

    @SuppressLint("StaticFieldLeak")
    private DetailEventsActivity detailEventsActivity;

    private OkHttpClient client;
    private JSONObject form_data;

    public EventDetailsAsync(DetailEventsActivity detailEventsActivity, JSONObject form_data){
        this.detailEventsActivity = detailEventsActivity;
        this.form_data = form_data;
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
    protected String doInBackground(String... strings) {

        MultipartBody.Builder multipartBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("event_id", form_data.optString("event_id"))
                .addFormDataPart("user_id", form_data.optString("user_id"));

        MultipartBody requestBody = multipartBody.build();

        try {
            return Api_post_get_method.POST(client, Config.EVENT_GET_DETAILS, requestBody);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Log.d("result from server ", ": " + s);
        detailEventsActivity.parse_event_details(s);
    }
}
