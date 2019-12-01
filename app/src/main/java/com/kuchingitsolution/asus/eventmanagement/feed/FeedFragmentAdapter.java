package com.kuchingitsolution.asus.eventmanagement.feed;

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

import com.kuchingitsolution.asus.eventmanagement.HomeActivity;
import com.kuchingitsolution.asus.eventmanagement.R;
import com.kuchingitsolution.asus.eventmanagement.config.Config;
import com.kuchingitsolution.asus.eventmanagement.config.Helper;
import com.kuchingitsolution.asus.eventmanagement.event_details.DetailEventsActivity;
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;

import static android.view.View.GONE;

public class FeedFragmentAdapter extends RecyclerView.Adapter<FeedFragmentAdapter.MyViewHolder> {

    private ArrayList<FeedModel> event_list = new ArrayList<>();
    public Context context;

    public FeedFragmentAdapter(Context context, ArrayList<FeedModel> event_list) {
        this.event_list = event_list;
        this.context = context;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView event_title, timestamp, location, company;
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
    public FeedFragmentAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event_feed, parent, false);
        return new FeedFragmentAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final FeedFragmentAdapter.MyViewHolder holder, final int position) {

        final FeedModel events = event_list.get(position);

        holder.event_title.setText(events.getReportTitle());
        String location = String.format("at - <b>%s</b>", events.getLocation_name());
        holder.location.setText(Html.fromHtml(location));
        holder.bookmark_region.setVisibility(GONE);
        holder.event_cover.setVisibility(View.VISIBLE);
        String event_time = String.format("%S - %S", Helper.get_time(events.getStart_time()), Helper.get_time(events.getEnd_time()));
        holder.timestamp.setText(event_time);

        Picasso.with(context).load(Config.IMAGE_CATALOG + events.getLink()).into(holder.event_cover);
        /*Glide.with(context).load(Config.IMAGE_CATALOG + events.getLink())
                .fitCenter()
                .crossFade(1000)
                .into(holder.event_cover);*/

        if (events.getSupport() > 0) {
            holder.like_logo.setImageResource(R.drawable.ic_favorite_border_black_16dp);
            holder.like_no.setText(String.format("%S", events.getSupport()));
        } else {
            holder.like_no.setText("0");
        }

        if (events.getSupport_this() == 1) {
            holder.like_logo.setImageResource(R.drawable.ic_favorite_white_18dp);
        }

        holder.like_section.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((HomeActivity) context).OnEventLike(events.getId(), holder.getAdapterPosition());
            }
        });

        // setup item listener
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetailEventsActivity.class);
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

}