package com.kuchingitsolution.asus.eventmanagement.message;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.kuchingitsolution.asus.eventmanagement.R;
import com.kuchingitsolution.asus.eventmanagement.auth.LoginActivity;
import com.kuchingitsolution.asus.eventmanagement.config.Config;
import com.kuchingitsolution.asus.eventmanagement.config.DB;
import com.kuchingitsolution.asus.eventmanagement.config.Session;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity implements OnUserClickCallback{

    ArrayList<ChatModel> chatModels = new ArrayList<>();
    private Toolbar toolbar;
    ChatAdapter chatAdapter;
    RecyclerView recyclerView;
    ImageView no_content;
    Button send;
    EditText message;
    Session session;
    private DB db;
    private JSONObject form_data = new JSONObject();
    public static String GET_FULL_MESSAGE = "GET_FULL_MESSAGE", SEND_MSG = "SEND_MESSAGE", TOUCH_CHAT_ROOM = "TOUCH_CHAT_ROOM";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        session = new Session(ChatActivity.this);
        db = new DB(this);

        if(!session.getStatus(Session.LOGGED_IN)){
            Intent intent = new Intent(ChatActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }

        recyclerView = findViewById(R.id.reyclerview_message_list);
        chatAdapter = new ChatAdapter(ChatActivity.this, chatModels, session.getKeyValue(Session.USER_ID));
        recyclerView.setAdapter(chatAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        no_content = findViewById(R.id.no_content);
        message = findViewById(R.id.edittext_chatbox);

        if(getIntent() != null) {

            if(getIntent().getStringExtra("target") != null){
                compile_form_data("selected_user_id", getIntent().getStringExtra("target"));
            }

            if(getIntent().getStringExtra("chat_room_id") != null){
                compile_form_data("chat_room_id", getIntent().getStringExtra("chat_room_id"));
            }

            if(getIntent().getStringExtra("name") != null){
                compile_form_data("room_name", getIntent().getStringExtra("name"));
            }

            compile_form_data("user_id", session.getKeyValue(Session.USER_ID));
        }

        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle(form_data.optString("room_name"));
            getSupportActionBar().show();
        }

        get_message();
    }

    private void compile_form_data(String key, String value){

        try {
            form_data.put(key,value);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private JSONObject get_form_data(){ return this.form_data; }

    private void get_message(){
        compile_form_data("type", GET_FULL_MESSAGE);
        no_content.setVisibility(View.GONE);
        new ChatAsync(this,get_form_data()).execute(Config.GET_CHAT_MESSAGE);
    }

    public void parse_message(String result){
//        showMessage(result);

        if(result == null){
            no_content.setImageResource(R.drawable.generic_error);
            no_content.setVisibility(View.VISIBLE);
            return;
        }

        try {
            JSONObject data = new JSONObject(result);
            String status = data.optString("status");

            if(status.equals("empty")){
                no_content.setImageResource(R.drawable.no_messages);
                no_content.setVisibility(View.VISIBLE);
                compile_form_data("chat_room_id", data.optString("chat_room_id"));
            } else {
                compile_form_data("chat_room_id", data.optString("chat_room_id"));
                JSONArray message = data.optJSONArray("data");

                if(message != null){
                    int length = message.length();

                    for (int i = 0; i < length ; i++){
                        JSONObject jsonObject = message.getJSONObject(i);
                        JSONObject user = jsonObject.getJSONObject("user");
                        ChatModel chatModel = new ChatModel(" ", user.getString("name"),
                                jsonObject.getString("message"), jsonObject.getString("created_at"),
                                jsonObject.getString("id"), user.getString("id"));
                        db.insertChat(" ", user.getString("name"),
                                jsonObject.getString("message"), jsonObject.getString("created_at"),
                                jsonObject.getString("id"), user.getString("id"),jsonObject.optString("chat_room_id"));
                        chatModels.add(chatModel);
                        Log.d("message_content", user.getString("name"));
                    }
                    chatAdapter.notifyDataSetChanged();
                    recyclerView.getLayoutManager().scrollToPosition(chatAdapter.getItemCount()-1);
                    no_content.setVisibility(View.GONE);
                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void showMessage(String message){
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialogBuilder.show();
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "message", Toast.LENGTH_SHORT).show();
            Log.d("onesignal_data", intent.getStringExtra("data"));
            append_received_msg(intent.getStringExtra("data"));
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(ChatActivity.this).registerReceiver(broadcastReceiver,
                new IntentFilter(Config.BROADCAST_MSG));
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(ChatActivity.this).unregisterReceiver(broadcastReceiver);
        super.onPause();
    }

    public void validate(View view) {

        String messages = message.getText().toString().trim();
        if(messages.length() == 0 || messages.isEmpty()) {
            Toast.makeText(ChatActivity.this, "No message. Please enter message", Toast.LENGTH_SHORT).show();
        } else {
            compile_form_data("message", messages);
            compile_form_data("type", SEND_MSG);
            send_message();
        }

    }

    private void send_message(){
        new ChatAsync(this, get_form_data()).execute(Config.POST_MESSAGE);
    }

    public void append_msg(String result){

//        showMessage(result);

        try {
            JSONObject data = new JSONObject(result);
            JSONObject info = data.getJSONObject("info");
            JSONObject messages = info.getJSONObject("message");
            JSONObject user = messages.getJSONObject("user");
            ChatModel chatModel = new ChatModel(" ", user.getString("name"),
                    messages.getString("message"), messages.getString("created_at"),
                    messages.getString("id"), user.getString("id"));
            chatModels.add(chatModel);
            chatAdapter.notifyDataSetChanged();
            recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, chatAdapter.getItemCount()-1);
            no_content.setVisibility(View.GONE);
            message.setText("");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void append_received_msg(String result){
        /*
         * {"room_id":"12","action_type":"message","category":"chat","message":{"updated_at":"2018-05-13 16:00:21",
         * "user_id":"12","chat_room_id":"12","created_at":"2018-05-13 16:00:21","id":24,"message":"response 11",
         * "user":{"updated_at":"2018-04-14 08:05:15","roles_id":"5","name":"test","created_at":"2018-04-14 08:05:15",
         * "id":12,"email":"t.wui1993@gmail.com","status":"1"},"room":{"updated_at":"2018-05-06 12:20:39","name":"Testing",
         * "created_at":"2018-05-06 12:20:39","id":12,"member_count":"2"}},"chat_room":[{"room_id":"12","room_name":"agency",
         * "updated_at":"2018-05-13 15:59:57","user_id":"12","chatroom":{"updated_at":"2018-05-06 12:20:39","name":"Testing",
         * "created_at":"2018-05-06 12:20:39","last_message":{"updated_at":"2018-05-13 16:00:21","user_id":"12","chat_room_id":"12",
         * "created_at":"2018-05-13 16:00:21","id":24,"message":"response 11"},"id":12,"member_count":"2"},"created_at":"2018-05-06 12:20:39","id":24}]}
         * */
        try {
            JSONObject data = new JSONObject(result);
            JSONObject message = data.optJSONObject("message");
            JSONObject user = message.optJSONObject("user");
            ChatModel chatModel = new ChatModel(" ", user.getString("name"),
                    message.getString("message"), message.getString("created_at"),
                    message.getString("id"), user.getString("id"));
            chatModels.add(chatModel);
            chatAdapter.notifyDataSetChanged();
            recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, chatAdapter.getItemCount()-1);
            no_content.setVisibility(View.GONE);
            touch_chat_room();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void touch_chat_room(){
        compile_form_data("type", TOUCH_CHAT_ROOM);
        new ChatAsync(this, get_form_data()).execute(Config.TOUCH_CHAT_ROOM);
    }

    @Override
    public void onDeleteMessage(String msg_id, int current_position) {

    }

}
