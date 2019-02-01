package com.kuchingitsolution.asus.eventmanagement.feed;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.GridView;
import android.widget.ImageView;

import com.kuchingitsolution.asus.eventmanagement.R;
import com.kuchingitsolution.asus.eventmanagement.config.CommonApiAsync;
import com.kuchingitsolution.asus.eventmanagement.config.Config;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CategoryFragment extends Fragment {

    private GridView category_grid;
    private CategoryAdapter categoryAdapter;
    private AVLoadingIndicatorView avi;
    private ImageView no_result;
    private ArrayList<CategoryModel> categoryModels = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_category, container, false);
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        return super.onCreateAnimation(transit, enter, nextAnim);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getView() != null) {
            setup_element(getView());
        }
    }

    private BroadcastReceiver listener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent ) {
            String data = intent.getStringExtra("result");
            Log.d( "Received data : ", "data here : " + data);
//            showMessage(data);
//            parse_result(data);
        }
    };

    private void setup_element(View view){

        category_grid = view.findViewById(R.id.grid_view);
        avi = view.findViewById(R.id.avi);
        no_result = view.findViewById(R.id.no_result);

        categoryAdapter = new CategoryAdapter(getContext(), categoryModels);
        category_grid.setAdapter(categoryAdapter);

        JSONObject form_data = new JSONObject();
        try {
            form_data.put("type", "category_list");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        get_all_category(form_data);

    }

    private void get_all_category(JSONObject form_data){

        CommonApiAsync commonApiAsync = new CommonApiAsync(getContext(), form_data);
        commonApiAsync.execute(Config.GET_CATEGORY);

    }

    public void parse_data(String r){
        showMessage(r);
        Log.d("test----", "---- " + r);

        try {
            JSONObject data = new JSONObject(r);
            JSONArray info = data.optJSONArray("categories");
            int length = info.length();

            for(int i = 0; i < length; i++){
                JSONObject category = info.optJSONObject(i);
                CategoryModel categoryModel = new CategoryModel(category.optInt("id"), category.optString("category_name"),category.optInt("id"));
                categoryModels.add(categoryModel);
            }

            categoryAdapter.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        avi.hide();
    }

    private void showMessage(String message){
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setPositiveButton("JOIN", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialogBuilder.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(getActivity() != null) {
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(listener,
                    new IntentFilter("FULL_CATEGORY_LIST"));
        }
    }

    @Override
    public void onPause() {
        if(getActivity() != null) {
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(listener);
        }
        super.onPause();
    }
}
