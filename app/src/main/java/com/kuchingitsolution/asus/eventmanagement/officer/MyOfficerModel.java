package com.kuchingitsolution.asus.eventmanagement.officer;

import org.json.JSONObject;

public class MyOfficerModel {

    public String officer_id, officer_name, officer_email, officer_profile_img, request_id, status;

    MyOfficerModel (JSONObject info){

        JSONObject user_data = info.optJSONObject("user");

        this.officer_id = user_data.optString("id");
        this.officer_name = user_data.optString("name");
        this.officer_email = user_data.optString("email");
        this.request_id = info.optString("id");
        this.status = info.optString("status");
        this.officer_profile_img = "";
    }

    public String getOfficer_id(){
        return this.officer_id;
    }

    public String getOfficer_name(){
        return this.officer_name;
    }

    public String getOfficer_email(){
        return this.officer_email;
    }

    public String getRequest_id() { return this.request_id; }

    public String getStatus() { return this.status; }

    public String getOfficer_profile_img(){ return this.officer_profile_img; }
}
