package com.kuchingitsolution.asus.eventmanagement.marketplace;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.kuchingitsolution.asus.eventmanagement.R;
import com.kuchingitsolution.asus.eventmanagement.marketplace.apis.apiService;
import com.kuchingitsolution.asus.eventmanagement.marketplace.apis.apiURL;
import com.kuchingitsolution.asus.eventmanagement.marketplace.model.Item;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DetailedActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView nameV, categoryV, descV;
    private Toolbar toolbar;
    private int id = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle bundle = getIntent().getExtras();

        if(bundle != null){
            id = bundle.getInt("id");
            String title = bundle.getString("name");
            if(getSupportActionBar() != null){
                getSupportActionBar().setTitle(title);
            }
        }

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if(bundle == null){
            onBackPressed();
            return;
        }

        imageView = findViewById(R.id.detailimageid);
        nameV = findViewById(R.id.detailnameid);
        categoryV = findViewById(R.id.detailcategoryid);
        descV = findViewById(R.id.detaildescid);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(apiURL.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService service = retrofit.create(apiService.class);

        Call<Item> call = service.getSingleItem(id);

        call.enqueue(new Callback<Item>() {
            @Override
            public void onResponse(Call<Item> call, Response<Item> response) {
                if (response != null && response.body() != null){
                    nameV.setText(response.body().getName());
                    categoryV.setText(response.body().getCategory());
                    descV.setText(response.body().getDescription());
                    Picasso.with(getApplicationContext()).load(apiURL.IMAGE_BASE_URL + response.body().getImageUrl()).into(imageView);
                }
            }

            @Override
            public void onFailure(Call<Item> call, Throwable t) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }
}
