package com.kuchingitsolution.asus.eventmanagement.feedback_manager;

import org.json.JSONObject;

public class FeedbackModel {

    String feedback_user, feedback_time, feedback_content;
    Float rating;

    FeedbackModel(JSONObject form_data){

        JSONObject user = form_data.optJSONObject("user");

        this.feedback_user = user.optString("name");
        this.feedback_content = form_data.optString("message");
        this.feedback_time = form_data.optString("created_at");
        this.rating = Float.valueOf(form_data.optString("rating"));
    }

    public Float getRating() {
        return rating;
    }

    public String getFeedback_content() {
        return feedback_content;
    }

    public String getFeedback_time() {
        return feedback_time;
    }

    public String getFeedback_user() {
        return feedback_user;
    }
}
