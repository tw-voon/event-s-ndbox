package com.kuchingitsolution.asus.eventmanagement.new_event;

public class EventTypeModel {

    private String category_id,category_name;

    public EventTypeModel (String categoryName, String categoryID){
        this.category_id = categoryID;
        this.category_name = categoryName;
    }

    public String getCategory_id(){
        return this.category_id;
    }

    public String getCategory_name(){
        return this.category_name;
    }

}
