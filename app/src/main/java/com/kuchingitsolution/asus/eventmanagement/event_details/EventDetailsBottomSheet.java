package com.kuchingitsolution.asus.eventmanagement.event_details;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.View;
import android.widget.LinearLayout;

import com.kuchingitsolution.asus.eventmanagement.R;
import com.kuchingitsolution.asus.eventmanagement.config.Session;

import static android.view.View.GONE;

public class EventDetailsBottomSheet extends BottomSheetDialogFragment {

    String event_id, owner_id;
    private LinearLayout edit_post, contact_officer, assign_officer, event_view_feedback , event_feedback, btn_add_to_calender, delete_post, btn_invite_vip, btn_take_attendance;
    private OptionBottomSheetCallback optionBottomSheetCallback;
    private Session session;

    public void setData(String event_id, String owner_id){
        this.event_id = event_id;
        this.owner_id = owner_id;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(final Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        session = new Session(getContext());
        View contentView = View.inflate(getContext(), R.layout.bottom_sheet_event_details, null);
        dialog.setContentView(contentView);
        this.optionBottomSheetCallback = ((OptionBottomSheetCallback) getContext());

        edit_post = dialog.findViewById(R.id.btn_edit);
        contact_officer = dialog.findViewById(R.id.btn_contact_officer);
        assign_officer = dialog.findViewById(R.id.btnOfficer);
        event_feedback = dialog.findViewById(R.id.btnFeedback);
        delete_post = dialog.findViewById(R.id.btn_delete_post);
        btn_add_to_calender = dialog.findViewById(R.id.btn_add_to_calendar);
        btn_invite_vip = dialog.findViewById(R.id.btn_invite_vip);
        event_view_feedback = dialog.findViewById(R.id.view_feedback);
        btn_take_attendance = dialog.findViewById(R.id.btn_take_attendance);

        String user_id = session.getKeyValue(Session.USER_ID);
        String roles_id = session.getKeyValue(Session.ROLE_CODE);
        delete_post.setVisibility(GONE);
        edit_post.setVisibility(GONE);
        btn_take_attendance.setVisibility(GONE);

        switch (roles_id){

            case "2":
                setup_officer_element();
                break;

            case "4":
                setup_agency_element();
                break;

            case "5":
                setup_default_element();
                break;

            case "6":
                setup_usahawan_element();
                break;

        }

        btn_invite_vip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionBottomSheetCallback.add_vip();
            }
        });

        contact_officer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionBottomSheetCallback.contact_officer();
            }
        });

        assign_officer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionBottomSheetCallback.assignOfficer();
            }
        });

        event_feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionBottomSheetCallback.feedbackOnclick();
            }
        });

        btn_add_to_calender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionBottomSheetCallback.addCalender();
            }
        });
    }

    private void setup_agency_element(){

        if(session.getKeyValue(Session.ROLE_CODE).equals("4") && this.owner_id.equals(session.getKeyValue(Session.USER_ID))){
//            btn_invite_vip.setVisibility(View.VISIBLE);

            event_view_feedback.setVisibility(View.VISIBLE);
            event_feedback.setVisibility(GONE);
            contact_officer.setVisibility(View.GONE);
            btn_invite_vip.setVisibility(GONE);
            delete_post.setVisibility(View.VISIBLE);
            edit_post.setVisibility(View.VISIBLE);
            btn_take_attendance.setVisibility(View.VISIBLE);

            event_view_feedback.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    optionBottomSheetCallback.view_feedback();
                }
            });

            delete_post.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    optionBottomSheetCallback.delete_post();
                }
            });

            edit_post.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    optionBottomSheetCallback.editPost();
                }
            });

            btn_take_attendance.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    optionBottomSheetCallback.onAgencyClick();
                }
            });

        } else {

            btn_invite_vip.setVisibility(GONE);
            assign_officer.setVisibility(GONE);
            event_feedback.setVisibility(View.VISIBLE);
            contact_officer.setVisibility(View.VISIBLE);
            event_view_feedback.setVisibility(GONE);

        }

    }

    private void setup_officer_element(){

        contact_officer.setVisibility(GONE);
        assign_officer.setVisibility(GONE);
        btn_invite_vip.setVisibility(GONE);
        event_view_feedback.setVisibility(GONE);

    }

    private void setup_default_element(){

        btn_invite_vip.setVisibility(GONE);
        assign_officer.setVisibility(GONE);
        event_view_feedback.setVisibility(GONE);
        contact_officer.setVisibility(GONE);

    }

    private void setup_usahawan_element(){

        contact_officer.setVisibility(View.VISIBLE);
        btn_invite_vip.setVisibility(GONE);
        assign_officer.setVisibility(GONE);
        event_view_feedback.setVisibility(GONE);

    }

    public static interface OptionBottomSheetCallback {
        void onAgencyClick();
        void assignOfficer();
        void feedbackOnclick();
        void addCalender();
        void contact_officer();
        void add_vip();
        void view_feedback();
        void delete_post();
        void editPost();
    }
}
