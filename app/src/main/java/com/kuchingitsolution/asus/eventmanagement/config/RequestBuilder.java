package com.kuchingitsolution.asus.eventmanagement.config;

import okhttp3.MultipartBody;

public class RequestBuilder {

    public static MultipartBody getAllCategory(String user_id){
        return new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("user_id", user_id)
                .build();
    }

}
