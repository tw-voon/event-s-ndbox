package com.kuchingitsolution.asus.eventmanagement.officer;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.kuchingitsolution.asus.eventmanagement.R;

import java.util.ArrayList;

public class OfficerListAdapter extends RecyclerView.Adapter<OfficerListAdapter.MyViewHolder>{

    private Context context;
    private ArrayList<MyOfficerModel> officerModels = new ArrayList<>();
    private OptionOfficerAdapterCallback optionOfficerAdapterCallback;

    OfficerListAdapter(Context context, ArrayList<MyOfficerModel> officerModels){
        this.officerModels = officerModels;
        this.context = context;
        this.optionOfficerAdapterCallback = ((OptionOfficerAdapterCallback) context);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView username, email;
        public Button remove;
        public MyViewHolder(View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            email = itemView.findViewById(R.id.email);
            remove = itemView.findViewById(R.id.remove);
        }
    }

    @Override
    public OfficerListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_my_officer, parent, false);
        return new OfficerListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(OfficerListAdapter.MyViewHolder holder, final int position) {
        final MyOfficerModel myOfficerModel = officerModels.get(position);

        holder.username.setText(myOfficerModel.getOfficer_name());
        holder.email.setText(myOfficerModel.getOfficer_email());

        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "remove this user " + myOfficerModel.getRequest_id(), Toast.LENGTH_SHORT).show();
                String type = myOfficerModel.getStatus().equals("1") ? "confirmed" : "pending";
                optionOfficerAdapterCallback.onRemoveButtonClicked(myOfficerModel.getRequest_id(), type, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return officerModels.size();
    }


}
