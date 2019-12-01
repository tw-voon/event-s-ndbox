package com.kuchingitsolution.asus.eventmanagement.auth;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.kuchingitsolution.asus.eventmanagement.HomeActivity;
import com.kuchingitsolution.asus.eventmanagement.R;
import com.kuchingitsolution.asus.eventmanagement.config.CommonApiAsync;
import com.kuchingitsolution.asus.eventmanagement.config.Config;
import com.kuchingitsolution.asus.eventmanagement.config.Session;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;

public class LoginActivity extends AppCompatActivity {

    private EditText username, password;
    private CircularProgressButton submit;
    private JSONObject form_data = new JSONObject();
    private Session session;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setup_view_element();
        setup_element_listener();
    }

    private void setup_view_element(){

        submit = findViewById(R.id.form_login);
        username = findViewById(R.id.edtEmail);
        password = findViewById(R.id.edtPassword);
        handler = new Handler();

        session = new Session(this);
    }

    private void setup_element_listener(){

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(validate_info()){
                    submit.startAnimation();
                    CommonApiAsync commonApiAsync = new CommonApiAsync(LoginActivity.this, get_form_data());
                    commonApiAsync.execute(Config.APPS_LOGIN);
                }

            }
        });

    }

    private boolean validate_info(){

        //submit.startAnimation();
        String email = username.getText().toString().trim();
        String pswd = password.getText().toString().trim();

        if(email.trim().isEmpty()){
            display_status("Email is required...");
            return false;
        } else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            display_status("Email format incorrect..");
            return false;
        }

        if(pswd.trim().isEmpty()){
            display_status("Password is required");
            return false;
        }

        generate_form_data(email, pswd);

        return true;
    }

    private void generate_form_data(String email, String password){

        try {
            form_data.put("email", email);
            form_data.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private JSONObject get_form_data(){
        return form_data;
    }

    public void login_response(String result){
        Log.d("login_result", "response: " + result);

        if(result == null){
            Toast.makeText(this, "An error has occur. Please try again later", Toast.LENGTH_SHORT).show();
            submit.doneLoadingAnimation(R.color.mt_red, BitmapFactory.decodeResource(getResources(), R.drawable.ic_close_white_24dp));
            submit.revertAnimation();
            return;
        }

        if(result.equals("fail")){
            Toast.makeText(this, "Wrong Email or Password", Toast.LENGTH_SHORT).show();
            submit.doneLoadingAnimation(R.color.mt_red, BitmapFactory.decodeResource(getResources(), R.drawable.ic_close_white_24dp));
            submit.revertAnimation();
            return;
        }

        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONObject info = jsonObject.getJSONObject("data");
            JSONArray role = jsonObject.getJSONArray("roles");
            JSONObject roles = role.getJSONObject(0);
            Log.d("logininfo--", "info -- " + role.toString());
            session.setKeyValue(Session.USERNAME, info.getString("name"));
            session.setKeyValue(Session.EMAIL, info.getString("email"));
            session.setKeyValue(Session.USER_ID, info.getString("id"));
            session.setKeyValue(Session.ROLE_CODE, roles.optString("roles_id"));
            session.setBooleanKey(Session.LOGGED_IN, true);
            JSONObject extra = roles.optJSONObject("extra_info");
            if(extra != null){
                session.setKeyValue(Session.MOBILE_NO, extra.optString("contact_no"));
                session.setKeyValue(Session.DOB, extra.optString("dob"));
                session.setKeyValue(Session.ADDR_LINE_1, extra.optString("address_line_1"));
                session.setKeyValue(Session.ADDR_LINE_2, extra.optString("address_line_2"));
                session.setKeyValue(Session.POSTCODE, extra.optString("postcode"));
                session.setKeyValue(Session.STATE, extra.optString("state"));
                session.setKeyValue(Session.CITY, extra.optString("city"));
                session.setKeyValue(Session.COUNTRY, extra.optString("country"));
                session.setIdValue(Session.GENDER, extra.optInt("gender"));
                session.setBooleanKey(Session.COMPLETED_PROFILE, true);
            }

            handler.postDelayed(q, 800);
            handler.postDelayed(r,1400);
//            redirect();
        } catch (JSONException e) {
            e.printStackTrace();
            handler.postDelayed(q, 1000);
            display_status("Error while login, please try again.");
        }

    }

    final Runnable q = new Runnable() {
        @Override
        public void run() {
            submit.doneLoadingAnimation(R.color.mt_red, BitmapFactory.decodeResource(getResources(), R.drawable.ic_close_white_24dp));
            submit.revertAnimation();
        }
    };

    final Runnable r = new Runnable() {
        public void run() {
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }
    };

    private void display_status(String message){
        Snackbar snackbar = Snackbar
                .make(findViewById(R.id.login_activity), Html.fromHtml("<font color=\"#ffffff\">"+ message +"</font>"), Snackbar.LENGTH_LONG);
        View snackBarView = snackbar.getView();
        if(Build.VERSION.SDK_INT >= 23 )
            snackBarView.setBackgroundColor(getResources().getColor(R.color.mt_red, null));
        else
            snackBarView.setBackgroundColor(getResources().getColor(R.color.mt_red));
        snackbar.show();
    }

    public void to_register(View view) {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    public void forgot_password(View view) {
        Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
        startActivity(intent);
    }
}
