package com.kuchingitsolution.asus.eventmanagement.officer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kuchingitsolution.asus.eventmanagement.R;
import com.kuchingitsolution.asus.eventmanagement.config.CommonRequestAsync;
import com.kuchingitsolution.asus.eventmanagement.config.Config;
import com.kuchingitsolution.asus.eventmanagement.config.Session;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AssignOfficerActivity extends AppCompatActivity implements OptionnAssignOfficerCallback{

    private Toolbar toolbar;
    private RecyclerView assign_officer_list;
    private ImageView event_thumbnail;
    private TextView event_title, status, events_officer;
    private Session session;
    private MenuItem item;
    private AssignOfficerAdapter assignOfficerAdapter;
    private LinearLayoutManager linearLayoutManager;
    private ArrayList<AssignOfficerModel> assignOfficerModels = new ArrayList<>();

    private JSONObject form_data = new JSONObject();
    public static String TYPE = "GET_ASSIGN_OFFICER";
    public static String TYPE2 = "ASSIGN_OFFICER";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign_officer);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        session = new Session(this);

        if(getIntent() != null){
            if(getIntent().getStringExtra("event_id") != null){
                compile_data("event_id", getIntent().getStringExtra("event_id"));
            }

            if(getIntent().getStringExtra("event_title") != null){
                compile_data("event_name", getIntent().getStringExtra("event_title"));
            }

            if(getIntent().getStringExtra("event_image") != null){
                compile_data("event_image", getIntent().getStringExtra("event_image"));
            }
        }

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(form_data.optString("event_name"));
        }

        compile_data("user_id", session.getKeyValue(Session.USER_ID));
        setup_element();
        get_assigned_officer();
    }

    private void setup_element(){

        event_thumbnail = findViewById(R.id.image_thumbnail);
        event_title = findViewById(R.id.events_title);
        status = findViewById(R.id.status);
        events_officer = findViewById(R.id.events_officer);

        assign_officer_list = findViewById(R.id.officer_list);
        assignOfficerAdapter = new AssignOfficerAdapter(this, assignOfficerModels);
        linearLayoutManager = new LinearLayoutManager(this);
        assign_officer_list.setAdapter(assignOfficerAdapter);
        assign_officer_list.setLayoutManager(linearLayoutManager);

        Log.d("all_result", form_data.toString());
        Glide.with(this).load(form_data.optString("event_image")).into(event_thumbnail);
        event_title.setText(form_data.optString("event_name"));

    }

    private void get_assigned_officer(){
        compile_data("type", TYPE);
        compile_data("callback", "parse_result");
        new CommonRequestAsync(this, get_all_data()).execute(Config.GET_ALL_OFFICER);
    }

    private void assign_officer(){
        compile_data("type", TYPE);
        compile_data("callback", "parse_submit_request");
        new CommonRequestAsync(this, get_all_data()).execute(Config.ASSIGN_OFFICER);
    }

    private void compile_data(String key, String value){

        try {
            form_data.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private BroadcastReceiver listener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent ) {
            String data = intent.getStringExtra("result");
            String type = intent.getStringExtra("type");
            Log.d( "Received data : ", "data here : " + data);
            //showMessage(data);

            if(type.equals("parse_result"))
                parse_result(data);
            else if(type.equals("parse_submit_request"))
                parse_submit_request(data);

        }
    };

    private void parse_result(String s){

        try {
            JSONObject data = new JSONObject(s);

            // view assigned officer
            JSONArray officers = data.optJSONArray("officer");
            if(officers.length() > 0){
                status.setText("ASSIGNED");
                JSONObject officer = officers.optJSONObject(0);
                JSONObject officer_info = officer.optJSONObject("user");

                events_officer.setText(officer_info.optString("name"));
            } else {
                status.setText("UNASSIGNED");
                events_officer.setText("No Officer assign yet");
            }

            // view officer list
            JSONArray officer_list = data.getJSONArray("officer_list");
            int length = officer_list.length();

            for (int i = 0; i < length ; i++){

                JSONObject officer = officer_list.getJSONObject(i);
                JSONObject user = officer.optJSONObject("user");
                AssignOfficerModel assignOfficerModel = new AssignOfficerModel(user.optString("id"), user.optString("name"), "");
                assignOfficerModels.add(assignOfficerModel);
            }

            assignOfficerAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void parse_submit_request(String s){
        if(s.equals("success")){
            onBackPressed();
        } else {
            showMessage("Error occur", "common");
        }
    }

    private void showMessage(String message, final String type){
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(type.equals("assign_officer"))
                    assign_officer();
                dialog.dismiss();
            }
        });
        alertDialogBuilder.show();
    }

    private JSONObject get_all_data(){
        return this.form_data;
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(listener,
                new IntentFilter(TYPE));
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(listener);
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.submit, menu);
        item = menu.findItem(R.id.submit);
        item.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (itemId == R.id.submit){
            showMessage(getString(R.string.assign_event_officer), "assign_officer");
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void OnOfficerSelect(String user_id, Boolean checked) {
        item.setVisible(checked);
        compile_data("officer_id", user_id);
    }
}
