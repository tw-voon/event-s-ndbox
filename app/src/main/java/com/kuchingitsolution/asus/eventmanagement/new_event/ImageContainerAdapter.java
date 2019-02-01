package com.kuchingitsolution.asus.eventmanagement.new_event;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.kuchingitsolution.asus.eventmanagement.R;
import com.kuchingitsolution.asus.eventmanagement.config.Config;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ImageContainerAdapter extends RecyclerView.Adapter<ImageContainerAdapter.MyViewHolder> {

    private List<ImageModel> image_models;
    private OpenCameraInterface cameraInterface;
    private Context context;

    public ImageContainerAdapter(Context context, List<ImageModel> image_models){
        this.context = context;
        this.image_models = image_models;
        this.cameraInterface = ((OpenCameraInterface) context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.frame_image_preview, parent, false);
        return new ImageContainerAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        ImageModel image_model = image_models.get(position);

        if(!image_model.getImage_uri().isEmpty()){
            Log.d("LoadLink2", "image path: " + image_model.getImage_uri());
            Picasso.with(context).load(Config.IMAGE_CATALOG + image_model.getImage_uri()).resize(400, 400).into(holder.image_view);
        }else if(image_model.getPath().equals("")){
            Log.d("CamActivity", "image path 2: " + image_model.getPath());
            holder.image_view.setImageResource(R.drawable.background_img);
            holder.image_close.setVisibility(View.INVISIBLE);
        } else {
            Log.d("CamActivity", "image path: " + image_model.getPath());
            holder.image_view.setImageURI(Uri.parse(image_model.getPath()));
            holder.image_close.setVisibility(View.VISIBLE);
        }

        holder.image_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cameraInterface.onImageCloseClicked(position);
            }
        });

        holder.image_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cameraInterface.onImageClicked(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return this.image_models.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        FrameLayout frameLayout;
        ImageView image_view, image_close;

        public MyViewHolder(View itemView) {
            super(itemView);
            frameLayout = itemView.findViewById(R.id.card_view);
            image_view = itemView.findViewById(R.id.image_view);
            image_close = itemView.findViewById(R.id.image_close);
        }
    }

}
