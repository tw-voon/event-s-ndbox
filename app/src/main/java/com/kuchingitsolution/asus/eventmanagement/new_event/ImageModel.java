package com.kuchingitsolution.asus.eventmanagement.new_event;

public class ImageModel {

    private String image_uri, path, image_id;

//    ImageModel(String image_uri, String path){
//        this.image_uri = image_uri;
//        this.path = path;
//    }

    ImageModel(String image_uri, String path, String image_id){
        this.image_uri = image_uri;
        this.path = path;
        this.image_id = image_id;
    }

    public void setImage_uri(String uri){
        this.image_uri = uri;
    }
    public void setPath(String path){
        this.path = path;
    }

    public void setImage_id(String image_id) {
        this.image_id = image_id;
    }

    public String getImage_uri(){
        return this.image_uri;
    }
    public String getPath(){
        return this.path;
    }

    public String getImage_id() {
        return image_id;
    }
}
