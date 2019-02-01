package com.kuchingitsolution.asus.eventmanagement.notification;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.kuchingitsolution.asus.eventmanagement.config.Config;
import com.kuchingitsolution.asus.eventmanagement.config.Session;
import com.onesignal.OSNotification;
import com.onesignal.OneSignal;

import org.json.JSONObject;

public class NotificationReceivedHandler implements OneSignal.NotificationReceivedHandler {

    private Context context;
    private LocalBroadcastManager localBroadcastManager;
    private Session session;

    public NotificationReceivedHandler(Context context){
        this.context = context;
        session = new Session(context);
    }

    @Override
    public void notificationReceived(OSNotification notification) {

        JSONObject data = notification.payload.additionalData;
        String customKey;
        Log.v("OneSignalExample", "customkey set with values: " + data.toString());

        if(data.length() > 0) {
            Log.d("OneSignalData", "testing");
            switch (data.optString("action_type")) {
                case "message":
                    session.putMessageBadge(1);
                    badge_broadcast(data.toString());
                    break;
            }
        }

    }

    private void badge_broadcast(String data){
        // Create intent with action
        Intent localIntent = new Intent(Config.BROADCAST_MSG);
        localIntent.putExtra("type", "message");
        localIntent.putExtra("data", data);
        Log.d("OneSignalData", "Send broadcast");
        // Send local broadcast
        LocalBroadcastManager.getInstance(context.getApplicationContext()).sendBroadcast(localIntent);
//        localBroadcastManager.sendBroadcast(localIntent);
    }
}
