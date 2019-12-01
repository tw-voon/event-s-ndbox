package com.kuchingitsolution.asus.eventmanagement.profile;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.kuchingitsolution.asus.eventmanagement.R;
import com.kuchingitsolution.asus.eventmanagement.config.CommonApiAsync;
import com.kuchingitsolution.asus.eventmanagement.config.Config;
import com.kuchingitsolution.asus.eventmanagement.config.DB;
import com.kuchingitsolution.asus.eventmanagement.config.Session;

import org.json.JSONException;
import org.json.JSONObject;

public class AddressActivity extends AppCompatActivity {

    private Session session;
    private DB db;
    private TextView country, state;
    private EditText addr_line_1, addr_line_2, city, postcode;
    private MenuItem menuitem;
    private JSONObject data = new JSONObject();
    private static int SELECT_STATE_CODE = 1001, SELECT_COUNTRY_CODE = 1002;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        String title = getString(R.string.app_name);

        if(intent != null){
            title = intent.getStringExtra("action_type");
        }

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(title);
        }

        setup_element();
    }

    private void setup_element(){

        session = new Session(this);
        db = new DB(this);

        country = findViewById(R.id.form_country);
        state = findViewById(R.id.form_state);
        addr_line_1 = findViewById(R.id.form_address_line_1);
        addr_line_2 = findViewById(R.id.form_address_line_2);
        city = findViewById(R.id.form_city);
        postcode = findViewById(R.id.form_postcode);

        setup_listener();
        auto_fill();

    }

    private void setup_listener(){

        country.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddressActivity.this, CountryStateActivity.class);
                intent.putExtra("type", "get_countries");
                intent.putExtra("action_type", "Please select your country");
                startActivityForResult(intent, SELECT_COUNTRY_CODE);
            }
        });

        state.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddressActivity.this, CountryStateActivity.class);
                intent.putExtra("type", "get_states");
                intent.putExtra("action_type", "Please select your state");
                startActivityForResult(intent, SELECT_STATE_CODE);
            }
        });

    }

    private void auto_fill(){

        country.setText(session.getKeyValue(Session.COUNTRY));
        state.setText(session.getKeyValue(Session.STATE));
        addr_line_1.setText(session.getKeyValue(Session.ADDR_LINE_1));
        addr_line_2.setText(session.getKeyValue(Session.ADDR_LINE_2));
        postcode.setText(session.getKeyValue(Session.POSTCODE));
        city.setText(session.getKeyValue(Session.CITY));

        compile_form_date("user_id", session.getKeyValue(Session.USER_ID));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_COUNTRY_CODE && resultCode == RESULT_OK && data != null) {
            country.setText(data.getStringExtra("name"));
            session.setKeyValue(Session.COUNTRY, data.getStringExtra("name"));
            session.setIdValue(Session.COUNTRY_ID, Integer.parseInt(data.getStringExtra("id")));
            compile_form_date("country_id", data.getStringExtra("id"));
            //Toast.makeText(AddressActivity.this, "Selected id: " + data.getStringExtra("name") + " -- " + SELECT_COUNTRY_CODE, Toast.LENGTH_SHORT).show();
        }

        if (requestCode == SELECT_STATE_CODE && resultCode == RESULT_OK && data != null) {
            state.setText(data.getStringExtra("name"));
            session.setKeyValue(Session.STATE, data.getStringExtra("name"));
            session.setIdValue(Session.STATE_ID, Integer.parseInt(data.getStringExtra("id")));
            compile_form_date("state_id", data.getStringExtra("id"));

            //Toast.makeText(AddressActivity.this, "Selected id: " + data.getStringExtra("name") + " -- " + SELECT_STATE_CODE, Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.submit, menu);
        menuitem = menu.findItem(R.id.submit);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            // action with ID action_settings was selected
            case R.id.submit:
                hideSoftKeyboard();
                menuitem.setEnabled(false);
                if(gathering_info()) {
                    Log.d("gathering_info", getData().toString());
                    CommonApiAsync commonApiAsync = new CommonApiAsync(this, getData());
                    commonApiAsync.execute(Config.UPDATE_ADDRESS);
                } else {
                    Log.d("gathering_info", "error");
                    menuitem.setEnabled(true);
                }
                break;
            case android.R.id.home:
                onBackPressed();
                break;

        }
        return true;

    }

    private boolean gathering_info(){

        if(addr_line_1.getText().toString().trim().isEmpty()){
            addr_line_1.setError("Address line 1 is required");
            return false;
        } else {
            addr_line_1.setError(null);
            compile_form_date("address_line_1", addr_line_1.getText().toString());
        }

        if(addr_line_2.getText().toString().trim().isEmpty()){
            addr_line_2.setError("Address line 2 is required");
            return false;
        } else {
            addr_line_2.setError(null);
            compile_form_date("address_line_2", addr_line_2.getText().toString());
        }

        if(postcode.getText().toString().trim().isEmpty()){
            postcode.setError("Postcode is required");
            return false;
        } else {
            postcode.setError(null);
            compile_form_date("postcode", postcode.getText().toString());
        }

        if(city.getText().toString().trim().isEmpty()){
            city.setError("City name is required");
            return false;
        } else {
            city.setError(null);
            compile_form_date("city", city.getText().toString());
        }

        if(state.getText().toString().trim().isEmpty()){
            state.setError("State is required");
            return false;
        } else {
            state.setError(null);
            compile_form_date("state", state.getText().toString());
            compile_form_date("state_id", String.valueOf(session.getIdValue(Session.STATE_ID)));
        }

        if(country.getText().toString().trim().isEmpty()){
            country.setError("Country name is required");
            return false;
        } else {
            country.setError(null);
            compile_form_date("country", country.getText().toString());
            compile_form_date("country_id", String.valueOf(session.getIdValue(Session.COUNTRY_ID)));
        }

        return true;
    }

    public void parse_response(String result){
        // showMessage(result);

        try {
            JSONObject response = new JSONObject(result);

            if(response.optBoolean("success")){
                session.setKeyValue(Session.ADDR_LINE_1, get_single_data("address_line_1"));
                session.setKeyValue(Session.ADDR_LINE_2, get_single_data("address_line_2"));
                session.setKeyValue(Session.POSTCODE, get_single_data("postcode"));
                session.setKeyValue(Session.CITY, get_single_data("city"));
                session.setKeyValue(Session.STATE, get_single_data("state"));
                session.setKeyValue(Session.COUNTRY, get_single_data("country"));
                display_status("Successfully updated your address");
                menuitem.setEnabled(true);
            } else {
                display_status("Error while updating your address");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void display_status(String message){
        Snackbar snackbar = Snackbar
                .make(findViewById(R.id.more_info_layout), Html.fromHtml("<font color=\"#ffffff\">"+ message +"</font>"), Snackbar.LENGTH_LONG);
        View snackBarView = snackbar.getView();
        if(Build.VERSION.SDK_INT >= 23 )
            snackBarView.setBackgroundColor(getResources().getColor(R.color.mt_red, null));
        else
            snackBarView.setBackgroundColor(getResources().getColor(R.color.mt_red));
        snackbar.show();
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

    private void compile_form_date(String key, String value){

        try {
            data.put(key, value);
            Log.d("selected data", data.toString() + " ------------------");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private JSONObject getData(){
        return this.data;
    }
    private String get_single_data(String key) { return data.optString(key); }

    public void hideSoftKeyboard() {
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }
}
