package com.kuchingitsolution.asus.eventmanagement.marketplace.model;

public class Item {

    int id;
    String name;
    String description;
    String imageUrl;
    String category;

    public Item(int id, String name, String description, String imageUrl, String category) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.category = category;
    }

    public Item(String name, String description, String imageUrl, String category) {
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.category = category;
    }

    public int getId() {
        return id;
    }


    public String getName() {
        return name;
    }


    public String getDescription() {
        return description;
    }


    public String getImageUrl() {
        return imageUrl;
    }


    public String getCategory() {
        return category;
    }
}
