package com.kuchingitsolution.asus.eventmanagement.config;

import android.content.Context;
import android.content.SharedPreferences;

import com.kuchingitsolution.asus.eventmanagement.marketplace.model.Item;

public class Session {

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private Context ctx;

    public static final String LOGGED_IN = "is_login";
    public static final String EMAIL = "email", USERNAME = "username", USER_ID = "user_id",
            ROLE_CODE = "role_code", LANGUAGE = "selected_language", COMPLETED_PROFILE = "complete_profile",
            MOBILE_NO = "mobile_number", DOB = "dob", ADDR_LINE_1 = "Addr_line_1", ADDR_LINE_2 = "Addr_line_2",
            POSTCODE = "postcode", CITY = "city", STATE = "state", COUNTRY = "country", GENDER = "gender",
            COUNTRY_ID = "country_id", STATE_ID = "state_id";

    private static final String KEY_ITEM_ID = "keyitemid";
    private static final String KEY_ITEM_NAME = "keyitemname";
    private static final String KEY_ITEM_DESCRIPTION = "keyitemdescription";
    private static final String KEY_ITEM_IMAGEURL = "keyitemimageurl";
    private static final String KEY_ITEM_CATEGORY ="keyitemcategory";

    public Session(Context ctx){
        this.ctx = ctx;
        prefs = ctx.getSharedPreferences(Config.PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void setKeyValue(String keyValue, String value){
        editor.putString(keyValue,value);
        editor.commit();
    }

    public void setIdValue(String keyValue, int value){
        editor.putInt(keyValue,value);
        editor.commit();
    }

    public void setBooleanKey(String keyValue, boolean status){
        editor.putBoolean(keyValue, status);
        editor.commit();
    }

    public void clearPreference() { prefs.edit().clear().apply(); }

    public String getKeyValue(String keyValue){
        return prefs.getString(keyValue, "0");
    }

    public int getIdValue(String keyValue){ return prefs.getInt(keyValue, 0); }

    public boolean getStatus(String key){return prefs.getBoolean(key, false);}

    public String getLanguage(){
        return prefs.getString(LANGUAGE, "en");
    }

    public void putMessageBadge(int number){
        editor.putInt(Config.MESSAGE, getMessageBadge() + number);
        editor.commit();
    }

    public void ResetMessageBadge(){
        editor.putInt(Config.MESSAGE, 0);
        editor.commit();
    }

    public int getMessageBadge(){
        return prefs.getInt(Config.MESSAGE, 0);
    }

    public Item getItem(){
        return new Item(
                prefs.getInt(KEY_ITEM_ID, 0),
                prefs.getString(KEY_ITEM_NAME, null),
                prefs.getString(KEY_ITEM_DESCRIPTION, null),
                prefs.getString(KEY_ITEM_IMAGEURL, null),
                prefs.getString(KEY_ITEM_CATEGORY, null)
        );
    }

}
