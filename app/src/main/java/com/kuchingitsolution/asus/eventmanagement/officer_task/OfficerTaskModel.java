package com.kuchingitsolution.asus.eventmanagement.officer_task;

import org.json.JSONArray;
import org.json.JSONObject;

public class OfficerTaskModel {

    String event_cover, title, start_time, end_time, event_id, location;
    int is_bookmarked, status;

    OfficerTaskModel(JSONObject form_data){

        JSONArray bookmark_media = form_data.optJSONArray("media");
        JSONObject media = bookmark_media.optJSONObject(0);
        JSONObject bookmark_location = form_data.optJSONObject("locations");

        this.event_cover = media.optString("link");
        this.title = form_data.optString("title");
        this.start_time = form_data.optString("start_time");
        this.end_time = form_data.optString("end_time");
        this.event_id = form_data.optString("id");
        this.location = bookmark_location.optString("name");
        this.is_bookmarked = form_data.optInt("bookmarked");
        this.status = form_data.optInt("status_id");

    }

    public String getEvent_cover(){ return this.event_cover; }
    public String getTitle(){ return this.title; }
    public String getStart_time(){ return this.start_time; }
    public String getEnd_time(){ return this.end_time; }
    public String getEvent_id(){ return this.event_id; }
    public String getLocation(){ return this.location; }
    public int getIs_bookmarked(){ return this.is_bookmarked; }
    public int getStatus(){ return this.status; }

}
