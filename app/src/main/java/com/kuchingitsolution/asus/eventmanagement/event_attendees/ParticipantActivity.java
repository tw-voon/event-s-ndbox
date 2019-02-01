package com.kuchingitsolution.asus.eventmanagement.event_attendees;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.kuchingitsolution.asus.eventmanagement.R;
import com.kuchingitsolution.asus.eventmanagement.config.CommonApiAsync;
import com.kuchingitsolution.asus.eventmanagement.config.Config;
import com.kuchingitsolution.asus.eventmanagement.config.Session;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ParticipantActivity extends AppCompatActivity {

    private ArrayList<ParticipantModel> participantModels = new ArrayList<>();
    private JSONObject form_data = new JSONObject();
    private Toolbar toolbar;
    private RecyclerView participant_list;
    private ParticipantAdapter participantAdapter;
    private ImageView no_content;
    private AVLoadingIndicatorView avi;
    private Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participant);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("People who join");
        }

        if(getIntent() != null) {
            if(getIntent().getStringExtra("event_id") != null){
                compile_form_data("event_id", getIntent().getStringExtra("event_id"));
            }
        }

        session = new Session(this);

        participant_list = findViewById(R.id.participant_recycler_view);
        participantAdapter = new ParticipantAdapter(this, participantModels);
        participant_list.setAdapter(participantAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        participant_list.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(participant_list.getContext(),
                linearLayoutManager.getOrientation());
        participant_list.addItemDecoration(dividerItemDecoration);
        no_content = findViewById(R.id.no_result);
        avi = findViewById(R.id.avi);

        get_participant_list();
    }

    private void compile_form_data(String key, String value){

        try {
            form_data.put(key,value);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void get_participant_list(){
        no_content.setVisibility(View.GONE);
        new CommonApiAsync(this, form_data).execute(Config.GET_PARTICIPANT);
    }

    public void parse_participant_response(String result){

        try {
            JSONObject object = new JSONObject(result);
            JSONArray data = object.getJSONArray("attended_user");
            int length = data.length();

            for (int i = 0 ; i < length ; i++){

                JSONObject participant = data.getJSONObject(i);
                ParticipantModel participantModel = new ParticipantModel(participant.optString("name"), participant.optInt("user_id"));
                participantModels.add(participantModel);
            }

            participantAdapter.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        avi.hide();
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
