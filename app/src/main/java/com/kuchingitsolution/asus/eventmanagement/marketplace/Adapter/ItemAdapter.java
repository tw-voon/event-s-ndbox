package com.kuchingitsolution.asus.eventmanagement.marketplace.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kuchingitsolution.asus.eventmanagement.R;
import com.kuchingitsolution.asus.eventmanagement.marketplace.DetailedActivity;
import com.kuchingitsolution.asus.eventmanagement.marketplace.apis.apiURL;
import com.kuchingitsolution.asus.eventmanagement.marketplace.model.Item;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    private ArrayList<Item> itemArrayList;
    private Context context;

    public ItemAdapter(ArrayList<Item> itemArrayList, Context context) {
        this.itemArrayList = itemArrayList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mv = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.homestayitem, parent, false);

        return new ViewHolder(mv);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Item item = itemArrayList.get(position);

        holder.itemname.setText(item.getName());

        Picasso.with(context).load(apiURL.IMAGE_BASE_URL + item.getImageUrl()).into(holder.itemimage);

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailedActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("id", item.getId());
                bundle.putString("name", item.getName());
                intent.putExtras(bundle);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(itemArrayList ==null){
            return 0;
        }
        else
            return itemArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView itemname;
        public ImageView itemimage;
        public LinearLayout linearLayout;

        public ViewHolder(View itemView) {
            super(itemView);

            itemname = itemView.findViewById(R.id.homestaytextviewID);
            itemimage = itemView.findViewById(R.id.homestayimageID);
            linearLayout = itemView.findViewById(R.id.linearlayouthomestay);
        }
    }
}
