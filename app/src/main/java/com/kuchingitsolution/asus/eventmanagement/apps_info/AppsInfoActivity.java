package com.kuchingitsolution.asus.eventmanagement.apps_info;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Toast;

import com.kuchingitsolution.asus.eventmanagement.R;
import com.kuchingitsolution.asus.eventmanagement.config.CommonApiAsync;
import com.kuchingitsolution.asus.eventmanagement.config.Config;

import org.json.JSONException;
import org.json.JSONObject;

public class AppsInfoActivity extends AppCompatActivity {

    private WebView webView;
    private Toolbar toolbar;
    JSONObject form_data = new JSONObject();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apps_info);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.apps_info);
        }

        try {
            form_data.put("action", "get_apps_info");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        setup_element();
        get_apps_info();

    }

    private void get_apps_info(){
        new CommonApiAsync(this, form_data).execute(Config.GET_APPS_INFO);
    }

    public void parse_apps_info(String s){
        Log.d("result from server", " - result : " + s);

        if(s == null){
            Toast.makeText(this, "Error while bookmarked. Please make sure your internet connection is good", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            JSONObject data = new JSONObject(s);
            webView.getSettings().setLoadWithOverviewMode(true);
            webView.loadData(data.getString("info"),"text/html",null);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void setup_element(){

        webView = findViewById(R.id.myWebView);

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
