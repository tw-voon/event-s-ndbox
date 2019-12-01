package com.kuchingitsolution.asus.eventmanagement.event_details;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Handler;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.kuchingitsolution.asus.eventmanagement.R;
import com.kuchingitsolution.asus.eventmanagement.config.CommonApiAsync;
import com.kuchingitsolution.asus.eventmanagement.config.Config;
import com.kuchingitsolution.asus.eventmanagement.config.DB;
import com.kuchingitsolution.asus.eventmanagement.config.Session;
import com.kuchingitsolution.asus.eventmanagement.profile.CountryStateActivity;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;

public class RegisterEventActivity extends AppCompatActivity {

    private String event_title, event_id, event_location, event_time;
    private EditText mobile_number, address_line_1, address_line_2, postcode, city;
    private TextView event_title_label, event_time_location, state, country;
    private LinearLayout dob_selection;
    private AVLoadingIndicatorView avi;
    private TextView dob_text;
    private RadioGroup gender;
    private RadioButton male, female;
    private String date = "";
    private CircularProgressButton register;
    private int gender_type = 1;
    private JSONObject form_data = new JSONObject();
    private Session session;
    private DB db;
    private Handler handler;
    private Bitmap bmp;
    private static int SELECT_STATE_CODE = 1001, SELECT_COUNTRY_CODE = 1002;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_event);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();

        if (intent != null) {
            event_title = intent.getStringExtra("event_title");
            event_id = intent.getStringExtra("event_id");
            event_location = intent.getStringExtra("event_location");
            event_time = intent.getStringExtra("event_time");
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(event_title);
        }

        setup_element();
    }

    private void setup_element() {

        handler = new Handler();
        event_title_label = findViewById(R.id.event_title);
        event_time_location = findViewById(R.id.event_time_location);
        mobile_number = findViewById(R.id.form_mobile);
        address_line_1 = findViewById(R.id.form_address_line_1);
        address_line_2 = findViewById(R.id.form_address_line_2);
        postcode = findViewById(R.id.form_postcode);
        city = findViewById(R.id.form_city);
        state = findViewById(R.id.form_state);
        country = findViewById(R.id.form_country);
        male = findViewById(R.id.form_male);
        female  = findViewById(R.id.form_female);
        avi = findViewById(R.id.avi);

        dob_selection = findViewById(R.id.dob_selection);
        dob_text = findViewById(R.id.dob_date);
        gender = findViewById(R.id.form_gender);
        register = findViewById(R.id.form_register);

        event_title_label.setText(event_title);
        event_time_location.setText(event_time);
//        String time_location = String.format("")

        db = new DB(this);
        session = new Session(this);
        compile_form_date("user_id", session.getKeyValue(Session.USER_ID));
        compile_form_date("event_id", event_id);

        setup_listener();
        auto_fill();
        avi.hide();
    }

    private void auto_fill(){

        mobile_number.setText(session.getKeyValue(Session.MOBILE_NO));
        gender_type = session.getIdValue(Session.GENDER);
        dob_text.setText(session.getKeyValue(Session.DOB));
        date = session.getKeyValue(Session.DOB);
        address_line_1.setText(session.getKeyValue(Session.ADDR_LINE_1));
        address_line_2.setText(session.getKeyValue(Session.ADDR_LINE_2));
        postcode.setText(session.getKeyValue(Session.POSTCODE));
        city.setText(session.getKeyValue(Session.CITY));
        state.setText(session.getKeyValue(Session.STATE));
        country.setText(session.getKeyValue(Session.COUNTRY));

        if(gender_type == 2){
            female.setChecked(true);
        }

    }

    public void pickDate() {

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

        if(!session.getKeyValue(Session.DOB).equals("0")) {
            datePickerDialog.getDatePicker().updateDate(
                    getCustomDateFormat(session.getKeyValue(Session.DOB), "yyyy"),
                    getCustomDateFormat(session.getKeyValue(Session.DOB), "MM"),
                    getCustomDateFormat(session.getKeyValue(Session.DOB), "dd")
            );
        } else {
            datePickerDialog.getDatePicker().setMaxDate(c.getTimeInMillis());
        }

        datePickerDialog.setTitle("");
        datePickerDialog.show();

    }

    private void setup_listener() {

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
                    register.startAnimation();
                    register_user_to_events();
                }
            }
        });

        gender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                switch (i) {
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
                Intent intent = new Intent(RegisterEventActivity.this, CountryStateActivity.class);
                intent.putExtra("type", "get_countries");
                intent.putExtra("action_type", "Please select your country");
                startActivityForResult(intent, SELECT_COUNTRY_CODE);
            }
        });

        state.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterEventActivity.this, CountryStateActivity.class);
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
            // Toast.makeText(MoreInfoActivity.this, "Selected id: " + data.getStringExtra("name") + " -- " + SELECT_COUNTRY_CODE, Toast.LENGTH_SHORT).show();
        }

        if (requestCode == SELECT_STATE_CODE && resultCode == RESULT_OK && data != null) {
            state.setText(data.getStringExtra("name"));
            session.setKeyValue(Session.STATE, data.getStringExtra("name"));
            session.setIdValue(Session.STATE_ID, Integer.parseInt(data.getStringExtra("id")));
            compile_form_date("state_id", data.getStringExtra("id"));

            // Toast.makeText(Re.this, "Selected id: " + data.getStringExtra("name") + " -- " + SELECT_STATE_CODE, Toast.LENGTH_SHORT).show();
        }

    }

    private void register_user_to_events(){

        compile_form_date("type", "register_user");
        CommonApiAsync commonApiAsync = new CommonApiAsync(this, form_data);
        commonApiAsync.execute(Config.REGISTER_EVENT);

    }

    private boolean validate_form() {

        if (mobile_number.getText().toString().isEmpty()) {
            mobile_number.setError("Mobile number is required");
            return false;
        } else if (!Patterns.PHONE.matcher(mobile_number.getText().toString()).matches()) {
            mobile_number.setError("Mobile number format error");
            return false;
        } else {
            mobile_number.setError(null);
            compile_form_date("mobile_no", mobile_number.getText().toString());
        }

        if (date.isEmpty()) {
            display_status("Date of birth is required");
            return false;
        } else {
            compile_form_date("dob", date);
        }

        if (address_line_1.getText().toString().trim().isEmpty()) {
            address_line_1.setError("Address line 1 is required");
            return false;
        } else {
            address_line_1.setError(null);
            compile_form_date("address_line_1", address_line_1.getText().toString());
        }

        if (address_line_2.getText().toString().trim().isEmpty()) {
            address_line_2.setError("Address line 2 is required");
            return false;
        } else {
            address_line_2.setError(null);
            compile_form_date("address_line_2", address_line_2.getText().toString());
        }

        if (postcode.getText().toString().trim().isEmpty()) {
            postcode.setError("Postcode is required");
            return false;
        } else {
            postcode.setError(null);
            compile_form_date("postcode", postcode.getText().toString());
        }

        if (city.getText().toString().trim().isEmpty()) {
            city.setError("City name is required");
            return false;
        } else {
            city.setError(null);
            compile_form_date("city", city.getText().toString());
        }

        if (state.getText().toString().trim().isEmpty()) {
            state.setError("City name is required");
            return false;
        } else {
            state.setError(null);
            compile_form_date("state", state.getText().toString());
            compile_form_date("state_id", String.valueOf(session.getIdValue(Session.STATE_ID)));
        }

        if (country.getText().toString().trim().isEmpty()) {
            country.setError("City name is required");
            return false;
        } else {
            country.setError(null);
            compile_form_date("country", country.getText().toString());
            compile_form_date("country_id", String.valueOf(session.getIdValue(Session.COUNTRY_ID)));
        }

        compile_form_date("gender", String.valueOf(gender_type));

        return true;

    }

    private void compile_form_date(String key, String value) {

        try {
            form_data.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void parse_response(String type, String result){

        switch (type){
            case "register_user":
                // showMessage(result);
                parse_register_event(result);
                break;
        }

    }

    private void parse_register_event(String result){

        try {
            JSONObject response = new JSONObject(result);

            if(response.optBoolean("success")){
                // display_status(response.optString("data"));
                JSONObject data = new JSONObject(response.optString("data"));
                this.generate_qrcode(data.optString("user_id"), data.optString("id"));
                this.gatherInfo(data.optString("user_id"), data.optString("id"), data.optString("event_id"));
//                handler.postDelayed(r, 1200);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        register.doneLoadingAnimation(R.color.mt_red, BitmapFactory.decodeResource(getResources(), R.drawable.ic_close_white_24dp));
        register.revertAnimation();
    }

    private void SendImage( final JSONObject events) {

        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.POST, Config.UPLOAD_QR, events,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        Log.d("Response", response.toString());
                        display_status("Successfully register");

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                onBackPressed();
                            }
                        }, 1000); // Millisecond 1000 = 1 sec
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", error.toString());
                        display_status("Error during the registration");
                        // showMessage(error.toString());
                    }
                }
        );
        {
            int socketTimeout = 30000;
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            getRequest.setRetryPolicy(policy);
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(getRequest);
        }
    }



    private void generate_qrcode(String user_id, String attendee_id) {
//        this.bmp = QRCodeHelper.newInstance(this)
//                .setContent(String.format("{user_id: %s, attendee_id: %s}", user_id, attendee_id))
//                .setWidthAndHeight(250, 250)
//                .setErrorCorrectionLevel(ErrorCorrectionLevel.Q)
//                .getQRCOde();
        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            this.bmp = barcodeEncoder.encodeBitmap(String.format("{user_id: %s, attendee_id: %s}", user_id, attendee_id), BarcodeFormat.QR_CODE, 500, 500);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void gatherInfo(String user_id, String attendee_id, String event_id) {
        JSONObject form_data = new JSONObject();
        try {
            form_data.put("user_id", user_id);
            form_data.put("attendee_id", attendee_id);
            form_data.put("event_id", event_id);
            form_data.put("qr_code", getStringImage());
            this.SendImage(form_data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String getStringImage() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        this.bmp.compress(Bitmap.CompressFormat.JPEG, 30, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    final Runnable r = new Runnable() {
        public void run() {
//            Intent intent = new Intent(RegisterEventActivity.this, DetailEventsActivity.class);
//            intent.putExtra("event_id", event_id);
//            startActivity(intent);
            onBackPressed();
            finish();
        }
    };

    private void display_status(String message) {
        Snackbar snackbar = Snackbar
                .make(findViewById(R.id.more_info_layout), Html.fromHtml("<font color=\"#ffffff\">" + message + "</font>"), Snackbar.LENGTH_LONG);
        View snackBarView = snackbar.getView();
        if (Build.VERSION.SDK_INT >= 23)
            snackBarView.setBackgroundColor(getResources().getColor(R.color.mt_red, null));
        else
            snackBarView.setBackgroundColor(getResources().getColor(R.color.mt_red));
        snackbar.show();
    }

    private void showMessage(String message) {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RegisterEventActivity.this);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialogBuilder.show();
    }

    private int getCustomDateFormat(String dateString, String format){

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        try {

            SimpleDateFormat dateFormat3 = new SimpleDateFormat(
                    format, Locale.getDefault());
            Date date = sdf.parse(dateString);
            Log.d("getCustomDateFormat", dateFormat3.format(date));
            if(format.equals("MM"))
                return Integer.parseInt(dateFormat3.format(date)) - 1;
            else
                return Integer.parseInt(dateFormat3.format(date));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
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
