package com.kuchingitsolution.asus.eventmanagement.event_details;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kuchingitsolution.asus.eventmanagement.R;

import java.util.ArrayList;
import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.MyViewHolder>{

    private List<CommentsModel> comments;
    Context context;
    private String url;

    public CommentsAdapter(Context context, ArrayList<CommentsModel> commentsModels){
        this.context = context;
        this.comments = commentsModels;
    }

    @Override
    public CommentsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comment, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CommentsAdapter.MyViewHolder holder, int position) {

        CommentsModel commentsModel = comments.get(position);

        holder.username.setText(commentsModel.getUsername());
        holder.message.setText(commentsModel.getCommentmsg());
        holder.time.setText(commentsModel.getTimeago());

    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView username, message, time;

        public MyViewHolder(View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            message = itemView.findViewById(R.id.commentmsg);
            time = itemView.findViewById(R.id.time);
        }
    }
}
