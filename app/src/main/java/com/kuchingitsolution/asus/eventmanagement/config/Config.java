package com.kuchingitsolution.asus.eventmanagement.config;

import android.content.Context;
import android.content.Intent;

import com.kuchingitsolution.asus.eventmanagement.HomeActivity;

public class Config {

    public static String PREF_NAME = "event_management";
    /* PRODUCTION LINK */
//    private static String DOMAIN_URL = "http://eventmanagement.kuchingitsolution.net/admin/api/";
//    public  static String IMAGE_CATALOG = "http://eventmanagement.kuchingitsolution.net/admin/images/";

    /* TESTING LINK */
    private static String DOMAIN_URL = "http://eventsandbox.kuchingitsolution.net/admin/api/";
    public static String IMAGE_CATALOG = "http://eventsandbox.kuchingitsolution.net/admin/images/";
    public static String HOST_NAME = "eventsandbox.kuchingitsolution.net";
    public static String GET_ALLCATEGORY = DOMAIN_URL + "category";
    public static String GET_INFOCATEGORY = DOMAIN_URL + "info_category";
    public static String UPLOAD_NEW_EVENT = DOMAIN_URL + "new_event";
    public static String UPLOAD_NEW_INFO_EVENT = DOMAIN_URL + "new_information_event";
    public static String UPDATE_EVENT = DOMAIN_URL + "update_event";
    public static String APPS_LOGIN = DOMAIN_URL + "login";
    public static String EVENT_GETS_OWN_COMPLETED = DOMAIN_URL + "get_completed_events";
    public static String EVENT_GETS_OWN_ONGOING = DOMAIN_URL + "get_ongoing_events";
    public static String EVENT_GET_DETAILS = DOMAIN_URL + "get_current_events";
    public static String ADD_NEW_COMMENT = DOMAIN_URL + "new_comments";
    public static String ADD_NEW_FEEDBACK = DOMAIN_URL + "new_feedback";
    public static String LIKE_EVENT = DOMAIN_URL + "like_this_event";
    public static String BOOKMARK_EVENT = DOMAIN_URL + "bookmark_this_event";
    public static String GET_OFFICER_LIST = DOMAIN_URL + "list_officer";
    public static String GET_PENDING_OFFICER_LIST = DOMAIN_URL + "list_pending_officer";
    public static String GET_OFFICER = DOMAIN_URL + "search_user";
    public static String SELECT_USER = DOMAIN_URL + "select_user";
    public static String DISAGREE_OFFICER = DOMAIN_URL + "disagree_officer";
    public static String GET_BOOKMARKED = DOMAIN_URL + "bookmarked_events";
    public static String REMOVE_BOOKMARK = DOMAIN_URL + "remove_bookmarked_event";
    public static String GET_NOTIFICATION = DOMAIN_URL + "get_notification";
    public static String GET_FULL_LIST = DOMAIN_URL + "get_all_events";
    public static String GET_CHAT_ROOM = DOMAIN_URL + "fetch_chat_room";
    public static String GET_CHAT_MESSAGE = DOMAIN_URL + "fetch_selected_chat";
    public static String POST_MESSAGE = DOMAIN_URL + "send_message";
    public static String GET_ALL_OFFICER = DOMAIN_URL + "assign_officer_list";
    public static String ASSIGN_OFFICER = DOMAIN_URL + "assign_officer";
    public static String REGISTER_USER = DOMAIN_URL + "register";
    public static String REGISTER_EVENT = DOMAIN_URL + "register_event";
    public static String GET_VIP_LIST = DOMAIN_URL + "get_invited_vip";
    public static String GET_FEEDBACK = DOMAIN_URL + "get_feedback";
    public static String SEARCH_ALL_EVENT = DOMAIN_URL + "search_all_event";
    public static String GET_OFFICER_TASK = DOMAIN_URL + "get_officer_task";
    public static String UPDATE_PROFILE = DOMAIN_URL + "update_profile";
    public static String GET_APPS_INFO = DOMAIN_URL + "get_apps_info";
    public static String GET_JOINED_EVENT = DOMAIN_URL + "get_joined_events";
    public static String TOUCH_CHAT_ROOM = DOMAIN_URL + "touch_chat_room";
    public static String FORGOT_PASSWORD = DOMAIN_URL + "password/email";
    public static String CONFIRM_REJECT_REQUEST = DOMAIN_URL + "confirm_reject_request";
    public static String GET_PARTICIPANT = DOMAIN_URL + "get_participants";
    public static String DELETE_EVENT = DOMAIN_URL + "delete_event";
    public static String CHECK_AUTH = DOMAIN_URL + "check_auth";
    public static String GET_CATEGORY = DOMAIN_URL + "get_category";
    public static String GET_TOP_INFO_CATEGORY = DOMAIN_URL + "top_info_category";
    public static String GET_INFOR_BY_CATEGORY = DOMAIN_URL + "info_category_post";
    public static String SAVE_USER_PROFILE = DOMAIN_URL + "save_user_profile";
    public static String GET_LOCAL = DOMAIN_URL + "get_localisation";
    public static String UNREGISTER_EVENT = DOMAIN_URL + "unregister_event";
    public static String UPDATE_ADDRESS = DOMAIN_URL + "update_address";
    public static String UPLOAD_QR = DOMAIN_URL + "create_attendee_qr";
    public static String ATTENDEE_INFO = DOMAIN_URL + "attendee_info";
    public static String TAKE_ATTENDANCE = DOMAIN_URL + "attendance_event";
    public static String TAKE_EVENT_ATTENDANCE_DETAILS = DOMAIN_URL + "attendance_event_details";
    public static String DISPLAY_FEEDBACK_FORM = DOMAIN_URL + "event_feedback";

    public static String CHAT_NOTIFICATION = "room_list";

    private Context context;
    private Session session;

    public static String BROADCAST_MSG = "on_receive_message";
    public static String UPDATE_LIST = "on_message_receive";
    public static final String MESSAGE = "message";

    public Config(Context context){
        this.context = context;
        this.session = new Session(context);
    }

}
