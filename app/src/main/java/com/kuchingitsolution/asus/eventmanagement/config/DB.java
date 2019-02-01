package com.kuchingitsolution.asus.eventmanagement.config;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DB extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 5;

    // Database Name
    private static final String DATABASE_NAME ="event_management";

    // SHARED COLUMN NAMED
    private static final String ID = "id";
    private static final String TITLE = "title";
    private static final String CREATED_AT = "created_at";
    private static final String UPDATED_AT = "updated_at";

    // EVENT TABLE
    private static final String TABLE_EVENT = "events",
            USERNAME = "username",
            TYPE_NAME = "type_name",
            LOCATION_NAME = "location_name",
            OFFICER_NAME = "officer_name",
            LINK = "link", DESC = "description",
            SUGGESTION = "suggestion", ADDRESS = "address",
            COMPANY_NAME = "company_name",LATITUDE = "lat",
            LONGITUDE = "lon", START_TIME = "start_time",
            END_TIME = "end_time", USER_ID = "user_id",
            TYPE_ID = "type_id", LOCATION_ID = "location_id",
            STATUS_ID = "status_id", OFFICER_ID = "officer_id", EVENT_TYPE = "event_type",
            BOOKMARK = "bookmark", SUPPORT = "support", SUPPORT_THIS = "support_this";

    // CHAT TABLE
    private static final String TABLE_CHAT = "chats", AVATAR_LINK = "avatar_link",
            MESSAGE = "message", CHAT_ROOM_ID = "chat_room_id";


    // COUNTRY MODEL
    private static final String TABLE_COUNTRY = "countries", COUNTRY_ID = "country_id", COUNTRY_NAME = "country_name";
    private static final String TABLE_STATE = "states", STATE_ID = "state_id", STATE_NAME = "state_name";

    // INFO CATEGORY
    private static final String TABLE_INFO_CATEGORY = "info_category", CATEGORY_ID = "id", CATEGORY_NAME = "name";

    private static final String TABLE_EVENT_IMAGE = "event_image", EVENT_IMAGE_ID = "event_image_id", EVENT_ID = "event_id";

    /*
    * this.avatar_link = avatar_link;
        this.username = username;
        this.message = message;
        this.timestamp = timestamp;
        this.message_id = message_id;
        this.user_id = user_id;
        */

    private Context context;

    public DB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        create_table(sqLiteDatabase);
    }

    private void create_table(SQLiteDatabase sqLiteDatabase){
        String CREATE_EVENT_TABLE = "CREATE TABLE " + TABLE_EVENT + "("
                + ID + " VARCHAR PRIMARY KEY,"
                + USER_ID + " INTEGER,"
                + USERNAME + " VARCHAR,"
                + TYPE_ID + " INTEGER,"
                + TYPE_NAME + " VARCHAR,"
                + LOCATION_ID + " INTEGER,"
                + LOCATION_NAME + " VARCHAR,"
                + LONGITUDE + " REAL,"
                + LATITUDE + " REAL,"
                + ADDRESS + " VARCHAR,"
                + STATUS_ID + " INTEGER,"
                + OFFICER_ID + " INTEGER,"
                + OFFICER_NAME + " VARCHAR,"
                + TITLE + " VARCHAR,"
                + DESC + " VARCHAR,"
                + START_TIME + " VARCHAR,"
                + END_TIME + " VARCHAR,"
                + COMPANY_NAME + " VARCHAR,"
                + SUGGESTION + " VARCHAR,"
                + LINK + " VARCHAR,"
                + SUPPORT + " INTEGER,"
                + BOOKMARK + " INTEGER,"
                + SUPPORT_THIS + " INTEGER,"
                + EVENT_TYPE + " INTEGER,"
                + CREATED_AT + " VARCHAR,"
                + UPDATED_AT + " VARCHAR" + ")";

        String CREATE_CHAT_TABLE = "CREATE TABLE " + TABLE_CHAT + "("
                + ID + " INTEGER PRIMARY KEY,"
                + CHAT_ROOM_ID + " INTEGER,"
                + MESSAGE + " VARCHAR,"
                + USERNAME + " VARCHAR,"
                + USER_ID + " INTEGER,"
                + AVATAR_LINK + " VARCHAR,"
                + CREATED_AT + " VARCHAR" + ")";

        String CREATE_COUNTRY_TABLE = "CREATE TABLE " + TABLE_COUNTRY + "("
                + COUNTRY_ID + " INTEGER PRIMARY KEY,"
                + COUNTRY_NAME + " VARCHAR)";

        String CREATE_STATE_TABLE = "CREATE TABLE " + TABLE_STATE + "("
                + STATE_ID + " INTEGER PRIMARY KEY,"
                + STATE_NAME + " VARCHAR,"
                + COUNTRY_ID + " INTEGER)";

        String CREATE_INFO_CATEGORY_TABLE = "CREATE TABLE " + TABLE_INFO_CATEGORY + "("
                + CATEGORY_ID + " INTEGER PRIMARY KEY,"
                + CATEGORY_NAME + " VARCHAR )";

        String CREATE_EVENT_IMAGE_TABLE = "CREATE TABLE " + TABLE_EVENT_IMAGE + "("
                + EVENT_IMAGE_ID + " INTEGER PRIMARY KEY,"
                + EVENT_ID + " INTEGER,"
                + LINK + " VARCHAR )";

        sqLiteDatabase.execSQL(CREATE_EVENT_TABLE);
        sqLiteDatabase.execSQL(CREATE_CHAT_TABLE);
        sqLiteDatabase.execSQL(CREATE_COUNTRY_TABLE);
        sqLiteDatabase.execSQL(CREATE_STATE_TABLE);
        sqLiteDatabase.execSQL(CREATE_INFO_CATEGORY_TABLE);
        sqLiteDatabase.execSQL(CREATE_EVENT_IMAGE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENT);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_CHAT);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_COUNTRY);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_STATE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_INFO_CATEGORY);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENT_IMAGE);

        create_table(sqLiteDatabase);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }

    public void clearTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_EVENT, null, null);
        db.delete(TABLE_CHAT, null, null);
        db.delete(TABLE_COUNTRY, null, null);
        db.delete(TABLE_STATE, null, null);
        db.delete(TABLE_INFO_CATEGORY, null, null);
        db.delete(TABLE_EVENT_IMAGE, null, null);

        db.close();
    }

    public void insertEventImage(JSONArray medias){

        int size = medias.length();
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        for (int i = 0; i < size ; i ++){

            try {
                JSONObject media = medias.getJSONObject(i);
                ContentValues item = new ContentValues();
                item.put(EVENT_IMAGE_ID, media.optString("id"));
                item.put(EVENT_ID, media.optString("event_id"));
                item.put(LINK, media.optString("link"));

                if(ifrecordexists(TABLE_EVENT_IMAGE, EVENT_IMAGE_ID, media.optString("id"))){
                    db.update(TABLE_EVENT_IMAGE, item, " id = '" + media.optString("id") + "'", null);
                } else {
                    db.insert(TABLE_EVENT_IMAGE, null, item);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    public String getEventImage(String event_id){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<JSONObject> complaintData = new ArrayList<>();

        String SELECT_COMPLAINTS = String.format("SELECT * FROM %S", TABLE_EVENT_IMAGE);

        Log.d("query", SELECT_COMPLAINTS);

        Cursor cursor = db.rawQuery(SELECT_COMPLAINTS, null);

        if(cursor.moveToFirst()) {

            Log.d("Result events", DatabaseUtils.dumpCursorToString(cursor));

            do {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put(EVENT_IMAGE_ID, cursor.getString(0));
                    jsonObject.put(EVENT_ID, cursor.getString(1));
                    jsonObject.put(LINK, cursor.getString(2));
                    complaintData.add(cursor.getPosition(), jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());

        }

        Log.d("Overall result", String.valueOf(complaintData));

        cursor.close();
        db.close();

        return String.valueOf(complaintData);
    }

    public void removeEvent(String event_id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_EVENT, ID + " = " + event_id , null);
        db.close();
    }

    public void insertInfoCategory(JSONArray info_category){

        int size = info_category.length();
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        for (int i = 0; i < size ; i ++){

            try {
                JSONObject category = info_category.getJSONObject(i);
                ContentValues item = new ContentValues();
                item.put(CATEGORY_ID, category.optString("id"));
                item.put(CATEGORY_NAME, category.optString("category_name"));

                if(ifrecordexists(TABLE_INFO_CATEGORY, CATEGORY_ID, category.optString("id"))){
                    db.update(TABLE_INFO_CATEGORY, item, " id = '" + category.optString("id") + "'", null);
                } else {
                    db.insert(TABLE_INFO_CATEGORY, null, item);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    public void insertCountry(JSONArray countries){

        int size = countries.length();
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        for (int i = 0; i < size ; i ++){

            try {
                JSONObject country = countries.getJSONObject(i);
                ContentValues item = new ContentValues();
                item.put(COUNTRY_ID, country.optString("id"));
                item.put(COUNTRY_NAME, country.optString("name"));

                if(ifrecordexists(TABLE_COUNTRY, COUNTRY_ID, country.optString("id"))){
                    db.update(TABLE_COUNTRY, item, " country_id = '" + country.optString("id") + "'", null);
                } else {
                    db.insert(TABLE_COUNTRY, null, item);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();

    }

    public void insertState(JSONArray states){

        int size = states.length();
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        for (int i = 0; i < size ; i ++){

            try {
                JSONObject state = states.getJSONObject(i);
                ContentValues item = new ContentValues();
                item.put(STATE_ID, state.optString("id"));
                item.put(STATE_NAME, state.optString("name"));
                item.put(COUNTRY_ID, state.optString("country_id"));

                if(ifrecordexists(TABLE_STATE, STATE_ID, state.optString("id"))){
                    db.update(TABLE_STATE, item, " state_id = '" + state.optString("id") + "'", null);
                } else {
                    db.insert(TABLE_STATE, null, item);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();

    }

    public String getTableInfoCategory() {

        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<JSONObject> complaintData = new ArrayList<>();

        String SELECT_COMPLAINTS = String.format("SELECT * FROM %S", TABLE_INFO_CATEGORY);

        Log.d("query", SELECT_COMPLAINTS);

        Cursor cursor = db.rawQuery(SELECT_COMPLAINTS, null);

        if(cursor.moveToFirst()) {

            Log.d("Result events", DatabaseUtils.dumpCursorToString(cursor));

            do {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put(CATEGORY_ID, cursor.getString(0));
                    jsonObject.put(CATEGORY_NAME, cursor.getString(1));
                    complaintData.add(cursor.getPosition(), jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());

        }

        Log.d("Overall result", String.valueOf(complaintData));

        cursor.close();
        db.close();

        return String.valueOf(complaintData);
    }

    public String getCountries() {

        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<JSONObject> complaintData = new ArrayList<>();

        String SELECT_COMPLAINTS = String.format("SELECT * FROM %S", TABLE_COUNTRY);

        Log.d("query", SELECT_COMPLAINTS);

        Cursor cursor = db.rawQuery(SELECT_COMPLAINTS, null);

        if(cursor.moveToFirst()) {

            Log.d("Result events", DatabaseUtils.dumpCursorToString(cursor));

            do {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put(COUNTRY_NAME, cursor.getString(1));
                    complaintData.add(cursor.getPosition(), jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());

        }

        Log.d("Overall result", String.valueOf(complaintData));

        cursor.close();
        db.close();

        return String.valueOf(complaintData);
    }

    public String getStates(String country_id) {

        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<JSONObject> complaintData = new ArrayList<>();

        String SELECT_COMPLAINTS = String.format("SELECT * FROM %s WHERE %s = %s", TABLE_STATE, COUNTRY_ID, country_id);

        Log.d("query", SELECT_COMPLAINTS);

        Cursor cursor = db.rawQuery(SELECT_COMPLAINTS, null);

        if(cursor.moveToFirst()) {

            Log.d("Result events", DatabaseUtils.dumpCursorToString(cursor));

            do {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put(STATE_NAME, cursor.getString(1));
                    complaintData.add(cursor.getPosition(), jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());

        }

        Log.d("Overall result", String.valueOf(complaintData));

        cursor.close();
        db.close();

        return String.valueOf(complaintData);
    }

    public void insertChat(String avatar_link, String username, String message, String timestamp, String message_id, String user_id, String chat_room_id){

        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        ContentValues item = new ContentValues();

        item.put(ID, message_id);
        item.put(USER_ID, user_id);
        item.put(USERNAME, username);
        item.put(CHAT_ROOM_ID, chat_room_id);
        item.put(MESSAGE, message);
        item.put(AVATAR_LINK, avatar_link);
        item.put(CREATED_AT, timestamp);

        if(ifchatexists(message_id)){
            db.update(TABLE_CHAT, item, " id = '" + message_id + "'", null);
        } else {
            db.insert(TABLE_CHAT, null, item);
        }

        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();

    }

    public String get_latest_message(String chat_room_id){

        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<JSONObject> complaintData = new ArrayList<>();

        //SELECT * FROM table ORDER BY column DESC LIMIT 1
        String SELECT_LATEST_CHAT_ROW = String.format("SELECT * FROM %S WHERE %S = %S ORDER BY %S DESC LIMIT 1",
                TABLE_CHAT, CHAT_ROOM_ID, chat_room_id, ID);

        Log.d("query", SELECT_LATEST_CHAT_ROW);

        Cursor cursor = db.rawQuery(SELECT_LATEST_CHAT_ROW, null);

        if(cursor.moveToFirst()){
            Log.d("Result MESSAGE", DatabaseUtils.dumpCursorToString(cursor));
            do {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put(CHAT_ROOM_ID, cursor.getString(1));
                    jsonObject.put(MESSAGE, cursor.getString(2));
                    jsonObject.put(CREATED_AT, cursor.getString(6));
                    complaintData.add(cursor.getPosition(), jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());
        } else {
            Log.d("Result == ", "NO");
        }

        Log.d("Overall result", String.valueOf(complaintData));

        cursor.close();
        db.close();
        return String.valueOf(complaintData);

    }

    public boolean insertComplaint(JSONObject form_data,JSONArray responses){

        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        String id = "0";
        long ids;
        boolean add_to_stack = false;

        ContentValues item = new ContentValues();
        try {

            JSONObject locations = form_data.optJSONObject("locations");
            JSONObject event_owner = form_data.optJSONObject("user");
            JSONArray media = form_data.optJSONArray("media");
            JSONObject img_cover = media.optJSONObject(0);
            JSONObject response = responses.optJSONObject(0);

            id = form_data.getString("id");
            item.put(ID, form_data.optString("id"));
            item.put(USER_ID, event_owner.optInt("id"));
            item.put(USERNAME, event_owner.optString("name"));
            item.put(TYPE_ID,form_data.optInt("category_id"));
            item.put(TYPE_NAME, form_data.optString("category_name"));
            item.put(LOCATION_ID, form_data.optInt("location_id"));
            item.put(LOCATION_NAME, locations.optString("name"));
            item.put(LATITUDE, locations.optString("lat"));
            item.put(LONGITUDE, locations.optString("lon"));
            item.put(ADDRESS, locations.optString("address"));
            item.put(STATUS_ID, form_data.optInt("status_id"));
            item.put(OFFICER_ID, form_data.optInt("officer_id"));
            item.put(OFFICER_NAME, form_data.optString("officer_name", "NULL"));
            item.put(TITLE, form_data.optString("title"));
            item.put(DESC, form_data.optString("description"));
            item.put(SUGGESTION, form_data.optString("extra_info", null));
            item.put(LINK, img_cover.optString("link"));
            item.put(SUPPORT, form_data.optInt("support"));
            item.put(BOOKMARK, form_data.optInt("bookmarked"));
            item.put(CREATED_AT, form_data.optString("created_at"));
            item.put(COMPANY_NAME, event_owner.optString("name"));
            item.put(START_TIME, form_data.optString("start_time"));
            item.put(END_TIME, form_data.optString("end_time"));
            item.put(EVENT_TYPE, form_data.optString("type_id"));

            if(response != null){
                item.put(SUPPORT_THIS, response.optInt("support"));
                item.put(BOOKMARK, response.optInt("bookmark"));
            } else {
                item.put(SUPPORT_THIS, 0);
                item.put(BOOKMARK, 0);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(ifExists(id)) {
            ids = db.update(TABLE_EVENT, item, " id = '" + id + "'", null);
            add_to_stack = false;
        } else {
            ids = db.insert(TABLE_EVENT, null, item);
            add_to_stack = true;
        }

        Log.d("page_insert", " ---------- " + ids + " --------- ");
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();

        return add_to_stack;
    }

    public String getEvents(int page) {

        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<JSONObject> complaintData = new ArrayList<>();

        int start = (page - 1 ) * 2;

        String SELECT_COMPLAINTS = String.format("SELECT * FROM %S LIMIT %s,2 ", TABLE_EVENT, start);

        Log.d("query", SELECT_COMPLAINTS);

        Cursor cursor = db.rawQuery(SELECT_COMPLAINTS, null);

        if(cursor.moveToFirst()) {

            Log.d("Result events", DatabaseUtils.dumpCursorToString(cursor));

            do {
                JSONObject jsonObject = new JSONObject();
                try {

                    jsonObject.put(ID, cursor.getString(0));
                    jsonObject.put(USER_ID, cursor.getString(1));
                    jsonObject.put(USERNAME, cursor.getString(2));
                    jsonObject.put(TYPE_ID, cursor.getString(3));
                    jsonObject.put(TYPE_NAME, cursor.getString(4));
                    jsonObject.put(LOCATION_ID, cursor.getString(5));
                    jsonObject.put(LOCATION_NAME, cursor.getString(6));
                    jsonObject.put(LONGITUDE, cursor.getString(7));
                    jsonObject.put(LATITUDE, cursor.getString(8));
                    jsonObject.put(ADDRESS, cursor.getString(9));
                    jsonObject.put(STATUS_ID, cursor.getString(10));
                    jsonObject.put(OFFICER_ID, cursor.getString(11));
                    jsonObject.put(OFFICER_NAME, cursor.getString(12));
                    jsonObject.put(TITLE, cursor.getString(13));
                    jsonObject.put(DESC, cursor.getString(14));
                    jsonObject.put(START_TIME, cursor.getString(15));
                    jsonObject.put(END_TIME, cursor.getString(16));
                    jsonObject.put(COMPANY_NAME, cursor.getString(17));
                    jsonObject.put(SUGGESTION, cursor.getString(18));
                    jsonObject.put(LINK, cursor.getString(19));
                    jsonObject.put(SUPPORT, cursor.getString(20));
                    jsonObject.put(BOOKMARK, cursor.getString(21));
                    jsonObject.put(SUPPORT_THIS, cursor.getString(22));
                    jsonObject.put(EVENT_TYPE, cursor.getString(23));
                    jsonObject.put(CREATED_AT, cursor.getString(24));
                    jsonObject.put(UPDATED_AT, cursor.getString(25));

                    complaintData.add(cursor.getPosition(), jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());

        }

        Log.d("Overall result", String.valueOf(complaintData));

        cursor.close();
        db.close();

        return String.valueOf(complaintData);
    }

    public String getCurrenctEvents(int id) {

        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<JSONObject> complaintData = new ArrayList<>();

        String SELECT_COMPLAINTS = String.format("SELECT * FROM %S WHERE %s = %s ", TABLE_EVENT, ID, String.valueOf(id));

        Log.d("query", SELECT_COMPLAINTS);

        Cursor cursor = db.rawQuery(SELECT_COMPLAINTS, null);

        if(cursor.moveToFirst()) {

            Log.d("Result events", DatabaseUtils.dumpCursorToString(cursor));

            do {
                JSONObject jsonObject = new JSONObject();
                try {

                    jsonObject.put(ID, cursor.getString(0));
                    jsonObject.put(USER_ID, cursor.getString(1));
                    jsonObject.put(USERNAME, cursor.getString(2));
                    jsonObject.put(TYPE_ID, cursor.getString(3));
                    jsonObject.put(TYPE_NAME, cursor.getString(4));
                    jsonObject.put(LOCATION_ID, cursor.getString(5));
                    jsonObject.put(LOCATION_NAME, cursor.getString(6));
                    jsonObject.put(LONGITUDE, cursor.getString(7));
                    jsonObject.put(LATITUDE, cursor.getString(8));
                    jsonObject.put(ADDRESS, cursor.getString(9));
                    jsonObject.put(STATUS_ID, cursor.getString(10));
                    jsonObject.put(OFFICER_ID, cursor.getString(11));
                    jsonObject.put(OFFICER_NAME, cursor.getString(12));
                    jsonObject.put(TITLE, cursor.getString(13));
                    jsonObject.put(DESC, cursor.getString(14));
                    jsonObject.put(START_TIME, cursor.getString(15));
                    jsonObject.put(END_TIME, cursor.getString(16));
                    jsonObject.put(COMPANY_NAME, cursor.getString(17));
                    jsonObject.put(SUGGESTION, cursor.getString(18));
                    jsonObject.put(LINK, cursor.getString(19));
                    jsonObject.put(SUPPORT, cursor.getString(20));
                    jsonObject.put(BOOKMARK, cursor.getString(21));
                    jsonObject.put(SUPPORT_THIS, cursor.getString(22));
                    jsonObject.put(EVENT_TYPE, cursor.getString(23));
                    jsonObject.put(CREATED_AT, cursor.getString(24));
                    jsonObject.put(UPDATED_AT, cursor.getString(25));

                    complaintData.add(cursor.getPosition(), jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());

        }

        Log.d("Overall result", String.valueOf(complaintData));

        cursor.close();
        db.close();

        return String.valueOf(complaintData);
    }

    public String get_event_changes(String id){

        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<JSONObject> complaintData = new ArrayList<>();

        String SELECT_COMPLAINTS = String.format("SELECT * FROM %S WHERE %S = %S",
                TABLE_EVENT, ID, id);

        Log.d("query", SELECT_COMPLAINTS);

        Cursor cursor = db.rawQuery(SELECT_COMPLAINTS, null);

        if(cursor.moveToFirst()){
            Log.d("Result complaint", DatabaseUtils.dumpCursorToString(cursor));
            do {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put(SUPPORT, cursor.getString(20));
                    jsonObject.put(BOOKMARK, cursor.getString(21));
                    jsonObject.put(SUPPORT_THIS, cursor.getString(22));
                    complaintData.add(cursor.getPosition(), jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());
        } else Log.d("Result == ", "NO");

        Log.d("Overall result", String.valueOf(complaintData));

        cursor.close();
        db.close();
        return String.valueOf(complaintData);
    }

    public boolean update_response(String id, int status_like, int total_like){
        SQLiteDatabase db = this.getReadableDatabase();
        long ids = 0;

        ContentValues item = new ContentValues();
        item.put(SUPPORT, total_like);
        item.put(SUPPORT_THIS, status_like);
        ids = db.update(TABLE_EVENT, item, " id =" + id , null);
        Log.d("tag", " " + id);
        db.close();

        return ids > 0;
    }

    private boolean ifExists(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        String checkQuery = "SELECT " + ID + " FROM " + TABLE_EVENT + " WHERE " + ID + "= '"+ id + "'";
        cursor= db.rawQuery(checkQuery,null);
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }

    private boolean ifchatexists(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        String checkQuery = "SELECT " + ID + " FROM " + TABLE_CHAT + " WHERE " + ID + "= '"+ id + "'";
        cursor= db.rawQuery(checkQuery,null);
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }

    private boolean ifrecordexists(String table_name, String field, String value){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        String checkQuery = "SELECT " + field + " FROM " + table_name + " WHERE " + field + "= '"+ value + "'";
        cursor= db.rawQuery(checkQuery,null);
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }
}
