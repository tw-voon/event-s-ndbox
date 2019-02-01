package com.kuchingitsolution.asus.eventmanagement.config;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.kuchingitsolution.asus.eventmanagement.EventManagementApplication;
import com.kuchingitsolution.asus.eventmanagement.HomeActivity;
import com.kuchingitsolution.asus.eventmanagement.InfoPost.InfoPostActivity;
import com.kuchingitsolution.asus.eventmanagement.R;
import com.kuchingitsolution.asus.eventmanagement.apps_info.AppsInfoActivity;
import com.kuchingitsolution.asus.eventmanagement.auth.ForgotPasswordActivity;
import com.kuchingitsolution.asus.eventmanagement.auth.LoginActivity;
import com.kuchingitsolution.asus.eventmanagement.auth.MoreInfoActivity;
import com.kuchingitsolution.asus.eventmanagement.auth.RegisterActivity;
import com.kuchingitsolution.asus.eventmanagement.config.Api_post_get_method;
import com.kuchingitsolution.asus.eventmanagement.config.Config;
import com.kuchingitsolution.asus.eventmanagement.event_attendees.ParticipantActivity;
import com.kuchingitsolution.asus.eventmanagement.event_attendees.TakeAttendanceActivity;
import com.kuchingitsolution.asus.eventmanagement.event_details.DetailEventsActivity;
import com.kuchingitsolution.asus.eventmanagement.event_details.RegisterEventActivity;
import com.kuchingitsolution.asus.eventmanagement.event_details.TicketActivity;
import com.kuchingitsolution.asus.eventmanagement.event_vip.EventVipActivity;
import com.kuchingitsolution.asus.eventmanagement.feedback_manager.FeedbackManagerActivity;
import com.kuchingitsolution.asus.eventmanagement.new_event.EditEventActivity;
import com.kuchingitsolution.asus.eventmanagement.officer_task.OfficerTaskActivity;
import com.kuchingitsolution.asus.eventmanagement.profile.AddressActivity;
import com.kuchingitsolution.asus.eventmanagement.profile.CountryStateActivity;
import com.kuchingitsolution.asus.eventmanagement.profile.MyProfileActivity;
import com.kuchingitsolution.asus.eventmanagement.search.SearchActivity;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;

public class CommonApiAsync extends AsyncTask<String,Integer,String>{

    @SuppressLint("StaticFieldLeak")
    private Context context;

    private OkHttpClient client;
    private AlertDialog.Builder alert;
    private AlertDialog ad;
    private JSONObject form_data;

    public CommonApiAsync(Context context, JSONObject form_data){
        this.context = context;
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

        if(s != null){
            Log.d("overall here", " - " + s + " - " + form_data.toString());
        }
        Log.d("my result", form_data.toString());
        if(context instanceof LoginActivity){
            ((LoginActivity) context).login_response(s);
        } else if(context instanceof RegisterActivity){
            ((RegisterActivity) context).parse_register_result(s);
        } else if(context instanceof HomeActivity){
            ((HomeActivity) context).parse_responses(s, form_data.optString("type"));
        } else if(context instanceof DetailEventsActivity){
            switch (form_data.optString("type")) {
                case "join_event":
                    ((DetailEventsActivity) context).is_join(s);
                    break;
                case "delete_event":
                    ((DetailEventsActivity) context).is_deleted(s);
                    break;
            }
        } else if(context instanceof EventVipActivity){
            switch (form_data.optString("type")) {
                case "get_list":
                    ((EventVipActivity) context).parse_list(s);
                    break;
                case "search":
                    ((EventVipActivity) context).parse_result(s);
                    break;
                case "select":
                    ((EventVipActivity) context).parse_invitation(s);
                    break;
            }
        } else if(context instanceof FeedbackManagerActivity){
            ((FeedbackManagerActivity) context).parse_feedback(s);
        } else if(context instanceof SearchActivity){
            ((SearchActivity) context).parse_search_result(s, form_data.optString("type"));
        } else if(context instanceof OfficerTaskActivity){

            switch (form_data.optString("type")){
                case "GET_MY_TASK":
                    ((OfficerTaskActivity) context).parse_task_result(s);
                    break;
                case "GET_MY_EVENT":
                    ((OfficerTaskActivity) context).parse_joined_event(s);
                    break;
            }

        } else if(context instanceof MyProfileActivity){
            ((MyProfileActivity) context).parse_profile_info(s);
        } else if(context instanceof AppsInfoActivity){
            ((AppsInfoActivity) context).parse_apps_info(s);
        } else if(context instanceof ForgotPasswordActivity){
            ((ForgotPasswordActivity) context).parse_reset_response(s);
        } else if(context instanceof  ParticipantActivity){
            ((ParticipantActivity) context).parse_participant_response(s);
        } else if(context instanceof InfoPostActivity){
            ((InfoPostActivity) context).choose_type(form_data.optString("type"), s);
        } else if(context instanceof MoreInfoActivity){
            ((MoreInfoActivity) context).parse_resposne(form_data.optString("type"), s);
        } else if (context instanceof RegisterEventActivity){
            ((RegisterEventActivity) context).parse_response(form_data.optString("type"), s);
        } else if(context instanceof CountryStateActivity){
            ((CountryStateActivity) context).parse_response(form_data.optString("type"), s);
        } else if(context instanceof AddressActivity){
            ((AddressActivity) context).parse_response(s);
        } else if(context instanceof EditEventActivity){
            ((EditEventActivity) context).parse_response(s);
        } else if(context instanceof TicketActivity){
            ((TicketActivity) context).response(s);
        } else if( context instanceof TakeAttendanceActivity) {
            ((TakeAttendanceActivity) context).parseResponse(s);
        }
    }
}
