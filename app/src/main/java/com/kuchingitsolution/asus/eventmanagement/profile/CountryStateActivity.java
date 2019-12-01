package com.kuchingitsolution.asus.eventmanagement.profile;

import android.content.DialogInterface;
import android.content.Intent;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.kuchingitsolution.asus.eventmanagement.R;
import com.kuchingitsolution.asus.eventmanagement.config.CommonApiAsync;
import com.kuchingitsolution.asus.eventmanagement.config.Config;
import com.kuchingitsolution.asus.eventmanagement.config.DB;
import com.kuchingitsolution.asus.eventmanagement.config.Session;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CountryStateActivity extends AppCompatActivity {

    private Session session;
    private DB db;
    private JSONObject form_data = new JSONObject();
    private RecyclerView listing;
    private String action_code = null;
    private ListingAdapter listingAdapter;
    private ImageView no_result;
    private ArrayList<country_state_model> country_state_models = new ArrayList<>();
    private AVLoadingIndicatorView avi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_state_activity);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        String title = getString(R.string.app_name);

        if(intent != null){
            title = intent.getStringExtra("action_type");
            action_code = intent.getStringExtra("type");
        }

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(title);
        }

        setup_element();
        setup_local();
    }

    private void setup_element(){

        listing = findViewById(R.id.listing);
        listingAdapter = new ListingAdapter(this, country_state_models);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        listing.setAdapter(listingAdapter);
        listing.setLayoutManager(linearLayoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(listing.getContext(),
                linearLayoutManager.getOrientation());
        listing.addItemDecoration(dividerItemDecoration);

        no_result = findViewById(R.id.no_result);
        no_result.setVisibility(View.GONE);
        avi = findViewById(R.id.avi);
        session = new Session(this);

    }

    private void setup_local(){

        compile_form_date("type", action_code);

        if(action_code.equals("get_states")){
            compile_form_date("country_id", String.valueOf(session.getIdValue(Session.COUNTRY_ID)));
        }
        CommonApiAsync commonApiAsync = new CommonApiAsync(this, form_data);
        commonApiAsync.execute(Config.GET_LOCAL);

    }

    private void compile_form_date(String key, String value){

        try {
            form_data.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void parse_response(String type, String result) {

        switch (type){
            case "get_countries":
                parse_country_list(result, "countries");
                break;
            case "get_states":
                parse_country_list(result, "states");
                break;
        }

    }

    public void send_response(String id, String name){

        Intent intent = new Intent(CountryStateActivity.this, AddressActivity.class);
        intent.putExtra("name", name);
        intent.putExtra("id", id);
        setResult(RESULT_OK, intent);
        finish();

    }

    private void parse_country_list(String result, String key){
//        showMessage(result);

        try {

            JSONObject data = new JSONObject(result);
            JSONArray countries = data.optJSONArray(key);
            int size = countries.length();

            for (int i = 0 ; i < size; i++){
                JSONObject country = countries.getJSONObject(i);
                country_state_model country_state_model = new country_state_model(country.optString("name"), country.optString("id"));
                country_state_models.add(country_state_model);
            }

            listingAdapter.notifyDataSetChanged();

        } catch (JSONException e) {
            no_result.setVisibility(View.VISIBLE);
            e.printStackTrace();
        }

        no_result.setVisibility(View.GONE);
        avi.hide();

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
