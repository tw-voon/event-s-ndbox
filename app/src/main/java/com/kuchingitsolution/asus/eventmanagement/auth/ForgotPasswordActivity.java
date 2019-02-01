package com.kuchingitsolution.asus.eventmanagement.auth;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.kuchingitsolution.asus.eventmanagement.HomeActivity;
import com.kuchingitsolution.asus.eventmanagement.R;
import com.kuchingitsolution.asus.eventmanagement.config.CommonApiAsync;
import com.kuchingitsolution.asus.eventmanagement.config.Config;

import org.json.JSONException;
import org.json.JSONObject;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText edtemail;
    private CircularProgressButton submit;
    JSONObject form_data = new JSONObject();
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        setup_element();
    }

    private void setup_element(){
        edtemail = findViewById(R.id.edtEmail);
        submit = findViewById(R.id.form_login);
        handler = new Handler();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submit.startAnimation();
                validate_form();
            }
        });
    }

    private void validate_form(){
        String email = edtemail.getText().toString().trim();

        if(email.isEmpty()){
            display_status("Email is required");
            handler.postDelayed(r, 1000);
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            display_status("Email format incorrect..");
            handler.postDelayed(r, 1000);
            return;
        }

        generate_form_data("email", email);
        generate_form_data("type", "forgot_password");
        send_reset_email();
    }

    private void send_reset_email(){
        new CommonApiAsync(ForgotPasswordActivity.this, form_data).execute(Config.FORGOT_PASSWORD);
    }

    public void parse_reset_response(String s){
        Log.d("Result from server", "Password reset: " + s);
        showMessage(s);
        if(s.equals("We can't find a user with that e-mail address.")){
            display_status("We can't find a user with that e-mail address.");
        } else if(s.equals("We have e-mailed your password reset link!")) {
            display_status("We have e-mailed your password reset link!");
            edtemail.setText("");
        }
        handler.postDelayed(r, 1000);
//        revert_();
    }

    private void showMessage(String message){
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ForgotPasswordActivity.this);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialogBuilder.show();
    }

    private void generate_form_data(String key, String value){

        try {
            form_data.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void revert_(){
        submit.doneLoadingAnimation(R.color.mt_red, BitmapFactory.decodeResource(getResources(), R.drawable.ic_close_white_24dp));
        submit.revertAnimation();
    }

    private void display_status(String message){
        Snackbar snackbar = Snackbar
                .make(findViewById(R.id.forgot_password_layout), Html.fromHtml("<font color=\"#ffffff\">"+ message +"</font>"), Snackbar.LENGTH_LONG);
        View snackBarView = snackbar.getView();
        if(Build.VERSION.SDK_INT >= 23 )
            snackBarView.setBackgroundColor(getResources().getColor(R.color.mt_red, null));
        else
            snackBarView.setBackgroundColor(getResources().getColor(R.color.mt_red));
        snackbar.show();
    }

    final Runnable r = new Runnable() {
        public void run() {
            revert_();
//            Intent intent = new Intent(ForgotPasswordActivity.this, HomeActivity.class);
//            startActivity(intent);
//            finish();
        }
    };

    public void back_to_login(View view) {
        onBackPressed();
    }
}
