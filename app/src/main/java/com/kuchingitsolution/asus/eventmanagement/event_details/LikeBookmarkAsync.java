package com.kuchingitsolution.asus.eventmanagement.event_details;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import com.kuchingitsolution.asus.eventmanagement.config.Api_post_get_method;
import com.kuchingitsolution.asus.eventmanagement.config.Config;
import com.kuchingitsolution.asus.eventmanagement.config.Session;

import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;

public class LikeBookmarkAsync extends AsyncTask<String,Integer,String> {

    @SuppressLint("StaticFieldLeak")
    private DetailEventsActivity detailEventsActivity;

    private String event_id, type;
    private OkHttpClient client;
    private Session session;

    public LikeBookmarkAsync(DetailEventsActivity detailEventsActivity, String event_id, String type){
        this.detailEventsActivity = detailEventsActivity;
        this.event_id = event_id;
        session = new Session(detailEventsActivity);
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
                .addFormDataPart("event_id", event_id)
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
        detailEventsActivity.like_bookmarked_event(s, type);
    }
}
