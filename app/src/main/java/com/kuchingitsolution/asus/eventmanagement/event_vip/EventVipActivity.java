package com.kuchingitsolution.asus.eventmanagement.event_vip;

import android.annotation.TargetApi;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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

public class EventVipActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private SearchView searchView = null;
    private AVLoadingIndicatorView avi;
    private ImageView no_result;
    private TextView result_label;
    private RecyclerView search_vip_list;
    private EventInvitedVipAdapter eventInvitedVipAdapter;
    private LinearLayoutManager linearLayoutManager;

    private CardView result_card;
    private ImageView img_profile;
    private TextView vip_name, vip_status;
    private JSONObject form_data = new JSONObject();
    private Session session;
    private LinearLayout found_section, not_found_section;
    private ArrayList<EventVipModel> eventVipModels = new ArrayList<>();
    private String event_id, event_title;
    private Button add_vip, invite_vip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_vip);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getIntent() != null){
            event_id = getIntent().getStringExtra("event_id");
            event_title = getIntent().getStringExtra("event_title");
            compile_data("type", "get_list");
            compile_data("event_id", event_id);
        }

        if(getSupportActionBar() != null ){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        setup_element();
        get_vip_list();
    }

    private void setup_element(){

        avi = findViewById(R.id.avi);
        no_result = findViewById(R.id.no_result);
        result_label = findViewById(R.id.result_label);
        result_card = findViewById(R.id.result_card);
        session = new Session(this);
        found_section = findViewById(R.id.found_section);
        not_found_section = findViewById(R.id.not_found_section);
        vip_name = findViewById(R.id.vip_name);
        vip_status = findViewById(R.id.vip_status);
        add_vip = findViewById(R.id.add_vip);
        invite_vip = findViewById(R.id.invite_vip);

        result_label.setVisibility(View.GONE);
        result_card.setVisibility(View.GONE);

        search_vip_list = findViewById(R.id.search_vip);
        eventInvitedVipAdapter = new EventInvitedVipAdapter(this, eventVipModels);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        search_vip_list.setLayoutManager(linearLayoutManager);
        search_vip_list.setAdapter(eventInvitedVipAdapter);

        invite_vip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                invite_user();
            }
        });

        add_vip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                send_invitation();
            }
        });

    }

    private void compile_data(String key, String value){
        try {
            form_data.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void get_vip_list(){
        Log.d("request parameter", " == " + form_data.toString());
        new CommonApiAsync(this, form_data).execute(Config.GET_VIP_LIST);
    }

    public void parse_list(String s){
        Log.d("result from server", s + " =========== ");

        if(s == null) {
            Toast.makeText(this, "Error had occur, Please try again later", Toast.LENGTH_SHORT).show();
            return;
        }

        if(eventVipModels.size() > 0)
            eventVipModels.clear();

        try {
            JSONObject raw = new JSONObject(s);
            JSONArray event_vip = raw.optJSONArray("event_vip");
            int length = event_vip.length();

            if(length == 0){
                avi.hide();
                return;
            }

            for (int i = 0; i < length ; i ++){

                JSONObject invited = event_vip.optJSONObject(i);
                JSONObject user = invited.optJSONObject("user");

                Log.d("user == ", " == " + user.toString());

                EventVipModel eventVipModel = new EventVipModel(user.optString("name"), invited.optString("status_id"), user.optString("id"), "");
                eventVipModels.add(eventVipModel);

            }

            eventInvitedVipAdapter.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        avi.hide();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // Get search query and create object of class AsyncFetch
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            if (searchView != null) {
                searchView.clearFocus();
            }
            //loading.setVisibility(View.VISIBLE);
            Log.d("query", "Query : " + query);
//            query_data(query);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // adds item to action bar
        getMenuInflater().inflate(R.menu.search_menu, menu);

        // Get Search item from action bar and Get Search service
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager) EventVipActivity.this.getSystemService(Context.SEARCH_SERVICE);
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            assert searchManager != null;
            searchView.setSearchableInfo(searchManager.getSearchableInfo(EventVipActivity.this.getComponentName()));
            searchView.setIconified(false);
            searchView.setMaxWidth(Integer.MAX_VALUE);

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    if(!Patterns.EMAIL_ADDRESS.matcher(query).matches()) {
                        display_status("Wrong email format");
                    } else if(query.equals(session.getKeyValue(Session.EMAIL))){
                        display_status("Please enter others than yourself email");
                    } else {
                        compile_data("email", query);
                        compile_data("event_id", event_id);
                        compile_data("user_id", session.getKeyValue(Session.USER_ID));
                        compile_data("roles_id", "3");
                        compile_data("type", "search");
                        search_vip();
                    }
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    result_label.setVisibility(View.GONE);
                    result_card.setVisibility(View.GONE);
                    no_result.setVisibility(View.GONE);
                    return false;
                }
            });
        }

        return true;
    }

    private void search_vip(){
        new CommonApiAsync(this, form_data).execute(Config.GET_OFFICER);
    }

    public void parse_result(String s){
        Log.d("result from server", " ===== " + s);

        try {
            JSONArray jsonArray = new JSONArray(s);
            int length = jsonArray.length();
            result_card.setVisibility(View.VISIBLE);
            result_label.setVisibility(View.VISIBLE);

            if(length == 0){
                not_found_section.setVisibility(View.VISIBLE);
                found_section.setVisibility(View.GONE);
                form_data = new JSONObject();
                return;
            }

            JSONObject data = jsonArray.optJSONObject(0);

            vip_name.setText(data.optString("name"));
            vip_status.setText(data.optString("email"));

            compile_data("selected_user_id", data.optString("id"));
            compile_data("name", data.optString("name"));
            compile_data("email", data.optString("email"));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        avi.hide();
    }

    private void send_invitation(){
        compile_data("roles_id", "3");
        compile_data("type", "select");
        new CommonApiAsync(this, form_data).execute(Config.SELECT_USER);
    }

    public void parse_invitation(String s){

        Log.d("result here", " === " + s);

        switch (s) {
            case "success":
                EventVipModel eventVipModel = new EventVipModel(form_data.optString("name"), "0", form_data.optString("selected_user_id"), "");
                eventVipModels.add(eventVipModel);
                eventInvitedVipAdapter.notifyDataSetChanged();
                restore_default();
                display_status("Successfully added. Awaiting the user to accept.");
                break;
            case "vip":
                display_status("This user already is VIP for this event");
                break;
            case "officer":
                display_status("This user is an officer");
                break;
        }
    }

    private void restore_default(){
        result_card.setVisibility(View.GONE);
        result_label.setVisibility(View.GONE);
    }

//    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void invite_user(){

        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP)
            share.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);

        share.putExtra(Intent.EXTRA_SUBJECT,  "Invitation for Joining an Event : " + event_title);
        share.putExtra(Intent.EXTRA_TEXT, "**Put Your Extra Message Here** \n Please click the link below to download the apps. https://e3v3a.app.goo.gl/rTCx");
        startActivity(Intent.createChooser(share, "Share"));
    }

    private void display_status(String message){
        Snackbar snackbar = Snackbar
                .make(findViewById(R.id.new_vip), Html.fromHtml("<font color=\"#ffffff\">"+ message +"</font>"), Snackbar.LENGTH_LONG);
        View snackBarView = snackbar.getView();
        if(Build.VERSION.SDK_INT >= 23 )
            snackBarView.setBackgroundColor(getResources().getColor(R.color.mt_red, null));
        else
            snackBarView.setBackgroundColor(getResources().getColor(R.color.mt_red));
        snackbar.show();
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
