package com.kuchingitsolution.asus.eventmanagement.feedback_manager;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.kuchingitsolution.asus.eventmanagement.R;
import com.kuchingitsolution.asus.eventmanagement.config.Session;

import java.util.ArrayList;

public class FeedbackManagerAdapter extends RecyclerView.Adapter<FeedbackManagerAdapter.MyViewHolder>{

    private ArrayList<FeedbackModel> feedbackModels = new ArrayList<>();
    private Context context;

    FeedbackManagerAdapter(Context context, ArrayList<FeedbackModel> feedbackModels){
        this.context = context;
        this.feedbackModels = feedbackModels;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView feedback_user, feedback_timestamp, feedback_content;
        public RatingBar feedback_rating;

        public MyViewHolder(View itemView) {
            super(itemView);

            feedback_user = itemView.findViewById(R.id.feedback_user);
            feedback_timestamp = itemView.findViewById(R.id.feedback_timestamp);
            feedback_content = itemView.findViewById(R.id.feedback_content);
            feedback_rating = itemView.findViewById(R.id.feedback_rating);

        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event_rating, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        FeedbackModel feedbackModel = feedbackModels.get(position);

        holder.feedback_user.setText(feedbackModel.getFeedback_user());
        holder.feedback_timestamp.setText(feedbackModel.getFeedback_time());
        holder.feedback_content.setText(feedbackModel.getFeedback_content());
        holder.feedback_rating.setRating(feedbackModel.getRating());

    }

    @Override
    public int getItemCount() {
        return feedbackModels.size();
    }


}
