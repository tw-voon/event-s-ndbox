package com.kuchingitsolution.asus.eventmanagement.marketplace.apis;

import com.kuchingitsolution.asus.eventmanagement.marketplace.model.Item;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface apiService {

    @GET("items")
    Call<ArrayList<Item>> getItems();

    @GET("items/{id}")
    Call<Item> getSingleItem(@Path("id") int id);
}
