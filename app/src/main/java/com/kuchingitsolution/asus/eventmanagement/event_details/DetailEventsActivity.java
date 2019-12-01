package com.kuchingitsolution.asus.eventmanagement.event_details;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CalendarContract;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.codemybrainsout.ratingdialog.RatingDialog;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.kuchingitsolution.asus.eventmanagement.HomeActivity;
import com.kuchingitsolution.asus.eventmanagement.ImageFullScreenActivity;
import com.kuchingitsolution.asus.eventmanagement.R;
import com.kuchingitsolution.asus.eventmanagement.auth.LoginActivity;
import com.kuchingitsolution.asus.eventmanagement.config.AppStatus;
import com.kuchingitsolution.asus.eventmanagement.config.CommonApiAsync;
import com.kuchingitsolution.asus.eventmanagement.config.Config;
import com.kuchingitsolution.asus.eventmanagement.config.DB;
import com.kuchingitsolution.asus.eventmanagement.config.Helper;
import com.kuchingitsolution.asus.eventmanagement.config.Session;
import com.kuchingitsolution.asus.eventmanagement.event_attendees.ParticipantActivity;
import com.kuchingitsolution.asus.eventmanagement.event_attendees.TakeAttendanceActivity;
import com.kuchingitsolution.asus.eventmanagement.event_vip.EventVipActivity;
import com.kuchingitsolution.asus.eventmanagement.feedback_manager.FeedbackFormActivity;
import com.kuchingitsolution.asus.eventmanagement.feedback_manager.FeedbackManagerActivity;
import com.kuchingitsolution.asus.eventmanagement.map.ActivityViewOnMap;
import com.kuchingitsolution.asus.eventmanagement.message.ChatActivity;
import com.kuchingitsolution.asus.eventmanagement.new_event.EditEventActivity;
import com.kuchingitsolution.asus.eventmanagement.officer.AssignOfficerActivity;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class DetailEventsActivity extends AppCompatActivity implements BaseSliderView.OnSliderClickListener, EventDetailsBottomSheet.OptionBottomSheetCallback, ContactListOptionCallback{

    private JSONObject form_data = new JSONObject();
    private TextView events_title, events_time, events_location, event_category, events_desc,
            event_extra_info_label, event_extra_info, host_label, host_name, host_desc, event_comments, user_join, vip_join;
    private SliderLayout bannerSlider;
    private PopupWindow mPopupWindow, feedback_Window;
    private CoordinatorLayout coordinatorLayout;
    private Session session;
    private JSONArray comment_section, event_media;
    private JSONObject user_post_data = new JSONObject();
    private EventDetailsBottomSheet eventDetailsBottomSheet;
    private DB db;

    private CommentsAdapter commentsAdapter;
    private ArrayList<CommentsModel> commentsModels = new ArrayList<>();
    private ArrayList<ContactListModel> contactListModels = new ArrayList<>();
    private EditText comment_content;
    private Button event_register;
    private Menu menu;
    private CardView event_details;
    private AVLoadingIndicatorView avi;

    private FloatingActionButton like, bookmark;
    private Float rating;
    private int is_completed_event, event_type;
    private boolean is_open_register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_events);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }
        session = new Session(this);
        db = new DB(this);

        if(!session.getStatus(Session.LOGGED_IN)){
            Intent intent = new Intent(DetailEventsActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }

        if (getIntent() != null) {

            try {
                form_data.put("event_id", getIntent().getStringExtra("event_id"));
                form_data.put("user_id", session.getKeyValue(Session.USER_ID));
                int position = getIntent().getIntExtra("position", 0);
                session.setKeyValue("current_event", getIntent().getStringExtra("event_id"));
                session.setKeyValue("current_position", String.valueOf(position));
                Log.d("changes details", "current position: " + position + " current_event : " + getIntent().getStringExtra("event_id"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        setup_view_element();

        like = findViewById(R.id.like);
        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LikeBookmarkAsync likeBookmarkAsync = new LikeBookmarkAsync(DetailEventsActivity.this, form_data.optString("event_id"), "like");
                likeBookmarkAsync.execute(Config.LIKE_EVENT);
            }
        });

        bookmark = findViewById(R.id.bookmark);
        bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LikeBookmarkAsync likeBookmarkAsync = new LikeBookmarkAsync(DetailEventsActivity.this, form_data.optString("event_id"), "bookmark");
                likeBookmarkAsync.execute(Config.BOOKMARK_EVENT);
            }
        });

        get_event_details();

    }

    private void setup_view_element(){

        events_title = findViewById(R.id.events_title);
        events_time = findViewById(R.id.events_time);
        events_location = findViewById(R.id.events_location);
        event_category = findViewById(R.id.event_category);
        events_desc = findViewById(R.id.events_desc);
        event_extra_info_label = findViewById(R.id.event_extra_info_label);
        event_extra_info = findViewById(R.id.event_extra_info);
        host_label = findViewById(R.id.host_label);
        host_name =findViewById(R.id.host_name);
        host_desc = findViewById(R.id.host_desc);
        event_register = findViewById(R.id.event_register);
        user_join = findViewById(R.id.event_user);
        vip_join = findViewById(R.id.event_vip);
        event_details = findViewById(R.id.event_details);
        avi = findViewById(R.id.avi);

        coordinatorLayout = findViewById(R.id.details_layout);

        // header banner
        bannerSlider = findViewById(R.id.slider);
        bannerSlider.stopAutoCycle();

        // comment recycler view
        event_comments = findViewById(R.id.comment_label);

        event_comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                show_comment_popout();
            }
        });
        event_details.setVisibility(View.GONE);
        avi.show();
    }

    private void get_event_details(){
        EventDetailsAsync eventDetailsAsync = new EventDetailsAsync(this, form_data);
        eventDetailsAsync.execute();
    }

    @SuppressLint("RestrictedApi")
    public void parse_event_details(String result){

        if(result == null){
            avi.hide();
            Toast.makeText(this, "Error while loading events. Please make sure your internet connection is good", Toast.LENGTH_SHORT).show();
            onBackPressed();
            return;
        }

        if(result.isEmpty()){
            avi.hide();
            Toast.makeText(this, "Error while loading events. Please make sure your internet connection is good", Toast.LENGTH_SHORT).show();
            onBackPressed();
            return;
        }

        try {
            // raw json data from server
            JSONObject raw_data = new JSONObject(result);

            // get only JSON data from events
            JSONArray event_data = raw_data.getJSONArray("events_data");
            final JSONObject event_details = event_data.getJSONObject(0);

            JSONObject category_data = event_details.optJSONObject("category");
            event_media = event_details.optJSONArray("media");
//            db.insertEventImage(event_media);

            final JSONObject location_info = event_details.optJSONObject("locations");

            event_type = event_details.getInt("type_id");

            switch (event_type){
                case 1:
                    render_normal_events(raw_data);
                    break;
                case 2:
                    host_label.setVisibility(View.GONE);
                    host_name.setVisibility(View.GONE);
                    host_desc.setVisibility(View.GONE);
                    vip_join.setVisibility(View.GONE);
                    events_time.setVisibility(View.GONE);
                    break;
            }

            JSONArray event_response = raw_data.optJSONArray("event_responses");

            if(event_response.length() > 0){
                JSONObject response = event_response.optJSONObject(0);

                if(response.optString("support").equals("1"))
                    like.setImageResource(R.drawable.ic_favorite_black_24dp);

                if(response.optString("bookmark").equals("1"))
                    bookmark.setImageResource(R.drawable.ic_bookmark_black_24dp);
            }

            if(contactListModels.size() > 0){
                contactListModels.clear();
            }

            // contact section
            JSONObject officer = event_details.optJSONObject("officer");
            if(officer != null){
                JSONObject officer_details = officer.getJSONObject("user");
                ContactListModel contactListModel = new ContactListModel(
                        officer_details.optString("id"),
                        "Officer",
                        officer_details.optString("name"));
                contactListModels.add(contactListModel);
            }

            comment_section = raw_data.optJSONArray("event_comments");

            int comment_length = comment_section.length();
            String comment_label;

            if(comment_length > 0){
                comment_label = String.format("Comments (%S)", comment_length);
            } else {
                comment_label = "No comment yet.";
            }
            event_comments.setText(comment_label);

            if(!bannerSlider.isActivated()){
                Handler handler = new Handler();
                handler.postDelayed(runnable,1000);
            }

            events_title.setText(event_details.getString("title"));
            events_desc.setText(event_details.getString("description"));
            is_completed_event = event_details.optInt("status_id");
            Log.d("is_completed_event", is_completed_event + "   --- ");

            form_data.put("title", event_details.getString("title"));
            form_data.put("desc", event_details.getString("description"));

            event_category.setText(category_data.optString("category_name"));

            String event_extra = event_details.optString("extra_info");
            if(!event_extra.isEmpty()){
                event_extra_info.setText(event_extra);
            } else {
                event_extra_info.setVisibility(View.GONE);
                event_extra_info_label.setVisibility(View.GONE);
            }

            final String location_details = String.format("%S\n%S", location_info.optString("name"), location_info.optString("address"));
            events_location.setText(location_details);
            form_data.put("location", location_info.optString("name"));

            events_location.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(DetailEventsActivity.this, ActivityViewOnMap.class);
                    intent.putExtra("lat", Double.valueOf(location_info.optString("lat")));
                    intent.putExtra("lon", Double.valueOf(location_info.optString("lon")));
                    intent.putExtra("title", event_details.optString("title"));
                    startActivity(intent);
                }
            });

            if(is_completed_event == 0){
                event_comments.setVisibility(View.GONE);
                like.setVisibility(View.GONE);
                bookmark.setVisibility(View.GONE);
                menu.findItem(R.id.action_more).setVisible(false);
            }

            this.event_details.setVisibility(View.VISIBLE);

        } catch (JSONException e) {
            Toast.makeText(this, "Error while loading events or this event had been removed.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            onBackPressed();
        }

        avi.hide();
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            setup_header_image(event_media);
            bannerSlider.setCurrentPosition(0,true);
        }
    };

    private void to_participant_list(){
        Intent intent = new Intent(this, ParticipantActivity.class);
        intent.putExtra("event_id", form_data.optString("event_id"));
        startActivity(intent);
    }

    private void render_normal_events(JSONObject raw_data){

        try {
            JSONArray organiser_section = raw_data.optJSONArray("events_host");
            JSONObject organiser_data = organiser_section.optJSONObject(0);
            JSONObject agency = organiser_data.optJSONObject("agency");
            JSONObject owner_data = organiser_data.optJSONObject("user");
            user_post_data.put("owner_id", owner_data.optString("id"));
            is_open_register = raw_data.optBoolean("register_available", false);

            // event host
            host_name.setText(agency.optString("name"));
            host_desc.setText(agency.optString("description"));
            ContactListModel contactListModel = new ContactListModel(
                    owner_data.optString("id"),
                    "Event Agency",
                    owner_data.optString("name"));
            contactListModels.add(contactListModel);

            int user_joins = Integer.valueOf(raw_data.optString("user_event", "0"));
            int vip_joins = Integer.valueOf(raw_data.optString("vip_event", "0"));

            if(user_joins > 0){
                String user_joining = String.format("%s user are joining this event", user_joins);
                user_join.setText(user_joining);
                user_join.setVisibility(View.VISIBLE);
                user_join.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        to_participant_list();
                    }
                });
            }

            if(vip_joins > 0){
                String vip_joining = String.format("%s VIP are joining this event", vip_joins);
                vip_join.setText(vip_joining);
                vip_join.setVisibility(View.VISIBLE);
            }

            // temporary disabled VIP
            vip_join.setVisibility(View.GONE);

            JSONArray event_data = raw_data.getJSONArray("events_data");
            JSONObject event_details = event_data.getJSONObject(0);

            String start_date = event_details.getString("start_time");
            String end_date = event_details.getString("end_time");
            String register_start_date = event_details.getString("register_start_time");
            String register_end_date = event_details.getString("register_end_time");

            form_data.put("beginTime", start_date);
            form_data.put("endTime", end_date);
            form_data.put("register_start_date", register_start_date);
            form_data.put("register_end_time", register_end_date);

            String event_duration = String.format("%S - %S", Helper.get_date(start_date) , Helper.get_date(end_date));
            events_time.setText(event_duration);

            if(session.getKeyValue(Session.ROLE_CODE).equals("5")){

//                if(event_open_register){
                event_register.setVisibility(View.VISIBLE);
//                }

                if(raw_data.optInt("user_join") == 1){
                    event_register.setText("JOINED");
                    form_data.put("attendee_id", raw_data.optJSONObject("user_attend").optString("id"));
                } else {
                    event_register.setText("JOIN");
                }

                form_data.put("user_join", raw_data.optInt("user_join"));

                event_register.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!is_open_register){
                            display_status("Register period haven't start or expired");
                        } else if(event_register.getText().equals("JOIN"))
                            showMessage("Are you sure want to JOIN this event ?");
                        else {
                            view_ticket();
                        }
                            // showMessage("Are you sure want to WITHDRAW from this event ?");
                    }
                });
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void showMessage(String message){
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(event_register.getText().equals("JOIN")){
                    join_event();
                } else {
                    view_ticket();
                    // unjoin_event();
                }
                dialog.dismiss();
            }
        });
        alertDialogBuilder.show();
    }

    private void view_ticket() {
        Intent intent = new Intent(this, TicketActivity.class);
        intent.putExtra("event_id", form_data.optString("event_id"));
        intent.putExtra("event_title", form_data.optString("title"));
        intent.putExtra("event_time", events_time.getText().toString());
        intent.putExtra("event_locations", events_location.getText().toString());
        intent.putExtra("attendee_id", form_data.optString("attendee_id"));
        startActivity(intent);
    }

    private void join_event(){
        Intent intent = new Intent(this, RegisterEventActivity.class);
        intent.putExtra("event_id", form_data.optString("event_id"));
        intent.putExtra("event_title", form_data.optString("title"));
        intent.putExtra("event_time", events_time.getText().toString());
        intent.putExtra("event_locations", events_location.getText().toString());
        startActivity(intent);
    }

    private void unjoin_event(){
        try {
            form_data.put("type", "join_event");
            new CommonApiAsync(this, form_data).execute(Config.UNREGISTER_EVENT);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void delete_event(){
        try {
            form_data.put("type", "delete_event");
            new CommonApiAsync(this, form_data).execute(Config.DELETE_EVENT);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void is_deleted(String result){

        showMessage(result);
        if(result.equals("deleted")){
            db.removeEvent(form_data.optString("event_id"));
            Intent intent = new Intent(DetailEventsActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        } else {
            display_status("Error while deleting this events");
        }
    }

    public void is_join(String result){
        Log.d("Result", " -- " + result);

        if(result == null){
            display_status("Error had occur. Please try again later");
            return;
        }

        if(result.equals("registered")){
            display_status("You already in the list");
            event_register.setText("JOINED");
        }

        if(result.equals("success")){
            display_status("Successfully join to this event");
            event_register.setText("JOINED");
        }

        if(result.equals("withdraw")){
            display_status("You have withdraw yourself from this events");
            event_register.setText("JOIN");
            get_event_details();
        }
    }

    private void show_comment_popout(){
        // Initialize a new instance of LayoutInflater service
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        // Inflate the custom layout/view
        View customView = inflater.inflate(R.layout.popout_comment,null);

        // Initialize a new instance of popup window
        mPopupWindow = new PopupWindow(
                customView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );

        // Set an elevation value for popup window
        // Call requires API level 21
        if(Build.VERSION.SDK_INT>=21){
            mPopupWindow.setElevation(5.0f);
        }

        // Get a reference for the custom view close button
        ImageView closeButton = customView.findViewById(R.id.ib_close);

        // Comment send button
        final Button send_comment = customView.findViewById(R.id.btn_send);
        comment_content = customView.findViewById(R.id.message);
        RecyclerView commentlist = customView.findViewById(R.id.comment_list);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        commentlist.setLayoutManager(layoutManager);
        commentsAdapter = new CommentsAdapter(this, commentsModels);
        commentlist.setAdapter(commentsAdapter);
        parse_initial_comment(comment_section);

        // set click listener for comment
        send_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String comments = comment_content.getText().toString();
                if(comments.trim().isEmpty())
                    Toast.makeText(DetailEventsActivity.this, "Comment cannot be empty", Toast.LENGTH_SHORT).show();
                else
                    send_comment(comments);
            }
        });

        // Set a click listener for the popup window close button
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Dismiss the popup window
                mPopupWindow.dismiss();
                count_comment();
            }
        });

        mPopupWindow.setFocusable(true);
        mPopupWindow.update();
        mPopupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        mPopupWindow.showAtLocation(coordinatorLayout, Gravity.CENTER,0,0);
    }

    private void count_comment(){
        String comment = commentsModels.size() > 0 ? String.format("Comments (%S)", commentsModels.size()) : "No Comment yet.";
        event_comments.setText(comment);
    }

    private void send_comment(String content){
        Log.d("comments here", content + " mesg ");
        NewCommentAsync newCommentAsync = new NewCommentAsync(this, content, rating, form_data.optString("event_id"), "new_comment");
        newCommentAsync.execute(Config.ADD_NEW_COMMENT);
    }

    private void parse_initial_comment(JSONArray comment){

        if(commentsModels.size()>0)
            commentsModels.clear();

        if(comment.length() > 0){
            int length = comment.length();
            for (int i = 0 ; i < length ; i ++){

                try {
                    JSONObject item = comment.getJSONObject(i);
                    CommentsModel commentsModel = new CommentsModel(item);
                    Log.d("comment here ", " : " + item);
                    commentsModels.add(commentsModel);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            commentsAdapter.notifyDataSetChanged();
        }
    }

    public void parse_comment_response(String s){
        Log.d("result from server", ": " + s);
        try {
            JSONObject result = new JSONObject(s);
            JSONArray comments = result.getJSONArray("event_comments");
            JSONObject comment = comments.getJSONObject(0);
            CommentsModel commentsModel = new CommentsModel(comment,session.getKeyValue(Session.USERNAME), session.getKeyValue(Session.USER_ID));
            comment_section.put(comment);
            Log.d("result from server", ": " + comment_section.toString());
            commentsModels.add(commentsModel);
            commentsAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        comment_content.setText("");
    }

    public void like_bookmarked_event(String result, String type){

        Log.d("like_bookmark", result + " --- here");
        switch (type){
            case "like":
                if(!result.equals("fail")){

                    try {
                        JSONObject response = new JSONObject(result);

                        if(response.optString("status").equals("1"))
                            like.setImageResource(R.drawable.ic_favorite_black_24dp);
                        else
                            like.setImageResource(R.drawable.ic_favorite_border_black_24dp);

                        db.update_response(form_data.optString("event_id"),response.optInt("status"), response.optInt("total_like"));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                break;

            case "bookmark":
                if(!result.equals("fail")){
                    if(result.equals("1"))
                        bookmark.setImageResource(R.drawable.ic_bookmark_black_24dp);
                    else
                        bookmark.setImageResource(R.drawable.ic_bookmark_border_black_24dp);
                }
                break;
        }

        if(result.equals("fail"))
            Toast.makeText(this, "Error, please try again later", Toast.LENGTH_SHORT).show();
    }


    private void setup_header_image(JSONArray media){

        HashMap<String,String> url_maps = new HashMap<String, String>();
        String event_id = form_data.optString("event_id");

        //add banner using image url
        for(int i = 0; i < media.length() ; i++){
            JSONObject items = media.optJSONObject(i);
            url_maps.put("image_" + i, Config.IMAGE_CATALOG + items.optString("link"));
        }

        try {
            form_data.put("image", url_maps.get("image_0"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for(String name : url_maps.keySet()){
            Log.d("what is this ? ", "item name: " + url_maps.get(name));
            TextSliderView textSliderView = new TextSliderView(this);
//            // initialize a SliderLayout
            textSliderView
//                    .description(name)
                    .image(url_maps.get(name))
                    .setScaleType(BaseSliderView.ScaleType.CenterCrop)
                    .setOnSliderClickListener(this);
//
//            //add your extra information
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle()
                    .putString("image",url_maps.get(name));
//
            bannerSlider.addSlider(textSliderView);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.details_top_menu, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == android.R.id.home) {
            onBackPressed();
            return true;
        } else if(itemId == R.id.action_more){
            eventDetailsBottomSheet = new EventDetailsBottomSheet();

            if(session.getKeyValue(Session.ROLE_CODE).equals("5"))
                eventDetailsBottomSheet.setData(form_data.optString("event_id"), user_post_data.optString("owner_id"), form_data.optString("user_join").equals("1"));
            else
                eventDetailsBottomSheet.setData(form_data.optString("event_id"), user_post_data.optString("owner_id"));

            eventDetailsBottomSheet.show(getSupportFragmentManager(), "Dialog");
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
        if(slider.getBundle() != null){
            Intent intent = new Intent(this, ImageFullScreenActivity.class);
            intent.putExtra("image", slider.getBundle().getString("image"));
            startActivity(intent);
        }
    }

    @Override
    public void onAgencyClick() {
        Intent intent = new Intent(DetailEventsActivity.this, TakeAttendanceActivity.class);
        intent.putExtra("event_id", form_data.optString("event_id"));
        intent.putExtra("event_title", form_data.optString("title"));
        startActivity(intent);
    }

    @Override
    public void assignOfficer() {
        Intent intent = new Intent(DetailEventsActivity.this, AssignOfficerActivity.class);
        Log.d("all_result", form_data.toString());
        intent.putExtra("event_id", form_data.optString("event_id"));
        intent.putExtra("event_title", form_data.optString("title"));
        intent.putExtra("event_image", form_data.optString("image"));
        startActivity(intent);
    }

    @Override
    public void unJoinEvent() {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Are you sure you want to un-join this event ?");
        alertDialogBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                eventDetailsBottomSheet.dismiss();
                unjoin_event();
                dialog.dismiss();
            }
        });
        alertDialogBuilder.show();
    }

    @Override
    public void feedbackOnclick() {
        eventDetailsBottomSheet.dismiss();
        // show_feedback();
        show_feedback_form();
    }

    @Override
    public void addCalender(){
        onAddEventClicked();
    }

    @Override
    public void contact_officer() {
        eventDetailsBottomSheet.dismiss();
        show_contact_list();
    }

    @Override
    public void add_vip() {
        Intent intent = new Intent(DetailEventsActivity.this, EventVipActivity.class);
        intent.putExtra("event_id", form_data.optString("event_id"));
        intent.putExtra("event_title", form_data.optString("title"));
        startActivity(intent);
    }

    @Override
    public void view_feedback() {
        Intent intent = new Intent(this, FeedbackManagerActivity.class);
        intent.putExtra("event_id", form_data.optString("event_id"));
        startActivity(intent);
    }

    @Override
    public void delete_post() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Are you sure want to delete this event ? Once it's deleted it cannot be undone!");
        alertDialogBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                delete_event();
                dialog.dismiss();
            }
        });
        alertDialogBuilder.show();
    }

    @Override
    public void editPost() {
        Intent intent = new Intent(this, EditEventActivity.class);
        intent.putExtra("event_id", form_data.optString("event_id"));
        startActivity(intent);
    }

    private void show_feedback(){
        final RatingDialog ratingDialog = new RatingDialog.Builder(this)
                .threshold(6)
                .playstoreUrl("")
                .onRatingChanged(new RatingDialog.Builder.RatingDialogListener() {
                    @Override
                    public void onRatingSelected(float ratings, boolean thresholdCleared) {
                        Log.d("rating: ", "rating : " + rating);
                        rating = ratings;

                        if(thresholdCleared){
                            send_feedback("Good");
                        }
                    }
                })
                .onRatingBarFormSumbit(new RatingDialog.Builder.RatingDialogFormListener() {
                    @Override
                    public void onFormSubmitted(String feedback) {
                        Log.d("feedback", " display here : " + feedback);
                        send_feedback(feedback);
                    }
                }).build();

        ratingDialog.show();
    }

    private void show_feedback_form() {
        Intent intent = new Intent(this, FeedbackFormActivity.class);
        startActivity(intent);
    }

    private void send_feedback(String content){
        Log.d("comments here", content + " mesg ");
        NewCommentAsync newCommentAsync = new NewCommentAsync(this, content, rating, form_data.optString("event_id"), "new_feedback");
        newCommentAsync.execute(Config.ADD_NEW_FEEDBACK);
    }

    public void parse_feedback(String result){
        if(result.equals("success")){
            display_status("Successfully submitted your events");
        }
    }

    private void show_contact_list(){
        // Initialize a new instance of LayoutInflater service
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        // Inflate the custom layout/view
        View customView = inflater.inflate(R.layout.popout_contact_list,null);

        // Initialize a new instance of popup window
        feedback_Window = new PopupWindow(
                customView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );

        // Set an elevation value for popup window
        // Call requires API level 21
        if(Build.VERSION.SDK_INT>=21){
            feedback_Window.setElevation(5.0f);
        }

        // Get a reference for the custom view close button
        ImageView closeButton = customView.findViewById(R.id.ib_close);

        // Comment send button
        final Button send_comment = customView.findViewById(R.id.btn_send);
        RecyclerView contact_list = customView.findViewById(R.id.contact_list);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        contact_list.setLayoutManager(layoutManager);
        ContactListAdapter contactListAdapter = new ContactListAdapter(this, contactListModels);
        contact_list.setAdapter(contactListAdapter);

        // set click listener for comment
        send_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(DetailEventsActivity.this, "Feedback cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        // Set a click listener for the popup window close button
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Dismiss the popup window
                feedback_Window.dismiss();
            }
        });

        feedback_Window.setFocusable(true);
        feedback_Window.update();
        feedback_Window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        feedback_Window.showAtLocation(coordinatorLayout, Gravity.CENTER,0,0);
    }

    public void onAddEventClicked() {
        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setType("vnd.android.cursor.item/event");

        Calendar cal = Calendar.getInstance();

        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, Helper.get_time_milli(form_data.optString("beginTime")));
        intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, Helper.get_time_milli(form_data.optString("endTime")));
        intent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, false);

        intent.putExtra(CalendarContract.Events.TITLE, form_data.optString("title"));
        intent.putExtra(CalendarContract.Events.DESCRIPTION, form_data.optString("desc"));
        intent.putExtra(CalendarContract.Events.EVENT_LOCATION, form_data.optString("location"));

        startActivity(intent);
    }


    private void display_status(String message){
        Snackbar snackbar = Snackbar
                .make(findViewById(R.id.details_layout), Html.fromHtml("<font color=\"#ffffff\">"+ message +"</font>"), Snackbar.LENGTH_LONG);
        View snackBarView = snackbar.getView();
        if(Build.VERSION.SDK_INT >= 23 )
            snackBarView.setBackgroundColor(getResources().getColor(R.color.mt_red, null));
        else
            snackBarView.setBackgroundColor(getResources().getColor(R.color.mt_red));
        snackbar.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(AppStatus.getInstance(getApplication()).isOnline()){
            get_event_details();
        }
    }

    @Override
    public void OnContactSelected(String selected_user_id, String username) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("name", username);
        intent.putExtra("target", selected_user_id);
        startActivity(intent);
    }
}