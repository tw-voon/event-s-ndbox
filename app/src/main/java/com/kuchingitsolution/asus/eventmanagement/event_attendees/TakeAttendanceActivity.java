package com.kuchingitsolution.asus.eventmanagement.event_attendees;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.kuchingitsolution.asus.eventmanagement.R;
import com.kuchingitsolution.asus.eventmanagement.config.CommonApiAsync;
import com.kuchingitsolution.asus.eventmanagement.config.Config;
import com.kuchingitsolution.asus.eventmanagement.config.Session;
import com.kuchingitsolution.asus.eventmanagement.event_details.RegisterEventActivity;

import org.json.JSONException;
import org.json.JSONObject;

import static android.view.View.GONE;

public class TakeAttendanceActivity extends AppCompatActivity {

    private Button buttonScan;
    private TextView textViewName, textViewEmail, initialLabel, label_username, label_email;
    private TextView label_event_attendees, textViewEventAttendees, textViewEventTitle;

    //qr code scanner object
    private IntentIntegrator qrScan;
    private Session session;
    private String event_title;
    private JSONObject form_data = new JSONObject();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_attendance);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        session = new Session(this);
        String event_id;

        if(getIntent().getStringExtra("event_id") != null) {
            event_id = getIntent().getStringExtra("event_id");
            event_title = getIntent().getStringExtra("event_title");
            session.setKeyValue("current_attendance_event_id", event_id);
            session.setKeyValue("current_attendance_event_title", event_title);
        } else {
            event_id = session.getKeyValue("current_attendance_event_id");
            event_title = session.getKeyValue("current_attendance_event_title");
        }

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(event_title);
        }

        setFormData("event_id", event_id);

        //View objects
        buttonScan = findViewById(R.id.buttonScan);
        textViewName = findViewById(R.id.textViewName);
        textViewEmail = findViewById(R.id.textViewEmail);
        initialLabel = findViewById(R.id.initial_label);
        label_email = findViewById(R.id.label_email);
        label_username = findViewById(R.id.label_username);
        label_event_attendees = findViewById(R.id.label_event_attendees);
        textViewEventAttendees = findViewById(R.id.textViewEventAttendees);
        textViewEventTitle = findViewById(R.id.textViewEventTitle);

        textViewEmail.setVisibility(GONE);
        textViewName.setVisibility(GONE);
        label_email.setVisibility(GONE);
        label_username.setVisibility(GONE);

        textViewEventTitle.setText(event_title);

        //intializing scan object
        qrScan = new IntentIntegrator(this);
        qrScan.setBeepEnabled(false);
        qrScan.setBarcodeImageEnabled(true);
        qrScan.setOrientationLocked(false);

        buttonScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qrScan.initiateScan();
            }
        });

        setFormData("type", "attendance_details");
        attendanceApiCall(Config.TAKE_EVENT_ATTENDANCE_DETAILS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            //if qrcode has nothing in it
            if (result.getContents() == null) {
                Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
            } else {
                //if qr contains data
                try {
                    //converting the data to json
                    JSONObject obj = new JSONObject(result.getContents());
                    //setting values to textviews
                    textViewName.setText(obj.optString("name"));
                    setFormData("user_id", obj.optString("user_id"));
                    setFormData("attendee_id", obj.optString("attendee_id"));

                    setFormData("type", "take_attendance");
                    attendanceApiCall(Config.TAKE_ATTENDANCE);
                } catch (JSONException e) {
                    e.printStackTrace();
                    //if control comes here
                    //that means the encoded format not matches
                    //in this case you can display whatever data is available on the qrcode
                    //to a toast
                    Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
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

    private void attendanceApiCall(String url) {
        CommonApiAsync commonApiAsync = new CommonApiAsync(this, form_data);
        commonApiAsync.execute(url);
    }

    public void parseResponse(String s) {
        // showMessage(s);

        if(s == null)
            return ;

        switch (form_data.optString("type")) {
            case "attendance_details":
                parseEventDetails(s);
                break;
            case "take_attendance":
                parseAttendanceResponse(s);
                break;
        }
    }

    private void parseEventDetails(String s) {
        try {
            JSONObject result = new JSONObject(s);
            JSONObject data = result.getJSONObject("data");
            int totalAttendee = data.optInt("attendees");
            int attended = data.optInt("attended");
            String attendees = String.format("%s / %s", totalAttendee, attended);
            textViewEventAttendees.setText(attendees);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void parseAttendanceResponse(String s) {
        try {
            JSONObject result = new JSONObject(s);
            if(result.optBoolean("success")) {

                JSONObject data = result.getJSONObject("data");
                JSONObject user = data.getJSONObject("user");
                JSONObject attendee = data.getJSONObject("attendee");
                int totalAttendee = data.optInt("attendees");
                int attended = data.optInt("attended");
                String attendees = String.format("%s / %s", totalAttendee, attended);

                textViewEventAttendees.setText(attendees);
                textViewName.setText(user.optString("name"));
                textViewEmail.setText(user.optString("email"));

                textViewName.setVisibility(View.VISIBLE);
                textViewEmail.setVisibility(View.VISIBLE);
                label_username.setVisibility(View.VISIBLE);
                label_email.setVisibility(View.VISIBLE);
                initialLabel.setVisibility(GONE);

                display_status("Successfully take attendance");

            } else {
                showMessage(result.optString("msg"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showMessage(String message) {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(TakeAttendanceActivity.this);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialogBuilder.show();
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

    private void setFormData(String key, Object value) {
        try {
            form_data.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
