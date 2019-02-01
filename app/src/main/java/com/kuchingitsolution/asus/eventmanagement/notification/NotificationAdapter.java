package com.kuchingitsolution.asus.eventmanagement.notification;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.kuchingitsolution.asus.eventmanagement.R;
import com.kuchingitsolution.asus.eventmanagement.config.CommonApiAsync;
import com.kuchingitsolution.asus.eventmanagement.config.Config;
import com.kuchingitsolution.asus.eventmanagement.config.Helper;
import com.kuchingitsolution.asus.eventmanagement.config.Session;
import com.kuchingitsolution.asus.eventmanagement.event_details.DetailEventsActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class NotificationAdapter  extends RecyclerView.Adapter<NotificationAdapter.MyViewHolder>{

    private ArrayList<NotificationModel> notificationModelList = new ArrayList<>();
    private Context context;
    private String url;
    private JSONObject form_data= new JSONObject();
    private Session session;

    public NotificationAdapter(Context context, ArrayList<NotificationModel> notificationModelList){
        this.context = context;
        this.notificationModelList = notificationModelList;
        session = new Session(context);
        compile_form_data("user_id", session.getKeyValue(Session.USER_ID));
    }

    @Override
    public NotificationAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(NotificationAdapter.MyViewHolder holder, int position) {

        final NotificationModel notificationModel = notificationModelList.get(position);

        holder.content.setText(notificationModel.getContent());
        holder.timestamp.setText(Helper.get_time_range(notificationModel.getTimestamp()));
        compile_form_data("notification_id", notificationModel.getNoti_id());

        if(notificationModel.getAction_type().equals("event_comment")
                || notificationModel.getAction_type().equals("assign_event_officer")
                || notificationModel.getAction_type().equals("feedback_notify")
                || notificationModel.getAction_type().equals("join_event")){

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, DetailEventsActivity.class);
                    intent.putExtra("event_id", notificationModel.getEvent_id());
                    context.startActivity(intent);
                }
            });

        } else if(notificationModel.getAction_type().equals("invite_existing_officer")){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    compile_form_data("type","invite_existing_officer");
                    showMessage("Are you accept this invitation as Officer ? ", "invite_existing_officer");
                }
            });
        } else if(notificationModel.getAction_type().equals("assign_agency_user")){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    compile_form_data("type","assign_agency_user");
                    showMessage("Are you accept this invitation as Agency ?", "assign_agency_user");
                }
            });
        } else if(notificationModel.getAction_type().equals("invite_existing_vip")){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    compile_form_data("event_id", notificationModel.getEvent_id());
                    compile_form_data("type", "invite_existing_vip");
                    showMessage("Are you sure want to accept this invitation as Event's VIP ?", "invite_existing_vip");
                }
            });
        } else if(notificationModel.getAction_type().equals("deleted")){

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context, "Unable to access because this event had been deleted", Toast.LENGTH_SHORT).show();
                }
            });

        }

    }

    private void compile_form_data(String key, String value){

        try {
            form_data.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void showMessage(String message, String type){
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                compile_form_data("answer", "accept");
                confirm_or_reject_request();
                dialog.dismiss();
            }
        });
        alertDialogBuilder.setNegativeButton("Reject", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                compile_form_data("answer", "reject");
                confirm_or_reject_request();
                dialog.dismiss();
            }
        });
        alertDialogBuilder.show();
    }

    private void confirm_or_reject_request(){
        new CommonApiAsync(context, form_data).execute(Config.CONFIRM_REJECT_REQUEST);
    }

    @Override
    public int getItemCount() {
        return notificationModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView content, timestamp;

        public MyViewHolder(View itemView) {
            super(itemView);
            content = itemView.findViewById(R.id.content);
            timestamp = itemView.findViewById(R.id.time);
        }
    }

}
