package com.kuchingitsolution.asus.eventmanagement.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.kuchingitsolution.asus.eventmanagement.R;
import com.kuchingitsolution.asus.eventmanagement.config.Config;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class NotificationFragment extends Fragment{

    private AVLoadingIndicatorView avi;
    private ImageView no_result;
    public String TYPE = "GET_NOTIFICATION";
    private ArrayList<NotificationModel> notificationModels = new ArrayList<>();
    private RecyclerView notification_list;
    private NotificationAdapter notificationAdapter;
    private LinearLayoutManager linearLayoutManager;
    private boolean is_visible = false, is_view_ready = false;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notification, container, false);
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        return super.onCreateAnimation(transit, enter, nextAnim);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(getView() != null){
            Log.d("loading view","notification start");
            setup_element(getView());
            get_notification();
        }
    }

    private void get_notification(){
        new NotificationAsync(getContext(), TYPE).execute(Config.GET_NOTIFICATION);
    }

    private BroadcastReceiver listener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent ) {
            String data = intent.getStringExtra("result");
            Log.d( "Received data : ", "data here : " + data);
//            showMessage(data);
            parse_result(data);
        }
    };

    private void parse_result(String s){

        Log.d("loading view","notification start " + s);

        if(s == null){
            avi.hide();
            no_result.setImageResource(R.drawable.generic_error);
            no_result.setVisibility(View.VISIBLE);
            return;
        }

        if(notificationModels.size() > 0){
            notificationModels.clear();
            notificationAdapter.notifyDataSetChanged();
        }

        try {
            JSONObject info = new JSONObject(s);
            JSONArray notifications = info.optJSONArray("notification");
            int length = notifications.length();

            if(length == 0){
                no_result.setImageResource(R.drawable.inbox_empty_state_icon);
                no_result.setVisibility(View.VISIBLE);
                avi.hide();
                return;
            }

            for(int i = 0; i < length ; i++){

                JSONObject notification = notifications.getJSONObject(i);
                NotificationModel notificationModel = new NotificationModel(notification);
                notificationModels.add(notificationModel);

            }

            notificationAdapter.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(notificationAdapter.getItemCount() > 0){
            no_result.setVisibility(View.GONE);
        }

        avi.hide();
    }

    private void showMessage(String message){
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialogBuilder.show();
    }

    private void setup_element(View view){

        avi = view.findViewById(R.id.avi);
        no_result = view.findViewById(R.id.no_result);

        avi.hide();
        no_result.setVisibility(View.GONE);

        notification_list = view.findViewById(R.id.notification_list);
        linearLayoutManager = new LinearLayoutManager(getContext());
        notificationAdapter = new NotificationAdapter(getContext(), notificationModels);
        notification_list.setAdapter(notificationAdapter);
        notification_list.setLayoutManager(linearLayoutManager);
        notification_list.setNestedScrollingEnabled(false);

        is_view_ready = true;

    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(listener,
                new IntentFilter(TYPE));
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(listener);
        super.onPause();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if(isVisibleToUser && is_view_ready){
            get_notification();
        }
    }
}
