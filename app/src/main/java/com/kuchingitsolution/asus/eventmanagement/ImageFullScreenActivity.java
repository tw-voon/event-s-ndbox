package com.kuchingitsolution.asus.eventmanagement;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import uk.co.senab.photoview.PhotoView;

public class ImageFullScreenActivity extends AppCompatActivity {

    private PhotoView imageView;
    private ProgressBar loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_full_screen);

        imageView = findViewById(R.id.fullscreen_image);
        loading = findViewById(R.id.loading_img);

        if (getIntent() != null) {
            String url = getIntent().getStringExtra("image");
            Picasso.with(this).load(url).into(imageView, new com.squareup.picasso.Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    loading.setVisibility(View.GONE);
                }
            });
            /*Glide.with(ImageFullScreenActivity.this)
                    .load(url)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            loading.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .skipMemoryCache(false)
                    .into(imageView);*/
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
