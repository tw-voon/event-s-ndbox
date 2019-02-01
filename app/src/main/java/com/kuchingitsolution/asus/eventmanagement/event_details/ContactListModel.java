package com.kuchingitsolution.asus.eventmanagement.event_details;

public class ContactListModel {

    private String contact_name, contact_position, contact_id;

    ContactListModel(String contact_id, String contact_position, String contact_name){
        this.contact_name = contact_name;
        this.contact_position = contact_position;
        this.contact_id = contact_id;
    }

    public String getContact_name(){ return this.contact_name; }
    public String getContact_position(){ return this.contact_position; }
    public String getContact_id(){ return this.contact_id; }
}
