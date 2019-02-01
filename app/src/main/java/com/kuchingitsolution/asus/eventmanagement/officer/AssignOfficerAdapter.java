package com.kuchingitsolution.asus.eventmanagement.officer;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.kuchingitsolution.asus.eventmanagement.R;

import java.util.ArrayList;

public class AssignOfficerAdapter extends RecyclerView.Adapter<AssignOfficerAdapter.MyViewHolder> {

    private ArrayList<AssignOfficerModel> assignOfficerModelList = new ArrayList<>();
    private OptionnAssignOfficerCallback assignOfficerCallback;
    private Context context;

    AssignOfficerAdapter(Context context, ArrayList<AssignOfficerModel> assignOfficerModelList){
        this.context = context;
        this.assignOfficerCallback = ((OptionnAssignOfficerCallback) context);
        this.assignOfficerModelList = assignOfficerModelList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView username;
        public ImageView imgProfile;
        public CheckBox checkBox;

        public MyViewHolder(View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            imgProfile = itemView.findViewById(R.id.profile_image);
            checkBox = itemView.findViewById(R.id.selected);
        }
    }

    @Override
    public AssignOfficerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_assign_officer_list, parent, false);
        return new AssignOfficerAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final AssignOfficerAdapter.MyViewHolder holder, int position) {

        final AssignOfficerModel assignOfficerModel = assignOfficerModelList.get(position);

        holder.username.setText(assignOfficerModel.getUsername());
        holder.imgProfile.setImageResource(R.drawable.profile_sample);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.checkBox.setChecked(true);
            }
        });

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                switch (compoundButton.getId()){
                    case R.id.selected:
                        holder.checkBox.setChecked(b);
                        assignOfficerCallback.OnOfficerSelect(assignOfficerModel.getUser_id(), b);
                        break;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return assignOfficerModelList.size();
    }

}
