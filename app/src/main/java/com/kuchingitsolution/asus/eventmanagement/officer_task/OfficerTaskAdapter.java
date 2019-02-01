package com.kuchingitsolution.asus.eventmanagement.officer_task;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kuchingitsolution.asus.eventmanagement.R;
import com.kuchingitsolution.asus.eventmanagement.config.Config;
import com.kuchingitsolution.asus.eventmanagement.event_details.DetailEventsActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class OfficerTaskAdapter extends RecyclerView.Adapter<OfficerTaskAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<OfficerTaskModel> officerTaskModels;

    OfficerTaskAdapter(Context context, ArrayList<OfficerTaskModel> officerTaskModels){
        this.context = context;
        this.officerTaskModels = officerTaskModels;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView events_title, events_start_time, events_end_time, events_status;
        private ImageView events_covers;

        public MyViewHolder(View itemView) {
            super(itemView);
            events_covers = itemView.findViewById(R.id.events_covers);
            events_title = itemView.findViewById(R.id.events_title);
            events_start_time = itemView.findViewById(R.id.events_start_time);
            events_end_time = itemView.findViewById(R.id.events_end_time);
            events_status = itemView.findViewById(R.id.events_status);
        }
    }


    @Override
    public OfficerTaskAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task_list, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(OfficerTaskAdapter.MyViewHolder holder, int position) {

        final OfficerTaskModel officerTaskModel = officerTaskModels.get(position);

        Glide.with(context).load(Config.IMAGE_CATALOG + officerTaskModel.getEvent_cover()).skipMemoryCache(false).into(holder.events_covers);
        holder.events_title.setText(officerTaskModel.getTitle());

        if(officerTaskModel.getStatus() == 1){
            holder.events_status.setText("ONGOING");
        } else {
            holder.events_status.setText("COMPLETED");
        }

        holder.events_start_time.setText(get_date(officerTaskModel.getStart_time()));
        holder.events_end_time.setText(get_date(officerTaskModel.getEnd_time()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetailEventsActivity.class);
                intent.putExtra("event_id", officerTaskModel.getEvent_id());
                context.startActivity(intent);
            }
        });
    }

    private String get_date(String time){

        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        String day = null;

        SimpleDateFormat dateFormat2 = new SimpleDateFormat(
                "dd MM yyyy, HH:mm a", Locale.getDefault());
        Date date = null;

        try {
            date = dateFormat.parse(time);
            day = dateFormat2.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return day;
    }

    @Override
    public int getItemCount() {
        return officerTaskModels.size();
    }


}
