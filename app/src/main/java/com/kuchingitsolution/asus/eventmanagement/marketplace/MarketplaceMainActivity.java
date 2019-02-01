package com.kuchingitsolution.asus.eventmanagement.marketplace;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.kuchingitsolution.asus.eventmanagement.R;
import com.kuchingitsolution.asus.eventmanagement.marketplace.Adapter.ItemAdapter;
import com.kuchingitsolution.asus.eventmanagement.marketplace.apis.apiService;
import com.kuchingitsolution.asus.eventmanagement.marketplace.apis.apiURL;
import com.kuchingitsolution.asus.eventmanagement.marketplace.model.Item;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MarketplaceMainActivity extends AppCompatActivity {

    private static final String URL_DATA = "";

    RecyclerView homerecycler, foodrecycler;
    RecyclerView.Adapter foodadapter, homestayadapter;
    ArrayList<Item> itemhomeArrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marketplace_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Marketplace");
        }

        homerecycler = findViewById(R.id.homestayrecyclerviewID);
        foodrecycler = findViewById(R.id.foodrecyclerviewID);
        homerecycler.setHasFixedSize(true);
        foodrecycler.setHasFixedSize(true);
        itemhomeArrayList = new ArrayList<>();
        final ArrayList<Item>  foods= new ArrayList<>();
        final ArrayList<Item> homestays = new ArrayList<>();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(apiURL.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService service = retrofit.create(apiService.class);

        Call<ArrayList<Item>> call = service.getItems();

        call.enqueue(new Callback<ArrayList<Item>>() {
            @Override
            public void onResponse(Call<ArrayList<Item>> call, Response<ArrayList<Item>> response) {
                if(response != null){

                    for(Item item :response.body()){
                        if (item.getCategory().equals("Food")){
                            foods.add(item);
                        }
                        else
                        {
                            homestays.add(item);
                        }
                    }
                    foodadapter = new ItemAdapter(foods, getApplicationContext());
                    foodrecycler.setAdapter(foodadapter);

                    homestayadapter = new ItemAdapter(homestays, getApplicationContext());
                    homerecycler.setAdapter(homestayadapter);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Item>> call, Throwable t) {

            }
        });



//        Call<Item> call = service.getItems();
//
//        call.enqueue(new Callback<Item>() {
//            @Override
//            public void onResponse(Call<Item> call, Response<Item> response) {
//
//
//                if (response != null) {
//                    for (int i = 0; i < response.body().getItems().size(); i++) {
//                        itemhomeArrayList.add(response.body().getItems().get(i));
//                    }
//                    adapter = new ItemAdapter(response.body().getItems(), getApplicationContext());
//                    foodrecycler.setAdapter(adapter);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Item> call, Throwable t) {
//
//            }
//        });


        homerecycler.setLayoutManager(new LinearLayoutManager(this));
        foodrecycler.setLayoutManager(new LinearLayoutManager(this));


//        for (int i = 0; i<6;i++){
//            Item item = new Item(
//                    "item","description","","home"
//            );
//
//            itemhomeArrayList.add(item);
//        }

//        adapter = new ItemAdapter(itemhomeArrayList, this);
//
//        homerecycler.setAdapter(adapter);
//        foodrecycler.setAdapter(adapter);
        LinearLayoutManager horizontalLayoutManagaer = new LinearLayoutManager(MarketplaceMainActivity.this, LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager horizontalLayoutManagaer1 = new LinearLayoutManager(MarketplaceMainActivity.this, LinearLayoutManager.HORIZONTAL, false);
        homerecycler.setLayoutManager(horizontalLayoutManagaer);
        foodrecycler.setLayoutManager(horizontalLayoutManagaer1);


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
