package com.kuchingitsolution.asus.eventmanagement.officer;

public class AssignOfficerModel {

    private String user_id, username, link;

    AssignOfficerModel(String user_id, String username, String link){
        this.user_id = user_id;
        this.username = username;
        this.link = link;
    }

    public String getUser_id(){ return user_id; }
    public String getUsername(){ return username; }
    public String getLink() { return link; }

}
