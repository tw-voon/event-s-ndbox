package com.kuchingitsolution.asus.eventmanagement.message;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.kuchingitsolution.asus.eventmanagement.R;
import com.kuchingitsolution.asus.eventmanagement.auth.LoginActivity;
import com.kuchingitsolution.asus.eventmanagement.config.Config;
import com.kuchingitsolution.asus.eventmanagement.config.Session;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MessageActivity extends AppCompatActivity {

    private ArrayList<ChatListModel> chatListModelList = new ArrayList<>();
    private RecyclerView userListView;
    private ChatListAdapter chatListAdapter;
    private ImageView no_content;
    private Toolbar toolbar;
    private AVLoadingIndicatorView avi;
    private String Id;
    private int position;
    Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("My Message");
        }

        session = new Session(this);

        if(!session.getStatus(Session.LOGGED_IN)){
            Intent intent = new Intent(MessageActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }

        userListView = findViewById(R.id.message_recycler_view);
        chatListAdapter = new ChatListAdapter(this, chatListModelList);
        userListView.setAdapter(chatListAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        userListView.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(userListView.getContext(),
                linearLayoutManager.getOrientation());
        userListView.addItemDecoration(dividerItemDecoration);
        no_content = findViewById(R.id.no_result);
        avi = findViewById(R.id.avi);
//        get_chat_room();

    }

    private void get_chat_room(){
        no_content.setVisibility(View.GONE);
        new MessageListAsync(this).execute(Config.GET_CHAT_ROOM);
    }

    public void process_chat_room(String result){

        if(result == null){
            no_content.setImageResource(R.drawable.generic_error);
            no_content.setVisibility(View.VISIBLE);
            avi.hide();
            return;
        }

//        showMessage(result);

        if(result.equals("empty")){
            no_content.setVisibility(View.VISIBLE);
        } else {

            if(chatListModelList.size() > 0){
                chatListModelList.clear();
                chatListAdapter.notifyDataSetChanged();
            }

            try {

                JSONArray jsonArray = new JSONArray(result);
                int length = jsonArray.length();

                for (int i = 0; i < length ; i++){
                    JSONObject data = jsonArray.getJSONObject(i);
                    JSONObject chatroom = data.getJSONObject("chatroom");
                    JSONObject last = chatroom.optJSONObject("last_message");
                    ChatListModel chatListModel;

                    if(last == null) {
                        chatListModel = new ChatListModel(
                                data.optString("room_name"), chatroom.optString("created_at"), " ",
                                "No message yet", data.getString("updated_at"), data.getString("room_id"));
                    } else {
                        chatListModel = new ChatListModel(
                                data.optString("room_name"), last.optString("created_at"), " ",
                                last.getString("message"), data.getString("updated_at"), data.getString("room_id"));
                    }
//                    db_offline.insertRoom(chatListModel);
                    chatListModelList.add(chatListModel);
                }
                chatListAdapter.notifyDataSetChanged();
//                load_view(db_offline.get_room());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        avi.hide();
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

    private BroadcastReceiver listener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent ) {
            String type = intent.getStringExtra("type");
            Log.d( "Received data : ", "data here : " + type);
            get_chat_room();
            //showMessage(data);
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        get_chat_room();
        LocalBroadcastManager.getInstance(this).registerReceiver(listener,
                new IntentFilter(Config.BROADCAST_MSG));
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(listener);
        super.onPause();
    }
}
