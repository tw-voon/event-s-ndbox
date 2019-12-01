package com.kuchingitsolution.asus.eventmanagement.event_details;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kuchingitsolution.asus.eventmanagement.R;

import java.util.ArrayList;

public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.MyViewHolder> {

    private ArrayList<ContactListModel> contactListModelList;
    Context context;
    private String url;
    private ContactListOptionCallback contactListOptionCallback;

    public ContactListAdapter(Context context, ArrayList<ContactListModel> contactListModelList){
        this.context = context;
        this.contactListModelList = contactListModelList;
        this.contactListOptionCallback = ((ContactListOptionCallback) context);
    }

    @Override
    public ContactListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_contact_list, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ContactListAdapter.MyViewHolder holder, int position) {

        final ContactListModel contactListModel = contactListModelList.get(position);

        holder.username.setText(contactListModel.getContact_name());
        holder.position.setText(contactListModel.getContact_position());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contactListOptionCallback.OnContactSelected(contactListModel.getContact_id(), contactListModel.getContact_name());
            }
        });

    }

    @Override
    public int getItemCount() {
        return contactListModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView username, position;

        public MyViewHolder(View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.chatUserName);
            position = itemView.findViewById(R.id.position);
        }
    }
}
