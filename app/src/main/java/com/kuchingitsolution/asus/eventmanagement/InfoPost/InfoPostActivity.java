package com.kuchingitsolution.asus.eventmanagement.InfoPost;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.kuchingitsolution.asus.eventmanagement.config.AppStatus;
import com.kuchingitsolution.asus.eventmanagement.config.CommonApiAsync;
import com.kuchingitsolution.asus.eventmanagement.config.Config;
import com.kuchingitsolution.asus.eventmanagement.config.DB;
import com.kuchingitsolution.asus.eventmanagement.config.EndlessScrollListener;
import com.kuchingitsolution.asus.eventmanagement.config.Session;
import com.kuchingitsolution.asus.eventmanagement.feed.FeedModel;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class InfoPostActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    private JSONObject form_data = new JSONObject();
    private InfoPostAdapter infoPostAdapter;
    private RecyclerView info_post;
    private ArrayList<FeedModel> feedModels = new ArrayList<>();
    private SwipeRefreshLayout swipeRefreshLayout;
    private EndlessScrollListener endlessScrollListener;
    private AVLoadingIndicatorView avi;
    private Session session;
    private ImageView no_result;
    private DB db;
    private String title = "Event Management", category_id;
    private int page;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_post);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();

        if(intent != null){
            title = intent.getStringExtra("category_name");
            category_id = intent.getStringExtra("category_id");
        }

        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle(title);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        setup_element();
    }

    private void setup_element(){

        session = new Session(this);
        db = new DB(this);

        no_result = findViewById(R.id.no_result);
        avi = findViewById(R.id.avi);
        swipeRefreshLayout = findViewById(R.id.event_refresh);

        info_post = findViewById(R.id.information_list);
        infoPostAdapter = new InfoPostAdapter(this, feedModels);
        info_post.setAdapter(infoPostAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        info_post.setLayoutManager(linearLayoutManager);

        endlessScrollListener = new EndlessScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int pages, int totalItemsCount, RecyclerView view) {
                Log.d("category - ", String.valueOf(pages));
                if(pages > 1){
                    page = pages;
                    swipeRefreshLayout.setRefreshing(true);
                    get_selected_information();
                }

            }
        };

        info_post.setNestedScrollingEnabled(false);
        swipeRefreshLayout.setOnRefreshListener(this);
        session.setKeyValue("current_position", null);
        session.setKeyValue("current_event", null);

        get_selected_information();
    }

    public void OnEventLike(String event_id, int position){

        try {

            form_data.put("event_id", event_id);
            form_data.put("user_id", session.getKeyValue(Session.USER_ID));
            form_data.put("position", position);
            form_data.put("type", "like");

            new CommonApiAsync(this, form_data).execute(Config.LIKE_EVENT);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void get_selected_information(){

        try {

            form_data.put("type", "category_post");
            form_data.put("category_id", String.valueOf(category_id));

            Log.d("categories -- ", category_id);

            new CommonApiAsync(this, form_data).execute(Config.GET_INFOR_BY_CATEGORY);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void choose_type(String type, String result){

        switch (type){
            case "like":
                add_like(result, form_data.optInt("position"), form_data.optString("event_id"));
                break;
            case "category_post":
                parse_events(result);
                break;
        }

    }

    private void add_like(String s, int position, String event_id){
        Log.d("result", s + " -- ");

        if(s == null){
            Toast.makeText(this, "Error occur, Please try again later", Toast.LENGTH_SHORT).show();
        } else {
            try {
                JSONObject likes = new JSONObject(s);
                feedModels.get(position).setSupport_this(likes.optInt("status"));
                feedModels.get(position).setSupport(likes.optInt("total_like"));
                infoPostAdapter.notifyItemChanged(position);
                db.update_response(event_id,likes.optInt("status"), likes.optInt("total_like"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void parse_events(final String result){

        if(result == null){
            avi.hide();
            no_result.setImageResource(R.drawable.generic_error);
            no_result.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setRefreshing(false);
            return;
        }

        final int current_size = infoPostAdapter.getItemCount();
        Handler handler = new Handler();
        Log.d("result here", result);
        final Runnable r = new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject data = new JSONObject(result);
                    JSONObject raw = data.getJSONObject("events");
                    JSONArray responses = data.optJSONArray("response");
                    JSONArray completed_event = raw.optJSONArray("data");
                    int completed_events_size = completed_event.length();
                    if(completed_events_size > 0){

                        for (int i = 0 ; i < completed_events_size ; i ++ ){
                            JSONObject each_event = completed_event.getJSONObject(i);
                            JSONArray response = responses.optJSONArray(i);
                            FeedModel myEventModel = new FeedModel(each_event,response);
                            db.insertComplaint(each_event,response);
                            feedModels.add(myEventModel);
                        }

                        infoPostAdapter.notifyItemRangeInserted(current_size, feedModels.size());
                    } else {
                        avi.hide();
                        if(page == 1 || (completed_events_size == 0 && current_size == 0)){
                            no_result.setImageResource(R.drawable.bookmark_empty_3);
                            no_result.setVisibility(View.VISIBLE);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        handler.post(r);
        avi.hide();
        swipeRefreshLayout.setRefreshing(false);

    }

    @Override
    public void onRefresh() {

        if(feedModels.size() > 0) {
            feedModels.clear();
            infoPostAdapter.notifyDataSetChanged();
            page = 1;
        }

        swipeRefreshLayout.setRefreshing(true);
        get_selected_information();
    }

    @Override
    protected void onResume() {
        super.onResume();
        info_post.addOnScrollListener(endlessScrollListener);
        update_viewed_item();
    }

    private void update_viewed_item(){
        String position = session.getKeyValue("current_position");
        String viewed_event = session.getKeyValue("current_event");
        Log.d("changes", "current position: " + position + " current_event : " + viewed_event);
        if(position != null && viewed_event != null && !viewed_event.equals("0")){
            String changes = db.get_event_changes(viewed_event);
            Log.d("changes here", changes);

            try {
                JSONArray jsonArray = new JSONArray(changes);
                JSONObject info = jsonArray.getJSONObject(0);

                feedModels.get(Integer.valueOf(position)).setSupport(info.optInt("support"));
                feedModels.get(Integer.valueOf(position)).setSupport_this(info.optInt("support_this"));
                infoPostAdapter.notifyItemChanged(Integer.valueOf(position));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

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
