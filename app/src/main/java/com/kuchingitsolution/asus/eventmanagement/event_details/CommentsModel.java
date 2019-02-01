package com.kuchingitsolution.asus.eventmanagement.event_details;

import org.json.JSONObject;

public class CommentsModel {

    private String username, commentmsg, timeago;
    private int userID, msgID;

    CommentsModel(JSONObject form_data){
        JSONObject user = form_data.optJSONObject("comment_belong_to");
        this.userID = user.optInt("user_id");
        this.username = user.optString("name");
        this.msgID = form_data.optInt("id");
        this.commentmsg = form_data.optString("message");
        this.timeago = form_data.optString("created_at");
    }

    CommentsModel(JSONObject form_data, String own_username, String own_userid){
        this.userID = Integer.parseInt(own_userid);
        this.username = own_username;
        this.msgID = form_data.optInt("id");
        this.commentmsg = form_data.optString("message");
        this.timeago = form_data.optString("created_at");
    }

    public void setUsername(String username){ this.username = username; }

    public void setTimeago(String timeago) { this.timeago = timeago; }

    public void setCommentmsg(String message){ this.commentmsg = message; }

    public void setUserID(int userID){ this.userID = userID; }

    public void setMsgID(int msgID){ this.msgID = msgID; }

    public String getUsername(){ return username; }

    public String getCommentmsg(){ return commentmsg; }

    public String getTimeago() { return timeago; }

    public int getUserID(){ return userID; }

    public int getMsgID(){ return msgID; }
}
