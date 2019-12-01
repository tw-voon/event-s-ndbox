package com.kuchingitsolution.asus.eventmanagement.bookmark;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.kuchingitsolution.asus.eventmanagement.R;
import com.kuchingitsolution.asus.eventmanagement.config.Config;
import com.kuchingitsolution.asus.eventmanagement.event_details.DetailEventsActivity;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class BookmarkAdapter extends RecyclerView.Adapter<BookmarkAdapter.MyViewHolder> {

    private ArrayList<BookmarkModel> bookmarkModelList;
    private OptionBookmarkCallback optionBookmarkCallback;
    Context context;
    private String url;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView title, time, location;
        public ImageView event_cover;
        public ImageButton bookmark;

        public MyViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.events_title);
            location = itemView.findViewById(R.id.events_location);
            time = itemView.findViewById(R.id.events_time);
            event_cover = itemView.findViewById(R.id.event_covers);
            bookmark = itemView.findViewById(R.id.bookmark);
        }
    }

    public BookmarkAdapter(Context context, ArrayList<BookmarkModel> bookmarkModelList) {
        this.context = context;
        this.bookmarkModelList = bookmarkModelList;
        this.optionBookmarkCallback = ((OptionBookmarkCallback) context);
    }

    @Override
    public BookmarkAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_bookmark, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(BookmarkAdapter.MyViewHolder holder, final int position) {

        final BookmarkModel bookmarkModel = bookmarkModelList.get(position);

        Picasso.with(context).load(Config.IMAGE_CATALOG + bookmarkModel.getEvent_cover()).into(holder.event_cover);
        // Glide.with(context).load(Config.IMAGE_CATALOG + bookmarkModel.getEvent_cover()).skipMemoryCache(false).into(holder.event_cover);
        holder.title.setText(bookmarkModel.getTitle());

        String event_duration = String.format("%S - %S", get_date(bookmarkModel.getStart_time()), get_date(bookmarkModel.getEnd_time()));
        holder.time.setText(event_duration);
        holder.location.setText(bookmarkModel.getLocation());

        if (bookmarkModel.getIs_bookmarked() == 1) {
            holder.bookmark.setImageResource(R.drawable.ic_bookmark_black_24dp);
        }

        holder.bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionBookmarkCallback.OnBookmarkCLicked(position, bookmarkModel.getEvent_id());
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetailEventsActivity.class);
                intent.putExtra("event_id", bookmarkModel.getEvent_id());
                context.startActivity(intent);
            }
        });

    }

    private String get_date(String time) {

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
        return bookmarkModelList.size();
    }


}
