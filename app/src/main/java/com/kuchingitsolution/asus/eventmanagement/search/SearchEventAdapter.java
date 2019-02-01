package com.kuchingitsolution.asus.eventmanagement.search;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kuchingitsolution.asus.eventmanagement.R;
import com.kuchingitsolution.asus.eventmanagement.config.Config;
import com.kuchingitsolution.asus.eventmanagement.config.RenderRoundedCornerImage;
import com.kuchingitsolution.asus.eventmanagement.event_details.DetailEventsActivity;
import com.kuchingitsolution.asus.eventmanagement.my_event.MyEventModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class SearchEventAdapter extends RecyclerView.Adapter<SearchEventAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<MyEventModel> myEventModels;

    SearchEventAdapter(Context context, ArrayList<MyEventModel> myEventModels){
        this.context = context;
        this.myEventModels = myEventModels;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView event_title, event_category, event_timestamp, event_location;
        public ImageView event_cover;

        public MyViewHolder(View itemView) {
            super(itemView);

            event_title = itemView.findViewById(R.id.event_title);
            event_category = itemView.findViewById(R.id.event_category);
            event_timestamp = itemView.findViewById(R.id.event_time);
            event_location = itemView.findViewById(R.id.event_location);
            event_cover = itemView.findViewById(R.id.event_covers);

        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_search_list, parent, false);
        return new SearchEventAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        final MyEventModel myEventModel = myEventModels.get(position);

        holder.event_title.setText(myEventModel.getTitle());
        holder.event_location.setText(myEventModel.getLocation_name());

        String event_time = String.format("%S - %S", get_time(myEventModel.getStart_time()), myEventModel.getEnd_time());
        holder.event_timestamp.setText(event_time);
        Glide.with(context).load(Config.IMAGE_CATALOG + myEventModel.getLink()).into(holder.event_cover);

        if(myEventModel.getStatus_id() == 1){
            holder.event_category.setText("ONGOING");
        } else {
            holder.event_category.setText("COMPLETED");
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context.getApplicationContext(), DetailEventsActivity.class);
                intent.putExtra("event_id", myEventModel.getId());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return myEventModels.size();
    }

    private String get_time(String time){

        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        SimpleDateFormat dateFormat2 = new SimpleDateFormat(
                "EEE, d MMM yyyy HH:mm:ss", Locale.getDefault());

        try {
            Date date = dateFormat.parse(time);
            Date now = dateFormat.parse(getDateTime());
            long diff = now.getTime() - date.getTime();

            long diffMinutes = diff / (60 * 1000) % 60;
            long diffHours = diff / (60 * 60 * 1000) % 24;
            long diffDays = diff / (24 * 60 * 60 * 1000);


            if(diffDays>0) {
                return String.valueOf(dateFormat2.format(date));
            } else if(diffHours>=1) {
                return String.format("%s hour ago", diffHours);
            } else if(diffMinutes<60) {
                return String.format("%s minute ago",diffMinutes);
            } else {
                return String.valueOf(0);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return time;

    }

    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

}
