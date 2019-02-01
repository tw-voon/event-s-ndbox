package com.kuchingitsolution.asus.eventmanagement.event_attendees;

public class ParticipantModel {

    private String name;
    private int user_id;

    ParticipantModel(String name, int user_id){
        this.name = name;
        this.user_id = user_id;
    }

    public String getName(){ return this.name; }
    public int getUser_id(){ return this.user_id; }
}
