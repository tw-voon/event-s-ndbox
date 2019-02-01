package com.kuchingitsolution.asus.eventmanagement.event_vip;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kuchingitsolution.asus.eventmanagement.R;

import java.util.ArrayList;

public class EventInvitedVipAdapter extends RecyclerView.Adapter<EventInvitedVipAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<EventVipModel> eventVipModels;

    EventInvitedVipAdapter(Context context, ArrayList<EventVipModel> eventVipModels){
        this.context = context;
        this.eventVipModels = eventVipModels;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public ImageView profile_img;
        public TextView username, invite_status;
        public MyViewHolder(View itemView) {
            super(itemView);
            profile_img = itemView.findViewById(R.id.profile_image);
            username = itemView.findViewById(R.id.vip_name);
            invite_status = itemView.findViewById(R.id.vip_status);
        }

    }

    @Override
    public EventInvitedVipAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_horizontal_vip, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(EventInvitedVipAdapter.MyViewHolder holder, int position) {
        EventVipModel eventVipModel = eventVipModels.get(position);

        Log.d("test", eventVipModel.getInvite_status() + " ==== ");
        holder.username.setText(eventVipModel.getUsername());

        if(eventVipModel.getInvite_status().equals("1"))
            holder.invite_status.setText("Confirmed");
        else if(eventVipModel.getInvite_status().equals("0"))
            holder.invite_status.setText("Pending");

    }

    @Override
    public int getItemCount() {
        return eventVipModels.size();
    }


}
