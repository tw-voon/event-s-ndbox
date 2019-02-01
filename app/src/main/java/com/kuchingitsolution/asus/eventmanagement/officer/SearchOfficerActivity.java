package com.kuchingitsolution.asus.eventmanagement.officer;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
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
import android.widget.TextView;

import com.kuchingitsolution.asus.eventmanagement.R;
import com.kuchingitsolution.asus.eventmanagement.config.Config;
import com.kuchingitsolution.asus.eventmanagement.config.Session;
import com.wang.avi.AVLoadingIndicatorView;
import com.yarolegovich.lovelydialog.LovelyProgressDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class SearchOfficerActivity extends AppCompatActivity {

    private SearchView searchView = null;
    private Toolbar toolbar;
    private ImageView no_result;
    private AVLoadingIndicatorView avi;
    private CardView result_card;
    private TextView username, email, result_label;
    private Button add_user;
    private SearchOfficerAsync searchOfficerAsync;
    private JSONObject form_data = new JSONObject();
    private Session session;
    private LovelyProgressDialog lovelyProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_officer);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null ){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        setup_element();

    }

    private void setup_element(){

        no_result = findViewById(R.id.no_result);
        avi = findViewById(R.id.avi);
        result_label = findViewById(R.id.result_label);
        session = new Session(this);

        no_result.setVisibility(View.GONE);
        avi.hide();
        result_label.setVisibility(View.GONE);

        result_card = findViewById(R.id.result_card);
        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        add_user = findViewById(R.id.add_user);

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
        SearchManager searchManager = (SearchManager) SearchOfficerActivity.this.getSystemService(Context.SEARCH_SERVICE);
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            assert searchManager != null;
            searchView.setSearchableInfo(searchManager.getSearchableInfo(SearchOfficerActivity.this.getComponentName()));
            searchView.setIconified(false);
            searchView.setMaxWidth(Integer.MAX_VALUE);

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    if(!Patterns.EMAIL_ADDRESS.matcher(query).matches()) {
                        display_status("Please input proper email format ");
                    } else {

                        try {

                            if(form_data.length() > 0)
                                form_data = new JSONObject();

                            form_data.put("email", query);
                            form_data.put("type", "search");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        avi.show();
                        searchOfficerAsync = new SearchOfficerAsync(SearchOfficerActivity.this, form_data);
                        searchOfficerAsync.execute(Config.GET_OFFICER);
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

            searchView.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    onBackPressed();
                    return false;
                }
            });
        }

        return true;
    }

    public void parse_result(String s){
        Log.d("result here", " : " + s);
        try {

            JSONArray result = new JSONArray(s);
            final JSONObject data = result.getJSONObject(0);

            if(result.length() == 0){
                no_result.setVisibility(View.VISIBLE);
                avi.hide();
                return;
            }

            result_label.setVisibility(View.VISIBLE);
            username.setText(data.optString("name"));
            email.setText(data.optString("email"));
            result_card.setVisibility(View.VISIBLE);

            add_user.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    try {
                        form_data.put("type", "select");
                        form_data.put("selected_user_id", data.optString("id"));
                        form_data.put("user_id", session.getKeyValue(Session.USER_ID));
                        form_data.put("roles_id", "2");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    searching();
                    searchOfficerAsync = new SearchOfficerAsync(SearchOfficerActivity.this, form_data);
                    searchOfficerAsync.execute(Config.SELECT_USER);
                }
            });

        } catch (JSONException e) {
            no_result.setVisibility(View.VISIBLE);
            e.printStackTrace();
        }

        avi.hide();
    }

    private void searching(){
        lovelyProgressDialog = new LovelyProgressDialog(this);
        lovelyProgressDialog.setIcon(R.drawable.ic_check_white_18dp)
                .setTitle("Adding this user...")
                .setTopColorRes(R.color.mt_red)
                .show();
    }

    public void parse_selected_user(String s){

        Log.d("result here", " : " + s);

        Handler handler = new Handler();
        handler.postDelayed(r, 1000);

        switch (s) {
            case "top_user":
                display_status("You cannot add this user");
                break;
            case "vip":
                display_status("This user was a VIP for an Event");
                break;
            case "officer":
                display_status("This user already is an Officer");
                break;
            case "success":
                set_result();
                break;
        }

    }

    final Runnable r = new Runnable() {
        @Override
        public void run() {
            lovelyProgressDialog.dismiss();
        }
    };

    private void set_result(){
        Intent intent = new Intent(SearchOfficerActivity.this, MyOfficerActivity.class);
        intent.putExtra("refresh", true);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void showMessage(String message){
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SearchOfficerActivity.this);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialogBuilder.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        switch (itemId){
            case android.R.id.home:
                super.onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    private void display_status(String message){
        Snackbar snackbar = Snackbar
                .make(findViewById(R.id.new_officer), Html.fromHtml("<font color=\"#ffffff\">"+ message +"</font>"), Snackbar.LENGTH_LONG);
        View snackBarView = snackbar.getView();
        if(Build.VERSION.SDK_INT >= 23 )
            snackBarView.setBackgroundColor(getResources().getColor(R.color.mt_red, null));
        else
            snackBarView.setBackgroundColor(getResources().getColor(R.color.mt_red));
        snackbar.show();
    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//        searchOfficerAsync.cancel(true);
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        searchOfficerAsync.cancel(true);
//    }
}
