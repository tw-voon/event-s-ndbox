package com.kuchingitsolution.asus.eventmanagement.bookmark;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;

import com.kuchingitsolution.asus.eventmanagement.config.Api_post_get_method;
import com.kuchingitsolution.asus.eventmanagement.config.Session;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;

public class BookmarkAsync extends AsyncTask<String,Integer,String> {

    private OkHttpClient client;
    private Session session;
    private JSONObject form_data;

    @SuppressLint("StaticFieldLeak")
    private BookmarkActivity bookmarkActivity;

    BookmarkAsync(BookmarkActivity bookmarkActivity, JSONObject form_data){
        this.bookmarkActivity = bookmarkActivity;
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
            case "GET_BOOKMARKED":
                bookmarkActivity.parse_bookmark_result(s);
                break;
            case "REMOVE_BOOKMARK":
                bookmarkActivity.remove_bookmark(s);
                break;
        }

    }
}
