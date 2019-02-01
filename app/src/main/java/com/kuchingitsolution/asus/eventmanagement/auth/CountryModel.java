package com.kuchingitsolution.asus.eventmanagement.auth;

public class CountryModel {

    private int country_id;
    private String country_name;

    CountryModel(int country_id, String country_name){

        this.country_id = country_id;
        this.country_name = country_name;

    }

    public int getCountry_id() {
        return country_id;
    }

    public String getCountry_name() {
        return country_name;
    }

}
