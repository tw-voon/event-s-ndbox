package com.kuchingitsolution.asus.eventmanagement.feedback_manager;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.kuchingitsolution.asus.eventmanagement.R;
import com.kuchingitsolution.asus.eventmanagement.config.CommonApiAsync;
import com.kuchingitsolution.asus.eventmanagement.config.Config;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FeedbackManagerActivity extends AppCompatActivity {

    private RecyclerView feedback_list;
    private FeedbackManagerAdapter feedbackManagerAdapter;
    private LinearLayoutManager linearLayoutManager;
    private ArrayList<FeedbackModel> feedbackModels = new ArrayList<>();
    private Toolbar toolbar;

    private AVLoadingIndicatorView avi;
    private ImageView no_result;
    private JSONObject form_data = new JSONObject();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_manager);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Event Feedback");
        }

        if(getIntent() != null){
            if(getIntent().getStringExtra("event_id") != null){

                try {
                    form_data.put("event_id", getIntent().getStringExtra("event_id"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }

        setup_element();
        get_event_feedback();
    }

    private void setup_element(){

        feedback_list = findViewById(R.id.feedback_list);
        feedbackManagerAdapter = new FeedbackManagerAdapter(this, feedbackModels);
        linearLayoutManager = new LinearLayoutManager(this);
        feedback_list.setAdapter(feedbackManagerAdapter);
        feedback_list.setLayoutManager(linearLayoutManager);

        no_result = findViewById(R.id.no_result);
        avi = findViewById(R.id.avi);

    }

    private void get_event_feedback(){
        new CommonApiAsync(this, form_data).execute(Config.GET_FEEDBACK);
    }

    public void parse_feedback(String s){

        Log.d("result from server", " Result here ===== " + s);

        try {
            JSONObject raw = new JSONObject(s);
            JSONObject feedback = raw.optJSONObject("feedback");
            JSONArray data = feedback.optJSONArray("data");

            int length = data.length();

            if(length == 0){
                no_result.setVisibility(View.VISIBLE);
                avi.hide();
                return;
            }

            for (int i = 0; i < length ; i ++){

                JSONObject item = data.getJSONObject(i);
                FeedbackModel feedbackModel = new FeedbackModel(item);
                feedbackModels.add(feedbackModel);

            }

            feedbackManagerAdapter.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        avi.hide();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

        }
        return true;

    }
}
