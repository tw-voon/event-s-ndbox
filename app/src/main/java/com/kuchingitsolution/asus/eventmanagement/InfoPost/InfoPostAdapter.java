package com.kuchingitsolution.asus.eventmanagement.InfoPost;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.kuchingitsolution.asus.eventmanagement.R;
import com.kuchingitsolution.asus.eventmanagement.config.Config;
import com.kuchingitsolution.asus.eventmanagement.config.Session;
import com.kuchingitsolution.asus.eventmanagement.event_details.DetailEventsActivity;
import com.kuchingitsolution.asus.eventmanagement.feed.FeedModel;
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static android.view.View.GONE;

public class InfoPostAdapter extends RecyclerView.Adapter<InfoPostAdapter.MyViewHolder>{

    private ArrayList<FeedModel> event_list = new ArrayList<>();
    public Context context;
    private Session session;
//    private OptionEventFeedCallback optionEventFeedCallback;

    public InfoPostAdapter(Context context, ArrayList<FeedModel> event_list){
        this.event_list = event_list;
        this.context = context;
//        this.optionEventFeedCallback = ((OptionEventFeedCallback) context);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView day, month, year, event_title, category, timestamp, location, company;
        TextView like_no, like, bookmark;
        ImageView like_logo, bookmark_logo, event_cover;
        LinearLayout response, bookmark_region, like_section;
        AVLoadingIndicatorView avi;

        public MyViewHolder(View itemView) {
            super(itemView);

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
            like_section = itemView.findViewById(R.id.like_section);

            avi = itemView.findViewById(R.id.avi);
        }
    }

    @Override
    public InfoPostAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event_feed, parent, false);
        return new InfoPostAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final InfoPostAdapter.MyViewHolder holder, final int position) {

        final FeedModel events = event_list.get(position);

        holder.event_title.setText(events.getReportTitle());
        String location = String.format("at - <b>%s</b>", events.getLocation_name());
        holder.location.setText(Html.fromHtml(location));
        holder.bookmark_region.setVisibility(GONE);
        holder.event_cover.setVisibility(View.VISIBLE);
        //String event_time = String.format("%S - %S", get_time(events.getStart_time()),get_time(events.getEnd_time()));
        //holder.timestamp.setText(event_time);
        holder.timestamp.setVisibility(GONE);

        Picasso.with(context).load(Config.IMAGE_CATALOG + events.getLink()).into(holder.event_cover);
        /*Glide.with(context).load(Config.IMAGE_CATALOG + events.getLink())
                .fitCenter()
                .crossFade(1000)
                .into(holder.event_cover);*/

        if(events.getSupport() > 0){
            holder.like_logo.setImageResource(R.drawable.ic_favorite_border_black_16dp);
            holder.like_no.setText(String.format("%S", events.getSupport()));
        } else {
            holder.like_no.setText("0");
        }

        if(events.getSupport_this() == 1){
            holder.like_logo.setImageResource(R.drawable.ic_favorite_white_18dp);
        }

        holder.like_section.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((InfoPostActivity) context).OnEventLike(events.getId(), holder.getAdapterPosition());
//                optionEventFeedCallback.OnLikeClicked(holder.getAdapterPosition());
            }
        });

        // setup item listener
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetailEventsActivity.class);
//                Toast.makeText(context, "data " + events.getId(), Toast.LENGTH_SHORT).show();
                intent.putExtra("event_id", events.getId());
                intent.putExtra("position", position);
                Log.d("onesignal", "from adapter");
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return event_list.size();
    }

    private String get_date(String type, String time){

        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        String day = null;

        switch (type){
            case "day":
                try {
                    SimpleDateFormat dateFormat2 = new SimpleDateFormat(
                            "dd", Locale.getDefault());
                    Date date = dateFormat.parse(time);
                    day = dateFormat2.format(date);
                } catch (ParseException e){
                    e.printStackTrace();
                }
                break;

            case "month":
                try {
                    SimpleDateFormat dateFormat3 = new SimpleDateFormat(
                            "MMM", Locale.getDefault());
                    Date date = dateFormat.parse(time);
                    day = dateFormat3.format(date);
                } catch (ParseException e){
                    e.printStackTrace();
                }
                break;

            case "year":
                try {
                    SimpleDateFormat dateFormat3 = new SimpleDateFormat(
                            "yyyy", Locale.getDefault());
                    Date date = dateFormat.parse(time);
                    day = dateFormat3.format(date);
                } catch (ParseException e){
                    e.printStackTrace();
                }
                break;

            case "full":
                try {
                    SimpleDateFormat dateFormat3 = new SimpleDateFormat(
                            "yyyy-MM-dd", Locale.getDefault());
                    Date date = dateFormat.parse(time);
                    day = dateFormat3.format(date);
                } catch (ParseException e){
                    e.printStackTrace();
                }
                break;
        }

        return day;
    }

    private String get_time(String time){

        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        SimpleDateFormat dateFormat2 = new SimpleDateFormat(
                "E, d-MMM-yyyy HH:mm", Locale.getDefault());

        try {
            Date date = dateFormat.parse(time);
            Date now = dateFormat.parse(getDateTime());
            long diff = now.getTime() - date.getTime();

            long diffMinutes = diff / (60 * 1000) % 60;
            long diffHours = diff / (60 * 60 * 1000) % 24;
            long diffDays = diff / (24 * 60 * 60 * 1000);


//            if(diffDays>0) {
//                return String.valueOf(dateFormat2.format(date));
//            } else if(diffHours>=1) {
//                return String.format("%s hour ago", diffHours);
//            } else if(diffMinutes<60) {
//                return String.format("%s minute ago",diffMinutes);
//            } else {
//                return String.valueOf(0);
//            }
            return String.valueOf(dateFormat2.format(date));
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
