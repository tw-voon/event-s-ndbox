package com.kuchingitsolution.asus.eventmanagement.event_attendees;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kuchingitsolution.asus.eventmanagement.R;

import java.util.ArrayList;

public class ParticipantAdapter extends RecyclerView.Adapter<ParticipantAdapter.MyViewHolder>{

    private ArrayList<ParticipantModel> participantModelArrayList = new ArrayList<>();
    private Context context;

    public ParticipantAdapter(Context context, ArrayList<ParticipantModel> participantModels){
        this.context = context;
        this.participantModelArrayList = participantModels;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        final ParticipantModel participantModel = participantModelArrayList.get(position);
        holder.username.setText(participantModel.getName());

    }

    @Override
    public int getItemCount() {
        return participantModelArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView username;

        public MyViewHolder(View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
        }
    }
}
