package com.kuchingitsolution.asus.eventmanagement.search;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kuchingitsolution.asus.eventmanagement.R;
import com.kuchingitsolution.asus.eventmanagement.config.CommonApiAsync;
import com.kuchingitsolution.asus.eventmanagement.config.Config;
import com.kuchingitsolution.asus.eventmanagement.config.Session;
import com.kuchingitsolution.asus.eventmanagement.feed.CategoryAdapter;
import com.kuchingitsolution.asus.eventmanagement.feed.CategoryModel;
import com.kuchingitsolution.asus.eventmanagement.my_event.MyEventModel;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    private SearchView searchView = null;
    private Toolbar toolbar;
    private ImageView no_result;
    private AVLoadingIndicatorView avi;
    private TextView result_label, category_label;
    private JSONObject form_data = new JSONObject();
    private Session session;

    private RecyclerView search_list;
    private SearchEventAdapter searchEventAdapter;
    private LinearLayoutManager linearLayoutManager;
    private ArrayList<MyEventModel> myEventModels = new ArrayList<>();

    private GridView category_grid;
    private CategoryAdapter categoryAdapter;
    private ArrayList<CategoryModel> categoryModels = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

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

        search_list = findViewById(R.id.event_search_result);
        linearLayoutManager = new LinearLayoutManager(this);
        searchEventAdapter = new SearchEventAdapter(this, myEventModels);
        search_list.setLayoutManager(linearLayoutManager);
        search_list.setAdapter(searchEventAdapter);

        category_grid = findViewById(R.id.grid_view);
        category_label = findViewById(R.id.category_label);
        avi = findViewById(R.id.avi);
        no_result = findViewById(R.id.no_result);

        categoryAdapter = new CategoryAdapter(this, categoryModels);
        category_grid.setAdapter(categoryAdapter);

        no_result.setVisibility(View.GONE);
        avi.hide();

        JSONObject form_data = new JSONObject();
        try {
            form_data.put("type", "category_list");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        category_grid.setVisibility(View.GONE);
        category_label.setVisibility(View.GONE);
        // hide_grid_view(false);
        //get_all_category(form_data);

    }

    private void get_all_category(JSONObject form_data){

        CommonApiAsync commonApiAsync = new CommonApiAsync(this, form_data);
        commonApiAsync.execute(Config.GET_CATEGORY);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // adds item to action bar
        getMenuInflater().inflate(R.menu.search_menu, menu);

        // Get Search item from action bar and Get Search service
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager) SearchActivity.this.getSystemService(Context.SEARCH_SERVICE);
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            assert searchManager != null;
            searchView.setSearchableInfo(searchManager.getSearchableInfo(SearchActivity.this.getComponentName()));
            searchView.setIconified(false);
            searchView.setMaxWidth(Integer.MAX_VALUE);
            searchView.clearFocus();

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    if(query.trim().isEmpty()) {
                        display_status("Please key in event name");
                    } else {

                        try {

                            if(form_data.length() > 0)
                                form_data = new JSONObject();

                            form_data.put("query", query);
                            form_data.put("type", "search");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        start_searching();
                        avi.show();
                    }
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    no_result.setVisibility(View.GONE);
                    return false;
                }

            });

            searchView.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    onBackPressed();
                    return true;
                }
            });

        }

        return true;
    }

    private void start_searching(){
        new CommonApiAsync(this, form_data).execute(Config.SEARCH_ALL_EVENT);
    }

    public void parse_search_result(String result, String type){

        Log.d("RESULT HERE", " SEARCHING == " + result);
//        showMessage(result);

        switch (type){
            case "search":
                parse_search_result(result);
                break;
            case "category_list":
                parse_category_result(result);
                break;
                default:
                    break;
        }

    }

    private void parse_category_result(String result){
//            showMessage(result);
//            Log.d("test----", "---- " + r);

        try {
            JSONObject data = new JSONObject(result);
            JSONArray info = data.optJSONArray("categories");
            int length = info.length();

            for (int i = 0; i < length; i++) {
                JSONObject category = info.optJSONObject(i);
                CategoryModel categoryModel = new CategoryModel(category.optInt("id"), category.optString("category_name"),category.optInt("live_event"));
                categoryModels.add(categoryModel);
            }

            categoryAdapter.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        avi.hide();
    }

    private void parse_search_result(String result){

        if(result == null){
            avi.hide();
            Toast.makeText(this, "Error while searching event. Please make sure your internet connection is good", Toast.LENGTH_SHORT).show();
            no_result.setVisibility(View.VISIBLE);
            return;
        }

        if(myEventModels.size() > 0){
            myEventModels.clear();
        }
        // hide_grid_view(true);

        try {
            JSONObject data = new JSONObject(result);
            JSONObject raw = data.getJSONObject("events");
            JSONArray completed_event = raw.optJSONArray("data");
            int completed_events_size = completed_event.length();
            if(completed_events_size > 0){

                for (int i = 0 ; i < completed_events_size ; i ++ ){
                    JSONObject each_event = completed_event.getJSONObject(i);
                    MyEventModel myEventModel = new MyEventModel(each_event);
                    myEventModels.add(myEventModel);
                }

                searchEventAdapter.notifyDataSetChanged();
            } else {
                no_result.setVisibility(View.VISIBLE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        avi.hide();

    }

    private void hide_grid_view(boolean hide){
        if(hide){
            category_grid.setVisibility(View.GONE);
            category_label.setVisibility(View.GONE);
            search_list.setVisibility(View.VISIBLE);
        } else {
            category_grid.setVisibility(View.VISIBLE);
            category_label.setVisibility(View.VISIBLE);
            search_list.setVisibility(View.GONE);
        }
    }

    private void showMessage(String message){
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SearchActivity.this);
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
