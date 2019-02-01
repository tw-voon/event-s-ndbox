package com.kuchingitsolution.asus.eventmanagement.officer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.kuchingitsolution.asus.eventmanagement.config.Api_post_get_method;
import com.kuchingitsolution.asus.eventmanagement.config.Session;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;

public class SearchOfficerAsync extends AsyncTask<String,Integer,String> {

    private OkHttpClient client;
    private Session session;
    private JSONObject form_data;
    private LocalBroadcastManager localBroadcastManager;

    @SuppressLint("StaticFieldLeak")
    private SearchOfficerActivity searchOfficerActivity;

    SearchOfficerAsync(SearchOfficerActivity searchOfficerActivity, JSONObject form_data){
        this.searchOfficerActivity = searchOfficerActivity;
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

        switch (form_data.optString("type")){
            case "search":
                searchOfficerActivity.parse_result(s);
                break;
            case "select":
                searchOfficerActivity.parse_selected_user(s);
                break;
        }

    }
}
