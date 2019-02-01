package com.kuchingitsolution.asus.eventmanagement.feed;

public class CategoryModel {

    private int id, hosted_event;
    private String category_name;

    public CategoryModel(int id, String category_name, int hosted_event){
        this.id = id;
        this.category_name = category_name;
        this.hosted_event = hosted_event;
    }

    public CategoryModel(int id, String category_name){
        this.id = id;
        this.category_name = category_name;
    }

    public String getCategory_name(){ return this.category_name;}
    public int getId(){ return this.id; }
    public int getHosted_event(){ return this.hosted_event; }
}
