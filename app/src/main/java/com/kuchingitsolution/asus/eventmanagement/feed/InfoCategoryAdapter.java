package com.kuchingitsolution.asus.eventmanagement.feed;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kuchingitsolution.asus.eventmanagement.InfoPost.InfoPostActivity;
import com.kuchingitsolution.asus.eventmanagement.R;

import java.util.ArrayList;

public class InfoCategoryAdapter extends RecyclerView.Adapter<InfoCategoryAdapter.MyViewHolder> {

    private ArrayList<CategoryModel> categoryModels = new ArrayList<>();
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView category_name;
        public CardView cardView;
        public MyViewHolder(View itemView) {
            super(itemView);
            category_name = itemView.findViewById(R.id.category_name);
            cardView = itemView.findViewById(R.id.item_container);
        }
    }

    InfoCategoryAdapter(Context context, ArrayList<CategoryModel> categoryModels){
        this.context = context;
        this.categoryModels = categoryModels;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_info_category, parent, false);
        return new InfoCategoryAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        final CategoryModel categoryModel = categoryModels.get(position);
        holder.category_name.setText(categoryModel.getCategory_name());

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, InfoPostActivity.class);
                intent.putExtra("category_id", String.valueOf(categoryModel.getId()));
                intent.putExtra("category_name", categoryModel.getCategory_name());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryModels.size();
    }



}
