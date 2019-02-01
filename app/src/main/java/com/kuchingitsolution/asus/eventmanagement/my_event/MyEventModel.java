package com.kuchingitsolution.asus.eventmanagement.my_event;

import org.json.JSONArray;
import org.json.JSONObject;

public class MyEventModel {

    private String id, username, type_name, location_name, status_name, officer_name, link, last_action, title, description, created_at, suggestion, company_name, lat, lon, start_time, end_time;
    private int user_id, type_id, location_id, status_id, officer_id, media_type, bookmark, support, support_this;

    public MyEventModel(JSONObject form_data){

        JSONObject locations = form_data.optJSONObject("locations");
        JSONObject event_owner = form_data.optJSONObject("user");
        JSONArray media = form_data.optJSONArray("media");
        JSONObject img_cover = media.optJSONObject(0);
        JSONObject response = form_data.optJSONObject("event_responses");

        this.id = form_data.optString("id");
        this.user_id = event_owner.optInt("id");
        this.username = event_owner.optString("name");
        this.type_id = form_data.optInt("category_id");
        this.type_name = form_data.optString("category_name");
        this.location_id = form_data.optInt("location_id");
        this.location_name = locations.optString("name");
        this.lat = locations.optString("lat");
        this.lon = locations.optString("lon");
        this.lon = locations.optString("address");
        this.status_id = form_data.optInt("status_id");
        this.officer_id = form_data.optInt("officer_id");
        this.officer_name = form_data.optString("officer_name", "NULL");
        this.title = form_data.optString("title");
        this.description = form_data.optString("description");
        this.suggestion = form_data.optString("extra_info", null);
        this.link = img_cover.optString("link");
        this.support = form_data.optInt("support");
        this.created_at = form_data.optString("created_at");
        this.company_name = event_owner.optString("name");
        this.support = form_data.optInt("support");
        this.start_time = form_data.optString("start_time");
        this.end_time = form_data.optString("end_time");

        if(response != null){
            this.support_this = response.optInt("support");
            this.bookmark = response.optInt("bookmark");
        } else {
            this.support_this = 0;
            this.bookmark = 0;
        }

    }

    public void setSupport_this(int status){ this.support_this = status; }
    public void setSupport(int total){ this.support = total; }
    public void setBookmark(int status) { this.bookmark = status; }

    public String getUsername(){
        return username;
    }
    public String getReportTitle(){
        return  title;
    }
    public String getNewsDescription(){
        return description;
    }
    public String getCreated_at() { return created_at; }
    public String getLink(){
        return link;
    }
    public String getId() { return id; }
    public String getLocation_name(){ return location_name;}
    public int getBookmarked() {return bookmark;}
    public int getSupported() {return support; }
    public int getUser_id(){ return  user_id; }
    public int getStatus_id(){return status_id;}
    public int getType_id(){return type_id;}
    public int getLocation_id(){return location_id;}
    public int getOfficer_id(){return officer_id;}
    public int getMedia_type(){return media_type;}
    public int getSupport(){return support;}
    public String getOfficer_name() { return officer_name; }
    public String getType_name(){return type_name;}
    public String getTitle(){ return title;}
    public String getDescription(){ return  description; }
    public String getCompany_name(){return company_name; }
    public String getSuggestion() { return suggestion; }
    public String getLat(){return lat;}
    public String getLon(){return lon;}
    public String getStart_time(){ return this.start_time; }
    public String getEnd_time(){ return this.end_time; }

    public void setOfficer(int id, String name){
        this.officer_id = id;
        this.officer_name = name;
    }

    public void support(){
        this.support = this.support + 1;
    }

    public void noSupport(){
        this.support = this.support - 1;
    }
}
