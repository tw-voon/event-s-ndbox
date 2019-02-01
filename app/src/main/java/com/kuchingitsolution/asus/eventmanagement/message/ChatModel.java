package com.kuchingitsolution.asus.eventmanagement.message;

public class ChatModel {

    private String avatar_link, username, message, timestamp, message_id, user_id;

    public ChatModel(String avatar_link, String username, String message, String timestamp, String message_id, String user_id){
        this.avatar_link = avatar_link;
        this.username = username;
        this.message = message;
        this.timestamp = timestamp;
        this.message_id = message_id;
        this.user_id = user_id;
    }

    public String getAvatar_link(){ return avatar_link; }
    public String getUsername(){ return username; }
    public String getMessage(){ return message; }
    public String getTimestamp() { return timestamp; }
    public String getMessage_id() { return message_id; }
    public String getUser_id() { return user_id; }
    
}
