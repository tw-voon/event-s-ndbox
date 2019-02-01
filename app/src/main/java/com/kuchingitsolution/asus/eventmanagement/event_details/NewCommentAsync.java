package com.kuchingitsolution.asus.eventmanagement.event_details;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

import com.kuchingitsolution.asus.eventmanagement.config.Api_post_get_method;
import com.kuchingitsolution.asus.eventmanagement.config.Config;
import com.kuchingitsolution.asus.eventmanagement.config.Session;

import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;

public class NewCommentAsync extends AsyncTask<String,Integer,String> {

    @SuppressLint("StaticFieldLeak")
    private DetailEventsActivity detailEventsActivity;

    private OkHttpClient client;
    private String form_data, event_id, type;
    private Session session;
    private Float rating;

    public NewCommentAsync(DetailEventsActivity detailEventsActivity, String form_data, Float rating, String event_id, String type){
        this.detailEventsActivity = detailEventsActivity;
        this.form_data = form_data;
        this.event_id = event_id;
        this.type = type;
        this.rating = rating;
        session = new Session(detailEventsActivity);
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
                .addFormDataPart("message", form_data)
                .addFormDataPart("event_id", event_id)
                .addFormDataPart("rating", String.valueOf(rating))
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

        if(type.equals("new_comment"))
            detailEventsActivity.parse_comment_response(s);
        else if(type.equals("new_feedback")){
            detailEventsActivity.parse_feedback(s);
        }
    }
}
