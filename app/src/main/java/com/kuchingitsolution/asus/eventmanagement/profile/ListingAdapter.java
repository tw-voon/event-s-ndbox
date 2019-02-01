package com.kuchingitsolution.asus.eventmanagement.profile;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kuchingitsolution.asus.eventmanagement.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ListingAdapter extends RecyclerView.Adapter<ListingAdapter.MyViewHolder>{

    ArrayList<country_state_model> country_state_models = new ArrayList<>();
    private Context context;

    public ListingAdapter(Context context, ArrayList<country_state_model> country_state_models) {
        this.context = context;
        this.country_state_models = country_state_models;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_country_state, parent, false);
        return new ListingAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        final country_state_model model = country_state_models.get(position);
        holder.name.setText(model.getName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(context instanceof CountryStateActivity){
                    ((CountryStateActivity) context).send_response(model.getId(), model.getName());
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return country_state_models.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public MyViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
        }
    }
}
