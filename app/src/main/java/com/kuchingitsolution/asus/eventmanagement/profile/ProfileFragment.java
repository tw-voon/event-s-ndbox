package com.kuchingitsolution.asus.eventmanagement.profile;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kuchingitsolution.asus.eventmanagement.HomeActivity;
import com.kuchingitsolution.asus.eventmanagement.R;
import com.kuchingitsolution.asus.eventmanagement.bookmark.BookmarkActivity;
import com.kuchingitsolution.asus.eventmanagement.config.Config;
import com.kuchingitsolution.asus.eventmanagement.config.Session;
import com.kuchingitsolution.asus.eventmanagement.my_account.MyAccountActivity;
import com.kuchingitsolution.asus.eventmanagement.my_event.MyEventActivity;
import com.kuchingitsolution.asus.eventmanagement.new_event.InformationActivity;
import com.kuchingitsolution.asus.eventmanagement.new_event.NewEventActivity;
import com.kuchingitsolution.asus.eventmanagement.officer_task.OfficerTaskActivity;

public class ProfileFragment extends Fragment{

    private LinearLayout bookmark_section, event_section, new_event_section, account_section, joined_event_section, mytask_section;
    private TextView username;
    private Session session;
    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
        context = getContext();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        return super.onCreateAnimation(transit, enter, nextAnim);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(getView() != null){
            session = new Session(context);
            new_event_section = getView().findViewById(R.id.new_event_section);
            event_section = getView().findViewById(R.id.event_section);
            account_section = getView().findViewById(R.id.account_section);
            bookmark_section = getView().findViewById(R.id.bookmark_section);
            joined_event_section = getView().findViewById(R.id.joined_event_section);
            mytask_section = getView().findViewById(R.id.event_task_section);

            username = getView().findViewById(R.id.username);
            username.setText(session.getKeyValue(Session.USERNAME));

            switch (session.getKeyValue(Session.ROLE_CODE)) {
                case "4": // agency
                    new_event_section.setVisibility(View.VISIBLE);
                    event_section.setVisibility(View.VISIBLE);
                    break;
                case "5": // normal user
                    joined_event_section.setVisibility(View.VISIBLE);
                    break;
                case "2": // officer
                    mytask_section.setVisibility(View.VISIBLE);
                    break;
            }

            new_event_section.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), NewEventActivity.class);
                    startActivity(intent);
//                    choose_type();
                }
            });

            event_section.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), MyEventActivity.class);
                    startActivity(intent);
                }
            });

            account_section.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), MyAccountActivity.class);
                    startActivity(intent);
                }
            });

            bookmark_section.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), BookmarkActivity.class);
                    startActivity(intent);
                }
            });

            mytask_section.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), OfficerTaskActivity.class);
                    startActivity(intent);
                }
            });

            joined_event_section.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), OfficerTaskActivity.class);
                    startActivity(intent);
                }
            });
        }
    }

    private void choose_type(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.choose_posting_type)
                .setItems(R.array.events_type, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item
                        switch (which){
                            case 0:
                                redirect(0);
                                break;
                            case 1:
                                redirect(1);
                                break;
                        }
                    }
                });
        builder.create().show();
    }

    private void redirect(int cases){

        Intent intent = new Intent();
        switch (cases){
            case 0:
                intent = new Intent(context.getApplicationContext(), InformationActivity.class);
                break;
            case 1:
                intent = new Intent(context.getApplicationContext(), NewEventActivity.class);
        }

        if(getActivity() != null)
            getActivity().startActivity(intent);
    }
}
