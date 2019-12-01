package com.kuchingitsolution.asus.eventmanagement.profile;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.kuchingitsolution.asus.eventmanagement.R;
import com.kuchingitsolution.asus.eventmanagement.config.CommonApiAsync;
import com.kuchingitsolution.asus.eventmanagement.config.Config;
import com.kuchingitsolution.asus.eventmanagement.config.Session;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MyProfileActivity extends AppCompatActivity {

    private LinearLayout username_section, email_section, password_section, dob_selection, gender_selection, address_selection;
    private TextView username, email, password, dob_text, gender_text, addr_line_1, addr_line_2, postcode,state;
    private EditText dialog_edittext;
    private Toolbar toolbar;
    private Session session;
    private JSONObject form_data = new JSONObject();
    private ArrayList mSelectedItems;
    private String date;
    private int gender_type = 0;
    private RadioButton male, female;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.title_Profile);
        }

        setup_element();
        compile_data("user_id", session.getKeyValue(Session.USER_ID));
    }

    private void setup_element(){

        username_section = findViewById(R.id.username_section);
        email_section = findViewById(R.id.email_section);
        password_section = findViewById(R.id.password_section);
        session = new Session(this);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        email = findViewById(R.id.email);

        username.setText(session.getKeyValue(Session.USERNAME));
        email.setText(session.getKeyValue(Session.EMAIL));
        password.setText(R.string.tap_change);

        dob_selection = findViewById(R.id.dob_selection);
        gender_selection = findViewById(R.id.gender_selection);
        address_selection = findViewById(R.id.address_selection);
        dob_text = findViewById(R.id.dob_date);
        gender_text = findViewById(R.id.form_gender);
        addr_line_1 = findViewById(R.id.form_address_line_1);
        addr_line_2 = findViewById(R.id.form_address_line_2);
        postcode = findViewById(R.id.form_postcode_with_city);
        state = findViewById(R.id.form_state_with_country);

        setup_listener();
        // aut_fill();
    }

    private void setup_listener(){

        username_section.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMessage("username");
            }
        });

        email_section.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                display_status(getString(R.string.email_change));
            }
        });

        password_section.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMessage("password");
            }
        });

        dob_selection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickDate();
            }
        });

        gender_selection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choose_gender();
            }
        });

        address_selection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyProfileActivity.this, AddressActivity.class);
                intent.putExtra("action_type", "Editing address");
                startActivity(intent);
            }
        });
    }

    private void aut_fill(){

        dob_text.setText(session.getKeyValue(Session.DOB));

        if(session.getIdValue(Session.GENDER) == 1){
            gender_text.setText("Male");
        } else {
            gender_text.setText("Female");
        }

        addr_line_1.setText(session.getKeyValue(Session.ADDR_LINE_1));
        addr_line_2.setText(session.getKeyValue(Session.ADDR_LINE_2));
        String postcode_city = String.format("%s, %s", session.getKeyValue(Session.POSTCODE), session.getKeyValue(Session.CITY));
        String state_country = String.format("%s, %s", session.getKeyValue(Session.STATE), session.getKeyValue(Session.COUNTRY));

        postcode.setText(postcode_city);
        state.setText(state_country);
    }

    private void showMessage(final String action_type){
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_profile_update, null);
        alertDialogBuilder.setView(dialogView);
        dialog_edittext = dialogView.findViewById(R.id.et_custom_field);

        switch (action_type){
            case "username":
                alertDialogBuilder.setTitle(getString(R.string.username));
                compile_data("type", action_type);
                break;
            case "password":
                alertDialogBuilder.setTitle(getString(R.string.password));
                compile_data("type", action_type);
                break;
        }

        alertDialogBuilder.setPositiveButton(getString(R.string.submit), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String edt_text = dialog_edittext.getText().toString();

                if(edt_text.trim().isEmpty()){
                    display_status(action_type + " cannot be empty");
                    return;
                }

                compile_data("info", edt_text);
                update_profile();
                dialogInterface.dismiss();
            }
        });

        alertDialogBuilder.show();
    }

    private void choose_gender(){

        int current_gender = session.getIdValue(Session.GENDER) == 2 ? 1 : 0;
        mSelectedItems = new ArrayList();  // Where we track the selected items
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Set the dialog title
        builder.setTitle("Please select your gender")
                // Specify the list array, the items to be selected by default (null for none),
                // and the listener through which to receive callbacks when items are selected
                .setSingleChoiceItems(R.array.gender, current_gender,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //showMessage(String.valueOf(which));
                                Log.d("gender_selection--", String.valueOf(which));
                                gender_type = which;
                            }
                        })
                // Set the action buttons
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK, so save the mSelectedItems results somewhere
                        // or return them to the component that opened the dialog
                        ++ gender_type;
                        Log.d("gender_selection", String.valueOf(id));
                        compile_data("info", String.valueOf(gender_type));
                        compile_data("type", "update_gender");
                        update_profile();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        builder.create().show();
        //return builder.create();
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
                        compile_data("info", date);
                        compile_data("type", "update_dob");
                        update_profile();
                    }
                }, mYear, mMonth, mDay);

        if(datePickerDialog.getWindow() != null) {
            datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        //datePickerDialog.getDatePicker().setMaxDate(c.getTimeInMillis());
        Log.d("getCustomDateFormat", session.getKeyValue(Session.DOB));
        datePickerDialog.getDatePicker().updateDate(
                getCustomDateFormat(session.getKeyValue(Session.DOB), "yyyy"),
                getCustomDateFormat(session.getKeyValue(Session.DOB), "MM"),
                getCustomDateFormat(session.getKeyValue(Session.DOB), "dd")
        );
        datePickerDialog.setTitle("");
        datePickerDialog.show();

    }

    private void update_profile(){
        new CommonApiAsync(this, form_data).execute(Config.UPDATE_PROFILE);
    }

    public void parse_profile_info(String s){
        Log.d("Result from server", s + " ");
        // {"user":{"id":2,"roles_id":2,"status":1,"name":"Officers","email":"officer@officer.com","created_at":"2018-04-03 13:17:30","updated_at":"2018-05-06 04:04:03"}}

        try {

            JSONObject data = new JSONObject(s);
            JSONObject user = data.optJSONObject("user");
            session.setKeyValue(Session.USERNAME, user.optString("name")); // update username again
            username.setText(user.optString("name"));

            switch (form_data.optString("type")) {
                case "username":
                    display_status(getString(R.string.username_status));
                    break;
                case "update_dob":
                    display_status("Successfully updated your birth date");
                    session.setKeyValue(Session.DOB, date);
                    break;
                case "update_gender":
                    display_status("Successfully updated your gender");
                    session.setIdValue(Session.GENDER, gender_type);
                    String text = gender_type == 2 ? "Female" : "Male";
                    gender_text.setText(text);
                    break;
                default:
                    display_status(getString(R.string.password_status));
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void compile_data(String key, String value){
        try {
            form_data.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void display_status(String message){
        Snackbar snackbar = Snackbar
                .make(findViewById(R.id.profile_page), Html.fromHtml("<font color=\"#ffffff\">"+ message +"</font>"), Snackbar.LENGTH_LONG);
        View snackBarView = snackbar.getView();
        if(Build.VERSION.SDK_INT >= 23 )
            snackBarView.setBackgroundColor(getResources().getColor(R.color.mt_red, null));
        else
            snackBarView.setBackgroundColor(getResources().getColor(R.color.mt_red));
        snackbar.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        super.onOptionsItemSelected(menuItem);

        int itemId = menuItem.getItemId();
        switch (itemId){
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

    private int getCustomDateFormat(String dateString, String format){

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        try {
//            Date date = sdf.parse(dateString);
//            String formated = new SimpleDateFormat(format, Locale.getDefault()).format(date);
//            Log.d("getCustomDateFormat", formated);
//            return Integer.parseInt(formated);

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
    protected void onResume() {
        super.onResume();
        aut_fill();
    }
}
