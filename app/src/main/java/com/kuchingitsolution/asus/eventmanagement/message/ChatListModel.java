package com.kuchingitsolution.asus.eventmanagement.message;

public class ChatListModel {

    private String username, timestamp, avatar, last_msg, last_seen, room_id;
    private int unread_msg;

    public ChatListModel(String username, String timestamp, String avatar, String last_msg, String last_seen, String room_id){
        this.username = username;
        this.avatar = avatar;
        this.timestamp = timestamp;
        this.last_msg = last_msg;
        this.last_seen = last_seen;
        this.room_id = room_id;
    }

    public void setUnreadMsg(int unread_msg){ this.unread_msg = unread_msg; }

    public int getUnread_msg(){return unread_msg;}
    public String getUsername(){ return username; }
    public String getTimestamp(){ return timestamp; }
    public String getAvatar() { return avatar; }
    public String getLast_msg() { return last_msg; }
    public String getLast_seen(){ return last_seen; }
    public String getRoom_id() { return room_id; }

}
