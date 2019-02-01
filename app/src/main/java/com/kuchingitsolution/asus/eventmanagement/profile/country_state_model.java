package com.kuchingitsolution.asus.eventmanagement.profile;

public class country_state_model {

    private String name, id;

    public country_state_model(String name, String id){
        this.name = name;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
