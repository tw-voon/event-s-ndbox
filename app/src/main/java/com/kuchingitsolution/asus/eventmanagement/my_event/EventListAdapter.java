package com.kuchingitsolution.asus.eventmanagement.my_event;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.kuchingitsolution.asus.eventmanagement.R;
import com.kuchingitsolution.asus.eventmanagement.config.Config;
import com.kuchingitsolution.asus.eventmanagement.config.Helper;
import com.kuchingitsolution.asus.eventmanagement.config.Session;
import com.kuchingitsolution.asus.eventmanagement.event_details.DetailEventsActivity;
import com.wang.avi.AVLoadingIndicatorView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static android.view.View.GONE;

public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.MyViewHolder> {

    private ArrayList<MyEventModel> event_list = new ArrayList<>();
    public Context context;
    private Session session;

    public EventListAdapter(Context context, ArrayList<MyEventModel> event_list){
        this.event_list = event_list;
        this.context = context;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView day, month, year, event_title, category, timestamp, location, company;
        TextView like_no, like, bookmark;
        ImageView like_logo, bookmark_logo, event_cover;
        LinearLayout response, bookmark_region;
        AVLoadingIndicatorView avi;

        public MyViewHolder(View itemView) {
            super(itemView);

            day = itemView.findViewById(R.id.day);
            month = itemView.findViewById(R.id.month);
            year = itemView.findViewById(R.id.year);

            event_title = itemView.findViewById(R.id.events_title);
            location = itemView.findViewById(R.id.event_venue);
            like = itemView.findViewById(R.id.like);
            like_no = itemView.findViewById(R.id.like_no);
            bookmark = itemView.findViewById(R.id.bookmark_label);
            like_logo = itemView.findViewById(R.id.like_logo);
            bookmark_logo = itemView.findViewById(R.id.bookmark_icon);
            event_cover = itemView.findViewById(R.id.event_covers);
            response = itemView.findViewById(R.id.response);
            bookmark_region = itemView.findViewById(R.id.bookmark_section);
            company = itemView.findViewById(R.id.events_organizer);
            timestamp = itemView.findViewById(R.id.events_time);

            avi = itemView.findViewById(R.id.avi);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        final MyEventModel events = event_list.get(position);

        holder.day.setText(Helper.get_date("day", events.getCreated_at()));
        holder.month.setText(Helper.get_date("month", events.getCreated_at()));
        holder.year.setText(Helper.get_date("year", events.getCreated_at()));

        holder.event_title.setText(events.getReportTitle());
        String location = String.format("at - <b>%s</b>", events.getLocation_name());
        holder.location.setText(Html.fromHtml(location));
        holder.timestamp.setText(Helper.get_time(events.getCreated_at()));
        holder.company.setText(events.getCompany_name());
        holder.bookmark_region.setVisibility(GONE);
        holder.event_cover.setVisibility(View.VISIBLE);

        Glide.with(context).load(Config.IMAGE_CATALOG + events.getLink())
                .fitCenter()
                .crossFade(1000)
                .into(holder.event_cover);

        if(events.getSupport() > 0){
            holder.like_logo.setImageResource(R.drawable.ic_favorite_black_24dp);
            holder.like_no.setText(String.format("(%S)", events.getSupport()));
        }

        if(position != 0){
            MyEventModel prev_events = event_list.get(position-1);
            if(Helper.get_date("full",events.getCreated_at()).equals(Helper.get_date("full",prev_events.getCreated_at()))){
                holder.day.setVisibility(GONE);
                holder.month.setVisibility(GONE);
                holder.year.setVisibility(GONE);
            } else {
                holder.day.setVisibility(View.VISIBLE);
                holder.month.setVisibility(View.VISIBLE);
                holder.year.setVisibility(View.VISIBLE);
            }
        } else {
            holder.day.setVisibility(View.VISIBLE);
            holder.month.setVisibility(View.VISIBLE);
            holder.year.setVisibility(View.VISIBLE);
        }

        // setup item listener
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetailEventsActivity.class);
//                Toast.makeText(context, "data " + events.getId(), Toast.LENGTH_SHORT).show();
                intent.putExtra("event_id", events.getId());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return event_list.size();
    }

}
