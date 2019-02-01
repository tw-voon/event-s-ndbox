package com.kuchingitsolution.asus.eventmanagement.setting;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.os.Process;

import com.kuchingitsolution.asus.eventmanagement.HomeActivity;
import com.kuchingitsolution.asus.eventmanagement.R;
import com.kuchingitsolution.asus.eventmanagement.config.Session;

public class SettingActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView language_value;
    private LinearLayout language_section;
    private Session session;
    private CharSequence[] language = {"English","Bahasa Malaysia","中文"};
    private int selected_language;
    private AlertDialog alertDialog1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.action_settings);
        }

        setup_element();
    }

    private void setup_element(){

        language_section = findViewById(R.id.language_section);
        language_value = findViewById(R.id.language_value);

        session = new Session(this);

        switch (session.getKeyValue(Session.LANGUAGE)){

            case "en":
                selected_language = 0;
                language_value.setText("English");
                break;
            case "ms":
                selected_language = 1;
                language_value.setText("BM");
                break;
            case "zh":
                selected_language = 2;
                language_value.setText("中文");
                break;

                default:
                    selected_language = 0;
                    language_value.setText("English");
                    break;
        }

        language_section.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choose_language();
            }
        });

    }

    public void choose_language(){

        AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
        builder.setTitle("Select Your Choice");
        builder.setSingleChoiceItems(language, selected_language, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {

                switch(item) {
                    case 0:
                        session.setKeyValue(Session.LANGUAGE, "en");
                        Log.d("language_file", ": en");
                        break;
                    case 1:
                        session.setKeyValue(Session.LANGUAGE, "ms");
                        Log.d("language_file", ": ms");
                        break;
                    case 2:
                        session.setKeyValue(Session.LANGUAGE, "zh");
                        Log.d("language_file", ": zh");
                        break;
                }
                alertDialog1.dismiss();
                Intent intent = new Intent(SettingActivity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                Process.killProcess(Process.myPid());
                System.exit(0);
            }
        });
        alertDialog1 = builder.create();
        alertDialog1.show();

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
