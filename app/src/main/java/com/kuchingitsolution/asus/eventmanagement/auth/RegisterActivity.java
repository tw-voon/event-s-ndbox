package com.kuchingitsolution.asus.eventmanagement.auth;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;

import com.kuchingitsolution.asus.eventmanagement.HomeActivity;
import com.kuchingitsolution.asus.eventmanagement.R;
import com.kuchingitsolution.asus.eventmanagement.config.CommonApiAsync;
import com.kuchingitsolution.asus.eventmanagement.config.Config;
import com.kuchingitsolution.asus.eventmanagement.config.Session;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;

public class RegisterActivity extends AppCompatActivity {

    private EditText username, email, password, confirm_password;
    private CircularProgressButton register;
    private JSONObject form_data = new JSONObject();
    private Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        setup_element();
        setup_listener();
    }

    private void setup_element(){

        session = new Session(this);
        username = findViewById(R.id.edtUsername);
        email = findViewById(R.id.edtEmail);
        password = findViewById(R.id.edtPassword);
        confirm_password = findViewById(R.id.edtPasswordAgn);
        register = findViewById(R.id.form_register);

    }

    private void setup_listener(){

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validate_form()){
                    register.startAnimation();
                    Log.d("result", form_data.toString() + " - ");
                    //display_status("All success" + form_data.optString("username"));
                    register();
                }
            }
        });

    }

    private void register(){
        new CommonApiAsync(this, form_data).execute(Config.REGISTER_USER);
    }

    public void parse_register_result(String s){

        Log.d("result from server", s + " --");

        register.doneLoadingAnimation(R.color.mt_red, BitmapFactory.decodeResource(getResources(), R.drawable.ic_close_white_24dp));
        register.revertAnimation();

        if(s == null){
            display_status("An error had occur. Please try again later");
            form_data = new JSONObject();
            return;
        }

        if(s.equals("exists")){
            display_status("This email already exists");
            form_data = new JSONObject();
            return;
        }

        try {

//            JSONObject user = new JSONObject(s);
            JSONObject jsonObject = new JSONObject(s);
            JSONObject info = jsonObject.getJSONObject("data");
            JSONArray role = jsonObject.getJSONArray("roles");
            JSONObject roles = role.getJSONObject(0);
            Log.d("info--", "info -- " + info.toString());
            session.setKeyValue(Session.USERNAME, info.getString("name"));
            session.setKeyValue(Session.EMAIL, info.getString("email"));
            session.setKeyValue(Session.USER_ID, info.getString("id"));
            session.setKeyValue(Session.ROLE_CODE, roles.optString("roles_id"));
            session.setBooleanKey(Session.LOGGED_IN, true);

            redirect();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // {"name":"testing user","email":"testing user","updated_at":"2018-04-14 05:56:31","created_at":"2018-04-14 05:56:31","id":9}

    }

    private void redirect(){
//        Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
        Intent intent = new Intent(RegisterActivity.this, MoreInfoActivity.class);
        startActivity(intent);
        finish();
    }

    private boolean validate_form(){

        String password_1 = password.getText().toString().trim();
        String password_2 = confirm_password.getText().toString().trim();
        String emails = email.getText().toString().trim();
        String user = username.getText().toString().trim();

        if(user.trim().isEmpty()){
            display_status("Username is required");
            return false;
        }

        if(emails.trim().isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(emails).matches()){
            display_status("Wrong email format");
            return false;
        }

        if(password_1.trim().isEmpty() || password_2.trim().isEmpty()){
            display_status("Password is required");
            return false;
        }

        if(!password_1.trim().equals(password_2.trim())){
            display_status("Password not match");
            return false;
        }

        try {
            form_data.put("username", user);
            form_data.put("email", emails);
            form_data.put("password", password_1);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return true;
    }

    private void display_status(String message){
        Snackbar snackbar = Snackbar
                .make(findViewById(R.id.register_layout), Html.fromHtml("<font color=\"#ffffff\">"+ message +"</font>"), Snackbar.LENGTH_LONG);
        View snackBarView = snackbar.getView();
        if(Build.VERSION.SDK_INT >= 23 )
            snackBarView.setBackgroundColor(getResources().getColor(R.color.mt_red, null));
        else
            snackBarView.setBackgroundColor(getResources().getColor(R.color.mt_red));
        snackbar.show();
    }

    public void to_login(View view) {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}
