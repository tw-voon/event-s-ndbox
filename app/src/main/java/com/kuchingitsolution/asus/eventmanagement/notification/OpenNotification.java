package com.kuchingitsolution.asus.eventmanagement.notification;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.kuchingitsolution.asus.eventmanagement.event_details.DetailEventsActivity;
import com.onesignal.OSNotificationAction;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;

import org.json.JSONObject;

public class OpenNotification implements OneSignal.NotificationOpenedHandler {

    private Context context;

    public OpenNotification(Context context){
        this.context = context;
    }

    @Override
    public void notificationOpened(OSNotificationOpenResult result) {

        OSNotificationAction.ActionType actionType = result.action.type;
        JSONObject data = result.notification.payload.additionalData;
        Log.d("OneSignalData", data.toString() + " Payload");

        if(data.length() > 0){
            Log.d("OneSignalData", "testing");
            switch (data.optString("action_type")){
                case "event_comment":
                    Log.d("OneSignalData", "testing2");
                    to_event_page(data.optString("event_id"));
                    break;
                case "feedback_notify":
                    Log.d("OneSignalData", "testing2");
                    to_event_page(data.optString("event_id"));
                    break;
                case "join_event":
                    Log.d("OneSignalData", "testing2");
                    to_event_page(data.optString("event_id"));
                    break;
                case "assign_event_officer":
                    Log.d("OneSignalData", "testing2");
                    to_event_page(data.optString("event_id"));
                    break;
                case "new_event":
                    Log.d("OneSignalData", "testing2");
                    to_event_page(data.optString("event_id"));
                    break;
            }
        }
    }

    private void to_event_page(String event_id){
        Intent intent = new Intent(context, DetailEventsActivity.class);
        intent.putExtra("event_id", event_id);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
