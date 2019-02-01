package com.kuchingitsolution.asus.eventmanagement.event_details;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kuchingitsolution.asus.eventmanagement.R;
import com.kuchingitsolution.asus.eventmanagement.config.CommonApiAsync;
import com.kuchingitsolution.asus.eventmanagement.config.Config;
import com.kuchingitsolution.asus.eventmanagement.config.Session;

import org.json.JSONException;
import org.json.JSONObject;

public class TicketActivity extends AppCompatActivity {

    private TextView participant, event_title, event_start_time, event_venue, close;
    private ImageView qr_ticket;
    private Session session;
    private String event_id;
    private JSONObject form_data = new JSONObject();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket);

        setup();
        setupData();
        getAttendeeInfo();
    }

    private void setup() {
        session = new Session(this);
        qr_ticket = findViewById(R.id.qr_ticket);
        participant = findViewById(R.id.participant);
        event_title = findViewById(R.id.event_title);
        event_start_time = findViewById(R.id.event_start_time);
        event_venue = findViewById(R.id.event_venue);
        close = findViewById(R.id.close);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void setupData() {
        Intent intent = getIntent();
        if (intent != null) {

            setupFormData("event_id", intent.getStringExtra("event_id"));
            setupFormData("event_title", intent.getStringExtra("event_title"));
            setupFormData("event_locations", intent.getStringExtra("event_locations"));
            setupFormData("event_time", intent.getStringExtra("event_time"));
            setupFormData("attendee_id", intent.getStringExtra("attendee_id"));

            participant.setText(session.getKeyValue(Session.USERNAME));
            event_title.setText(form_data.optString("event_title"));
            event_start_time.setText(form_data.optString("event_time"));
            event_venue.setText(form_data.optString("event_locations"));
        }
    }

    private void setupFormData(String key, Object value) {
        try {
            form_data.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getAttendeeInfo() {
        CommonApiAsync commonApiAsync = new CommonApiAsync(this, form_data);
        commonApiAsync.execute(Config.ATTENDEE_INFO);
    }

    public void response(String s) {
        // showMessage(s);
        if(s == null) return;

        try {
            JSONObject result = new JSONObject(s);
            if(result.optBoolean("success")) {
                String image_link = Config.IMAGE_CATALOG + result.optJSONObject("data").optString("qrcode");
                //showMessage(image_link);
                Glide.with(this).load(image_link).into(qr_ticket);
            } else {
                // showMessage(result.optString("msg"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showMessage(String message) {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(TicketActivity.this);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialogBuilder.show();
    }
}
