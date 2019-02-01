package com.kuchingitsolution.asus.eventmanagement.config;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;

import com.kuchingitsolution.asus.eventmanagement.new_event.CategoryActivity;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ApiAsyncTask extends AsyncTask<String,Integer,String>{

    @SuppressLint("StaticFieldLeak")
    private CategoryActivity categoryActivity;


    public ApiAsyncTask(CategoryActivity categoryActivity){
        this.categoryActivity = categoryActivity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... url) {

        Response response = null;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url[0])
                .build();
        try {
            response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        categoryActivity.updateList(s);
    }
}
