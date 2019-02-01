package com.kuchingitsolution.asus.eventmanagement.event_vip;

public class EventVipModel {

    private String username, invite_status, user_id, profile_img;

    EventVipModel(String username, String invite_status, String user_id, String profile_img){

        this.username = username;
        this.invite_status = invite_status;
        this.user_id = user_id;
        this.profile_img = profile_img;

    }

    public String getUsername(){ return this.username; }
    public String getInvite_status(){ return this.invite_status; }
    public String getUser_id(){ return this.user_id; }
    public String getProfile_img(){ return this.profile_img; }

}
