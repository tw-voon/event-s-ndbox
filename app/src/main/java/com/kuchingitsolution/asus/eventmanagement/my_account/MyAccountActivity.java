package com.kuchingitsolution.asus.eventmanagement.my_account;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kuchingitsolution.asus.eventmanagement.HomeActivity;
import com.kuchingitsolution.asus.eventmanagement.R;
import com.kuchingitsolution.asus.eventmanagement.apps_info.AppsInfoActivity;
import com.kuchingitsolution.asus.eventmanagement.auth.LoginActivity;
import com.kuchingitsolution.asus.eventmanagement.config.DB;
import com.kuchingitsolution.asus.eventmanagement.config.Session;
import com.kuchingitsolution.asus.eventmanagement.officer.MyOfficerActivity;
import com.kuchingitsolution.asus.eventmanagement.profile.MyProfileActivity;
import com.kuchingitsolution.asus.eventmanagement.setting.SettingActivity;

public class MyAccountActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private LinearLayout manage_officer_section,edit_profile_section;
    private TextView manage_officer_label, view_app_info;
    private Button logout;
    private Session session;
    private DB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        setup_element();
    }

    private void setup_element(){

        manage_officer_section = findViewById(R.id.manage_officer_section);
        manage_officer_label = findViewById(R.id.add_officer_label);
        edit_profile_section = findViewById(R.id.edit_profile_section);
        view_app_info = findViewById(R.id.view_app_info);
        logout = findViewById(R.id.logout_from_app);
        session = new Session(this);
        db = new DB(this);

        if(!session.getKeyValue(Session.ROLE_CODE).equals("4")){
            manage_officer_section.setVisibility(View.GONE);
            manage_officer_label.setVisibility(View.GONE);
        }

        view_app_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyAccountActivity.this, AppsInfoActivity.class);
                startActivity(intent);
            }
        });

        edit_profile_section.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyAccountActivity.this, MyProfileActivity.class);
                startActivity(intent);
            }
        });

        manage_officer_section.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyAccountActivity.this, MyOfficerActivity.class);
                startActivity(intent);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMessage();
            }
        });
    }

    private void showMessage(){
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(R.string.log_out);
        alertDialogBuilder.setMessage(R.string.logout_desc);
        alertDialogBuilder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                session.setBooleanKey(Session.LOGGED_IN, false);
                session.clearPreference();
                db.clearTable();
                Intent intent = new Intent(MyAccountActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
//                ((HomeActivity)getApplicationContext()).check_auth();
            }
        });
        alertDialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialogBuilder.show();
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

    public void to_app_setting(View view) {
        Intent intent = new Intent(MyAccountActivity.this, SettingActivity.class);
        startActivity(intent);
    }
}
