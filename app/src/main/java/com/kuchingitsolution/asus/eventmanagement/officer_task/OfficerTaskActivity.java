package com.kuchingitsolution.asus.eventmanagement.officer_task;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.kuchingitsolution.asus.eventmanagement.R;
import com.kuchingitsolution.asus.eventmanagement.config.CommonApiAsync;
import com.kuchingitsolution.asus.eventmanagement.config.Config;
import com.kuchingitsolution.asus.eventmanagement.config.Session;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class OfficerTaskActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Session session;
    private RecyclerView task_list;
    private OfficerTaskAdapter officerTaskAdapter;
    private ArrayList<OfficerTaskModel> officerTaskModelArrayList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private JSONObject form_data = new JSONObject();
    private ImageView no_result;
    private AVLoadingIndicatorView avi;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_officer_task);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        session = new Session(this);

        setup_action_bar();

        setup_element();
        get_task_list();
    }

    private void setup_action_bar(){

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            if(session.getKeyValue(Session.ROLE_CODE).equals("2")) {
                getSupportActionBar().setTitle(R.string.title_task);
                compile_form_data("type", "GET_MY_TASK");
                url = Config.GET_OFFICER_TASK;
            } else if(session.getKeyValue(Session.ROLE_CODE).equals("5")){
                getSupportActionBar().setTitle(R.string.title_joined_event);
                compile_form_data("type", "GET_MY_EVENT");
                url = Config.GET_JOINED_EVENT;
            }
        }

        compile_form_data("user_id", session.getKeyValue(Session.USER_ID));

    }

    private void get_task_list(){
        new CommonApiAsync(this, get_form_data()).execute(url);
    }

    public void parse_task_result(String s){

        Log.d("result from server ", " : is here : "+ s);

        try {
            JSONObject raw = new JSONObject(s);
            JSONArray task = raw.getJSONArray("assigned_task");
            int length = task.length();

            if(length == 0){
                no_result.setVisibility(View.VISIBLE);
                avi.hide();
                return;
            }

            for (int i = 0; i < length ; i++){
                JSONObject assigned = task.getJSONObject(i);
                OfficerTaskModel officerTaskModel = new OfficerTaskModel(assigned);
                officerTaskModelArrayList.add(officerTaskModel);
            }

            officerTaskAdapter.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        avi.hide();
    }

    public void parse_joined_event(String s){

        Log.d("result from server ", " : is here : "+ s);

        if(s == null){
            avi.hide();
            Toast.makeText(this, "Error while joined event. Please make sure your internet connection is good", Toast.LENGTH_SHORT).show();
            no_result.setVisibility(View.VISIBLE);
            return;
        }

        try {
            JSONObject raw = new JSONObject(s);
            JSONArray task = raw.getJSONArray("joined_events");
            int length = task.length();

            if(length == 0){
                no_result.setVisibility(View.VISIBLE);
                avi.hide();
                return;
            }

            for (int i = 0; i < length ; i++){
                JSONObject assigned = task.getJSONObject(i);
                OfficerTaskModel officerTaskModel = new OfficerTaskModel(assigned);
                officerTaskModelArrayList.add(officerTaskModel);
            }

            officerTaskAdapter.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        avi.hide();

    }

    private void compile_form_data(String key, String value){

        try {
            form_data.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private JSONObject get_form_data(){
        return this.form_data;
    }

    private void setup_element(){

        task_list = findViewById(R.id.task_list);
        officerTaskAdapter = new OfficerTaskAdapter(this, officerTaskModelArrayList);
        task_list.setAdapter(officerTaskAdapter);
        linearLayoutManager = new LinearLayoutManager(this);
        task_list.setLayoutManager(linearLayoutManager);

        avi = findViewById(R.id.avi);
        no_result = findViewById(R.id.no_result);

        avi.show();
        no_result.setVisibility(View.GONE);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }
}
