package com.kuchingitsolution.asus.eventmanagement.new_event;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.kuchingitsolution.asus.eventmanagement.config.Api_post_get_method;
import com.kuchingitsolution.asus.eventmanagement.config.Config;
import com.kuchingitsolution.asus.eventmanagement.config.CountingRequestBody;
import com.kuchingitsolution.asus.eventmanagement.config.Session;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class UploadInfoEvent extends AsyncTask<String,Integer,String> {

    @SuppressLint("StaticFieldLeak")
    private InformationActivity informationActivity;
    private List<ImageModel> imageModels = new ArrayList<>();
    private JSONObject form_data = new JSONObject();

    private Context context;
    private OkHttpClient client;
    private Session session;

    public UploadInfoEvent(InformationActivity informationActivity, JSONObject form_data, List<ImageModel> imageModels){
        this.informationActivity = informationActivity;
        this.form_data = form_data;
        this.imageModels = imageModels;
        session = new Session(informationActivity.getApplicationContext());
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        OkHttpClient.Builder b = new OkHttpClient.Builder();
        b.readTimeout(300, TimeUnit.SECONDS);
        b.writeTimeout(400, TimeUnit.SECONDS);
        b.retryOnConnectionFailure(false); // Don't retry the connection (prevent twice entry)
        client = b.build();
        informationActivity.upload_start();

    }

    @Override
    protected String doInBackground(String... strings) {

        MultipartBody.Builder multipartBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("event_title", form_data.optString("title"))
                .addFormDataPart("event_description", form_data.optString("desc"))
                .addFormDataPart("event_extra", form_data.optString("extra_info"))
                .addFormDataPart("event_category", form_data.optString("category_id"))
                .addFormDataPart("event_location", form_data.optString("location"))
                .addFormDataPart("agent_id", session.getKeyValue(Session.USER_ID))
                .addFormDataPart("event_venue", form_data.optString("location_name"))
                .addFormDataPart("venue_address", form_data.optString("address"))
                .addFormDataPart("venue_latitude", form_data.optString("latitude"))
                .addFormDataPart("venue_longitude", form_data.optString("longitude"));

        int image_size = imageModels.size();

        for(int i = 0; i < image_size; i++){
            if(!imageModels.get(i).getPath().isEmpty()){
                File image_file = new File(imageModels.get(i).getPath());
                multipartBody.addFormDataPart("image_" + i , image_file.getName(), RequestBody.create(MediaType.parse("image/png"), image_file));
                Log.d("to server", " Image name ---- " + image_file.getName() );
            }
        }

        MultipartBody requestBody = multipartBody.build();

        CountingRequestBody monitoring = new CountingRequestBody(requestBody, new CountingRequestBody.Listener() {
            @Override
            public void onRequestProgress(long bytesWritten, long contentLength) {

                float percentage = 100f * bytesWritten / contentLength;
                if (percentage >= 0) {
                    publishProgress((int)percentage);
                    Log.d("upload", String.valueOf(percentage));
                }
            }
        });

        try {
            return Api_post_get_method.POST(client, Config.UPLOAD_NEW_INFO_EVENT, monitoring);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        informationActivity.update_progress(values);
//        dialog_upload.setProgress(values[0]);
//        dialog_upload_status.setText(String.valueOf(values[0]));
//        if(values[0] == 100) {
//            status_done.setVisibility(View.VISIBLE);
//            dialog_upload.setIndeterminate(true);
//        }
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Log.d("result from server", "result : " + s);
        informationActivity.upload_done(s);
        //ad.dismiss();
    }
}
