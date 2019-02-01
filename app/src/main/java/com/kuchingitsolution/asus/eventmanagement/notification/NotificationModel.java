package com.kuchingitsolution.asus.eventmanagement.notification;

import org.json.JSONObject;

public class NotificationModel {

    private String event_id, specific_user_id, action_user, action_type, content, timestamp, noti_id;

    NotificationModel(JSONObject form_data){
        this.noti_id = form_data.optString("id");
        this.event_id = form_data.optString("event_id");
        this.specific_user_id = form_data.optString("specific_user_id");
        this.action_user = form_data.optString("action_user");
        this.action_type = form_data.optString("action_type");
        this.content = form_data.optString("content");
        this.timestamp = form_data.optString("created_at");
    }

    public String getEvent_id(){ return this.event_id; }
    public String getSpecific_user_id() { return this.specific_user_id; }
    public String getAction_user(){ return this.action_user; }
    public String getAction_type(){ return this.action_type; }
    public String getContent(){ return this.content; }
    public String getTimestamp(){ return this.timestamp; }
    public String getNoti_id(){ return this.noti_id; }

}
