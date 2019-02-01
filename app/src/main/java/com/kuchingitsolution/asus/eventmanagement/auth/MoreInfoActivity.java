package com.kuchingitsolution.asus.eventmanagement.auth;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.kuchingitsolution.asus.eventmanagement.HomeActivity;
import com.kuchingitsolution.asus.eventmanagement.R;
import com.kuchingitsolution.asus.eventmanagement.config.CommonApiAsync;
import com.kuchingitsolution.asus.eventmanagement.config.Config;
import com.kuchingitsolution.asus.eventmanagement.config.DB;
import com.kuchingitsolution.asus.eventmanagement.config.Session;
import com.kuchingitsolution.asus.eventmanagement.profile.CountryStateActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.Calendar;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;

public class MoreInfoActivity extends AppCompatActivity {

    private EditText mobile_number, address_line_1, address_line_2, postcode, city;
    private LinearLayout dob_selection;
    private TextView dob_text, state, country;
    private RadioGroup gender;
    private String date = "";
    private CircularProgressButton register;
    private int gender_type = 1;
    private JSONObject form_data = new JSONObject();
    private Session session;
    private DB db;
    private String[] countries_array = {"Please select your country"}, states_array = {"Please select your states"};
    private ArrayAdapter<String> country_adapter, state_adapter;
    private static int SELECT_STATE_CODE = 1001, SELECT_COUNTRY_CODE = 1002;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_info);

        setup_element();
//        setup_local();
    }

    private void setup_element(){

        mobile_number = findViewById(R.id.form_mobile);
        address_line_1 = findViewById(R.id.form_address_line_1);
        address_line_2 = findViewById(R.id.form_address_line_2);
        postcode = findViewById(R.id.form_postcode);
        city = findViewById(R.id.form_city);
        state = findViewById(R.id.form_state);
        country = findViewById(R.id.form_country);

        dob_selection = findViewById(R.id.dob_selection);
        dob_text = findViewById(R.id.dob_date);
        gender = findViewById(R.id.form_gender);
        register = findViewById(R.id.form_register);

        db = new DB(this);
        session = new Session(this);
        compile_form_date("user_id", session.getKeyValue(Session.USER_ID));

        setup_listener();

    }

    private void setup_local(){

        compile_form_date("type", "get_country_state");
        CommonApiAsync commonApiAsync = new CommonApiAsync(this, form_data);
        commonApiAsync.execute(Config.GET_LOCAL);

    }

    public void pickDate(){

        // Get Current Date
        final Calendar c = Calendar.getInstance();
        final int mYear = c.get(Calendar.YEAR);
        final int mMonth = c.get(Calendar.MONTH);
        final int mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        date = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                        dob_text.setText(date);
                    }
                }, mYear, mMonth, mDay);

        if(datePickerDialog.getWindow() != null) {
            datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        datePickerDialog.getDatePicker().setMaxDate(c.getTimeInMillis());
        datePickerDialog.setTitle("");
        datePickerDialog.show();

    }

    private void setup_listener(){

        dob_selection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickDate();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validate_form()){
                    save_user_profile();
                }
            }
        });

        gender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                switch (i){
                    case R.id.form_male:
                        gender_type = 1;
                        break;
                    case R.id.form_female:
                        gender_type = 2;
                        break;
                }
            }
        });

        country.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MoreInfoActivity.this, CountryStateActivity.class);
                intent.putExtra("type", "get_countries");
                intent.putExtra("action_type", "Please select your country");
                startActivityForResult(intent, SELECT_COUNTRY_CODE);
            }
        });

        state.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MoreInfoActivity.this, CountryStateActivity.class);
                intent.putExtra("type", "get_states");
                intent.putExtra("action_type", "Please select your state");
                startActivityForResult(intent, SELECT_STATE_CODE);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_COUNTRY_CODE && resultCode == RESULT_OK && data != null) {
            country.setText(data.getStringExtra("name"));
            session.setKeyValue(Session.COUNTRY, data.getStringExtra("name"));
            session.setIdValue(Session.COUNTRY_ID, Integer.parseInt(data.getStringExtra("id")));
            compile_form_date("country_id", data.getStringExtra("id"));
            //Toast.makeText(MoreInfoActivity.this, "Selected id: " + data.getStringExtra("name") + " -- " + SELECT_COUNTRY_CODE, Toast.LENGTH_SHORT).show();
        }

        if (requestCode == SELECT_STATE_CODE && resultCode == RESULT_OK && data != null) {
            state.setText(data.getStringExtra("name"));
            session.setKeyValue(Session.STATE, data.getStringExtra("name"));
            session.setIdValue(Session.STATE_ID, Integer.parseInt(data.getStringExtra("id")));
            compile_form_date("state_id", data.getStringExtra("id"));

            //Toast.makeText(MoreInfoActivity.this, "Selected id: " + data.getStringExtra("name") + " -- " + SELECT_STATE_CODE, Toast.LENGTH_SHORT).show();
        }

    }

    private boolean validate_form(){

        if(mobile_number.getText().toString().isEmpty()){
            mobile_number.setError("Mobile number is required");
            return false;
        } else if(!Patterns.PHONE.matcher(mobile_number.getText().toString()).matches()) {
            mobile_number.setError("Mobile number format error");
            return false;
        } else {
            mobile_number.setError(null);
            compile_form_date("mobile_no", mobile_number.getText().toString());
        }

        if(date.isEmpty()){
            display_status("Date of birth is required");
            return false;
        } else {
            compile_form_date("dob", date);
        }

        if(address_line_1.getText().toString().trim().isEmpty()){
            address_line_1.setError("Address line 1 is required");
            return false;
        } else {
            address_line_1.setError(null);
            compile_form_date("address_line_1", address_line_1.getText().toString());
        }

        if(address_line_2.getText().toString().trim().isEmpty()){
            address_line_2.setError("Address line 2 is required");
            return false;
        } else {
            address_line_2.setError(null);
            compile_form_date("address_line_2", address_line_2.getText().toString());
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

        if(state.getText().toString().trim().isEmpty() && session.getIdValue(Session.STATE_ID) == 0){
            state.setError("State name is required");
            return false;
        } else {
            state.setError(null);
            compile_form_date("state", state.getText().toString());
            compile_form_date("state_id", String.valueOf(session.getIdValue(Session.STATE_ID)));
        }

        if(country.getText().toString().trim().isEmpty() && session.getIdValue(Session.COUNTRY_ID) == 0){
            country.setError("Country name is required");
            return false;
        } else {
            country.setError(null);
            compile_form_date("country", country.getText().toString());
            compile_form_date("country_id", String.valueOf(session.getIdValue(Session.COUNTRY_ID)));
        }

        compile_form_date("gender", String.valueOf(gender_type));

        return true;

    }

    private void compile_form_date(String key, String value){

        try {
            form_data.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void save_user_profile(){

        compile_form_date("type", "save_user_profile");
        CommonApiAsync commonApiAsync = new CommonApiAsync(this, form_data);
        commonApiAsync.execute(Config.SAVE_USER_PROFILE);

    }

    public void parse_resposne(String type, String result) {

        switch (type){
            case "get_country_state":
                parse_country_state_list(result);
                break;
            case "save_user_profile":
                parse_user_info(result);
                break;
        }

    }

    private void parse_user_info(String result){
        showMessage(result);

        try {
            JSONObject response = new JSONObject(result);

            if(response.optBoolean("success")){

                session.setKeyValue(Session.MOBILE_NO, response.optString("mobile_no"));
                session.setKeyValue(Session.DOB, response.optString("dob"));
                session.setKeyValue(Session.ADDR_LINE_1, response.optString("address_line_1"));
                session.setKeyValue(Session.ADDR_LINE_2, response.optString("address_line_2"));
                session.setKeyValue(Session.POSTCODE, response.optString("postcode"));
                session.setKeyValue(Session.STATE, response.optString("state"));
                session.setKeyValue(Session.CITY, response.optString("city"));
                session.setKeyValue(Session.COUNTRY, response.optString("country"));
                session.setIdValue(Session.GENDER, response.optInt("gender"));
                session.setBooleanKey(Session.COMPLETED_PROFILE, true);

                Intent intent = new Intent(this, HomeActivity.class);
                startActivity(intent);
                finish();

            } else {
                display_status(response.optString("msg", "Error while saving your profile"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void parse_country_state_list(String result){
//        showMessage(result);

        try {

            JSONObject data = new JSONObject(result);
            JSONArray countries = data.getJSONArray("countries");
            JSONArray states = data.optJSONArray("states");

//            DB db = new DB(this);
            db.insertCountry(countries);
            db.insertState(states);

//            parse_country_states();

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void display_status(String message) {
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
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MoreInfoActivity.this);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialogBuilder.show();
    }

    public String[] generate_array(JSONArray theArray,String key)
    {
        int i = theArray.length();
        int n = ++i;
        String[] newArray = new String[n];
        for(int cnt=0;cnt<i;cnt++) {
            JSONObject data = theArray.optJSONObject(cnt);
            if(data!= null)
                newArray[cnt] = data.optString(key);

        }
        return newArray;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
