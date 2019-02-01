package com.kuchingitsolution.asus.eventmanagement.officer;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;

import com.kuchingitsolution.asus.eventmanagement.config.Api_post_get_method;
import com.kuchingitsolution.asus.eventmanagement.config.Session;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;

public class RemoveOfficerAsync extends AsyncTask<String,Integer,String> {

    private OkHttpClient client;
    private Session session;
    private String request_id = "", type = "";
    private int position;

    @SuppressLint("StaticFieldLeak")
    private MyOfficerActivity myOfficerActivity;

    RemoveOfficerAsync(MyOfficerActivity myOfficerActivity, String request_id, String type, int position) {
        this.myOfficerActivity = myOfficerActivity;
        session = new Session(myOfficerActivity);
        this.request_id = request_id;
        this.type = type;
        this.position = position;
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
                .addFormDataPart("request_id", request_id);

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
        myOfficerActivity.refresh_fragment_info(type, position);
    }
}
