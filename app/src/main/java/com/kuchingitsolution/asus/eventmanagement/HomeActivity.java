package com.kuchingitsolution.asus.eventmanagement;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.kuchingitsolution.asus.eventmanagement.auth.LoginActivity;
import com.kuchingitsolution.asus.eventmanagement.auth.MoreInfoActivity;
import com.kuchingitsolution.asus.eventmanagement.config.BadgeDrawable;
import com.kuchingitsolution.asus.eventmanagement.config.CommonApiAsync;
import com.kuchingitsolution.asus.eventmanagement.config.Config;
import com.kuchingitsolution.asus.eventmanagement.config.Session;
import com.kuchingitsolution.asus.eventmanagement.feed.CategoryFragment;
import com.kuchingitsolution.asus.eventmanagement.feed.FeedFragment;
import com.kuchingitsolution.asus.eventmanagement.feed.InfoCategoryFragment;
import com.kuchingitsolution.asus.eventmanagement.feed.OptionEventFeedCallback;
import com.kuchingitsolution.asus.eventmanagement.marketplace.MarketplaceMainActivity;
import com.kuchingitsolution.asus.eventmanagement.message.MessageActivity;
import com.kuchingitsolution.asus.eventmanagement.notification.NotificationFragment;
import com.kuchingitsolution.asus.eventmanagement.profile.MyProfileActivity;
import com.kuchingitsolution.asus.eventmanagement.profile.ProfileFragment;
import com.kuchingitsolution.asus.eventmanagement.search.SearchActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
//import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class HomeActivity extends AppCompatActivity {

    private AHBottomNavigation ahBottomNavigation;
    private Toolbar toolbar;
    private HomeSwipePager viewPager;
    private HomeBottomBarAdapter pagerAdapter;
    private Session session;
    private JSONObject form_data = new JSONObject();
    private String user_id, TAG = "HOME_ACTIVITY";
    LayerDrawable badge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        session = new Session(this);

        if(getSupportActionBar() != null){
            if(session.getIdValue("category_id") != 0) {
                String title = String.format("%s - (%s)", getResources().getString(R.string.app_name), session.getKeyValue("category_name"));
                getSupportActionBar().setTitle(title);
            } else {
                getSupportActionBar().setTitle(R.string.app_name);
            }
        }


        check_auth();
        setupViewPager();
        check_dynamic_link();
        send_user_tag();

        ahBottomNavigation = findViewById(R.id.navigation);
        addBottomNavigationItems();

        // setup bottom navigation stlye
        setup_bottom_navi_style();

        // select initial tab
        ahBottomNavigation.setCurrentItem(0);
        ahBottomNavigation.setOnTabSelectedListener(mOnNavigationItemSelectedListener);

        Log.d("user--", " data here : " + session.getKeyValue(Session.ROLE_CODE));
    }

    private void send_user_tag(){
        OneSignal.sendTag("user_id", session.getKeyValue(Session.USER_ID));
    }

    private void setup_country_state(){

        CommonApiAsync commonApiAsync = new CommonApiAsync(this, form_data);
        commonApiAsync.execute(Config.GET_LOCAL);

    }

    private void check_dynamic_link(){
//        FirebaseDynamicLinks.getInstance()
//                .getDynamicLink(getIntent())
//                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
//                    @Override
//                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
//                        // Get deep link from result (may be null if no link is found)
//                        Uri deepLink = null;
//                        if (pendingDynamicLinkData != null) {
//                            deepLink = pendingDynamicLinkData.getLink();
//                            Log.d(TAG, "getDynamicLink:onFailures" + deepLink.toString());
//                        }
//
//                    }
//                })
//                .addOnFailureListener(this, new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.w(TAG, "getDynamicLink:onFailure", e);
//                    }
//                });
    }

    private void check_auth(){
        if(!session.getStatus(Session.LOGGED_IN)){
            redirect_login();
            return;
        }

        user_id = session.getKeyValue(Session.USER_ID);

        if(!session.getStatus(Session.COMPLETED_PROFILE)){
            Intent intent = new Intent(this, MoreInfoActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
//            finish();
        }

    }

    private void valdiate_live_account(){

        JSONObject auth_data = new JSONObject();

        try {
            auth_data.put("email", session.getKeyValue(Session.EMAIL));
            auth_data.put("type", "check_account");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        CommonApiAsync check = new CommonApiAsync(this, auth_data);
        check.execute(Config.CHECK_AUTH);

    }

    public void account_response(String result){

        JSONObject response = null;
//        showMessage(result);

        if(result == null){
            return;
        }

        try {

            response = new JSONObject(result);

            if(!response.optBoolean("status")){
                session.clearPreference();
                redirect_login();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void redirect_login(){
        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void setup_bottom_navi_style(){
//        ahBottomNavigation.setBehaviorTranslationEnabled(true);
        //ahBottomNavigation.setTranslucentNavigationEnabled(true);
        if(Build.VERSION.SDK_INT > 22)
            ahBottomNavigation.setDefaultBackgroundColor(getResources().getColor(R.color.colorWhite,null));
        else
            ahBottomNavigation.setDefaultBackgroundColor(getResources().getColor(R.color.colorWhite));
    }

    private AHBottomNavigation.OnTabSelectedListener mOnNavigationItemSelectedListener =
            new AHBottomNavigation.OnTabSelectedListener() {
                @Override
                public boolean onTabSelected(int position, boolean wasSelected) {

                    if (!wasSelected)
                        viewPager.setCurrentItem(position);

                    switch (position){
                        case 0:
                            if(getSupportActionBar() != null) {
                                getSupportActionBar().setTitle(R.string.title_home);
                            }
                            break;

                        case 1:
                            if(getSupportActionBar() != null) {
                                getSupportActionBar().setTitle(R.string.title_Message);
                            }
                            break;

                        case 2:
                            if(getSupportActionBar() != null) {
                                getSupportActionBar().setTitle(R.string.title_Profile);
                            }
                            break;
                    }

                    return true;
                }
            };

    private void setupViewPager() {
        viewPager = findViewById(R.id.viewpager);
        viewPager.setPagingEnabled(false);
        viewPager.setOffscreenPageLimit(3);
        pagerAdapter = new HomeBottomBarAdapter(getSupportFragmentManager());

        pagerAdapter.addFragments(new FeedFragment());
        pagerAdapter.addFragments(new NotificationFragment());
        pagerAdapter.addFragments(new ProfileFragment());

        viewPager.setAdapter(pagerAdapter);
    }

    private void addBottomNavigationItems() {

        AHBottomNavigationItem item1 = new AHBottomNavigationItem(R.string.title_feed, R.drawable.ic_home_black_24dp, R.color.colorPrimary);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem(R.string.title_notification, R.drawable.ic_notifications_black_24dp, R.color.colorPrimary);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem(R.string.title_Profile, R.drawable.baseline_account_circle_black_24, R.color.colorPrimary);
        ahBottomNavigation.setDefaultBackgroundColor(Color.parseColor("#FEFEFE"));
        ahBottomNavigation.setAccentColor(Color.parseColor("#3F51B5"));

        ahBottomNavigation.addItem(item1);
        ahBottomNavigation.addItem(item2);
        ahBottomNavigation.addItem(item3);

    }

    public void OnEventLike(String event_id, int position){
        try {
            form_data.put("event_id", event_id);
            form_data.put("user_id", user_id);
            form_data.put("position", position);
            form_data.put("type", "like");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new CommonApiAsync(this, form_data).execute(Config.LIKE_EVENT);
    }

    public void OnEventBookmark(String event_id, int position){

        try {
            form_data.put("event_id", event_id);
            form_data.put("user_id", user_id);
            form_data.put("position", position);
            form_data.put("type", "bookmark");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new CommonApiAsync(this, form_data).execute(Config.BOOKMARK_EVENT);
    }

    public void parse_response(String s, String type){

        FeedFragment feedFragment = (FeedFragment) pagerAdapter.getItem(0);

        if(type.equals("like")){
            feedFragment.add_like(s, form_data.optInt("position"), form_data.optString("event_id"));
        } else if(type.equals("bookmark")){
            feedFragment.add_bookmark(s, form_data.optInt("position"));
        }

        form_data = new JSONObject();

    }

    public void parse_category(String s){

        Fragment fragment = pagerAdapter.getItem(0);

        if(fragment instanceof CategoryFragment){
            CategoryFragment categoryFragment = (CategoryFragment) pagerAdapter.getItem(0);
            categoryFragment.parse_data(s);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.top_menu, menu);

        MenuItem itemCart = menu.findItem(R.id.message);

        if(session.getKeyValue(Session.ROLE_CODE).equals("6") || session.getKeyValue(Session.ROLE_CODE).equals("4") || session.getKeyValue(Session.ROLE_CODE).equals("2")){
            badge = (LayerDrawable) itemCart.getIcon();
            setBadgeCount(this, badge, String.valueOf(session.getMessageBadge()));
        } else {
            itemCart.setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        super.onOptionsItemSelected(menuItem);
        int itemId = menuItem.getItemId();
        Intent intent;

        switch (itemId){
            case android.R.id.home:
                super.onBackPressed();
                break;
            case R.id.message:
                session.ResetMessageBadge();
                setBadgeCount(this,badge, String.valueOf(session.getMessageBadge()));
                intent = new Intent(HomeActivity.this, MessageActivity.class);
                startActivity(intent);
                break;
            case R.id.search:
                intent = new Intent(HomeActivity.this, SearchActivity.class);
                startActivity(intent);
                break;
            case R.id.marketplace:
                intent = new Intent(HomeActivity.this, MarketplaceMainActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }

    public static void setBadgeCount(Context context, LayerDrawable icon, String count) {

        BadgeDrawable badge;

        // Reuse drawable if possible
        Drawable reuse = icon.findDrawableByLayerId(R.id.ic_badge);
        if (reuse != null && reuse instanceof BadgeDrawable) {
            badge = (BadgeDrawable) reuse;
        } else {
            badge = new BadgeDrawable(context);
        }

        badge.setCount(count);
        icon.mutate();
        icon.setDrawableByLayerId(R.id.ic_badge, badge);
    }

    public void parse_officer_notification(String s){
        Log.d("result from server", "Result here: " + s);
        showMessage(s);

        if(s.equals("success")){
            session.setBooleanKey(Session.LOGGED_IN, false);
            session.clearPreference();
            redirect_login();
        }
    }

    public void parse_agency_notification(String s){
        Log.d("result from server", "Result here: " + s);

        if(s.equals("success")){
            session.setBooleanKey(Session.LOGGED_IN, false);
            redirect_login();
        }

    }

    public void parse_vip_notification(String s){
        Log.d("result from server", "Result here: " + s);
        if(s.equals("success")){
            Toast.makeText(this, "Successfully accept the invitation", Toast.LENGTH_SHORT).show();
        }
    }

    public void parse_responses(String s, String type){

        switch (type){
            case "invite_existing_officer":
                parse_officer_notification(s);
                break;
            case "assign_agency_user":
                parse_agency_notification(s);
                break;
            case "invite_existing_vip":
                parse_vip_notification(s);
                break;
            case "check_account":
                account_response(s);
                break;
            case "category_list":
                parse_category(s);
                break;
            case "get_info_category":
                parse_info_category(s);
                break;
                default:
                    parse_response(s, type);
        }
    }

    private void showMessage(String message){
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialogBuilder.show();
    }

    private void confirmExitApps(){
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Do you want to exit ?");
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
                System.exit(0);
                dialog.dismiss();
            }
        });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialogBuilder.show();
    }

    private void parse_info_category(String result){
        Fragment fragment = pagerAdapter.getItem(0);
        Log.d("testtt", result + " -- " );
        if(fragment instanceof FeedFragment){
            Log.d("testtt - ", result + " -- ");
            FeedFragment feedFragment = (FeedFragment) pagerAdapter.getItem(0);
            feedFragment.send_category_info(result, "parse_category_info");
        }
    }

    private BroadcastReceiver listener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent ) {
            String type = intent.getStringExtra("type");
            Log.d( "Received data : ", "data here : " + type);
            //showMessage(data);

            if(type.equals("message"))
                setBadgeCount(HomeActivity.this, badge, String.valueOf(session.getMessageBadge()));

        }
    };

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        confirmExitApps();
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(listener,
                new IntentFilter(Config.BROADCAST_MSG));
        valdiate_live_account();
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(listener);
        super.onPause();
    }

}
