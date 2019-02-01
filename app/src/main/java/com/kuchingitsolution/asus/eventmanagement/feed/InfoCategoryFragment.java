package com.kuchingitsolution.asus.eventmanagement.feed;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;

import com.kuchingitsolution.asus.eventmanagement.R;
import com.kuchingitsolution.asus.eventmanagement.config.AppStatus;
import com.kuchingitsolution.asus.eventmanagement.config.CommonApiAsync;
import com.kuchingitsolution.asus.eventmanagement.config.Config;
import com.kuchingitsolution.asus.eventmanagement.config.DB;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class InfoCategoryFragment extends Fragment {

    private RecyclerView category_info_list;
    private InfoCategoryAdapter infoCategoryAdapter;
    private ArrayList<CategoryModel> categoryModels = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private JSONObject form_data = new JSONObject();
    private DB db;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_info_category, container, false);
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        return super.onCreateAnimation(transit, enter, nextAnim);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(getView() != null && getActivity() != null){

            db = new DB(getActivity());
            category_info_list = getView().findViewById(R.id.category_row);
            infoCategoryAdapter = new InfoCategoryAdapter(getContext(), categoryModels);
            linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
            category_info_list.setAdapter(infoCategoryAdapter);
            category_info_list.setLayoutManager(linearLayoutManager);
            ViewCompat.setNestedScrollingEnabled(category_info_list, false);

            try {
                form_data.put("type", "get_info_category");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if(AppStatus.getInstance(getActivity()).isOnline()) {
                get_info_category();
            } else {
                parse_offline();
            }
        }
    }

    public void get_info_category(){
        CommonApiAsync commonApiAsync = new CommonApiAsync(getContext(), form_data);
        commonApiAsync.execute(Config.GET_INFOCATEGORY);
    }

    public void parse_offline(){

        String result = db.getTableInfoCategory();

        if(result != null && !result.isEmpty()){

            if(categoryModels.size() > 0){
                categoryModels.clear();
            }

            try {
                JSONArray data = new JSONArray(result);
                int size = data.length();

                for (int i = 0; i < size ; i ++ ){
                    JSONObject info = data.optJSONObject(i);
                    CategoryModel categoryModel = new CategoryModel(info.optInt("id"), info.optString("name"));
                    categoryModels.add(categoryModel);
                }

                infoCategoryAdapter.notifyDataSetChanged();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void parse_category_result(String s){
        Log.d("call_category: ", s + " -- ");
//         showMessage(s);

        if(categoryModels.size() > 0){
            categoryModels.clear();
        }

        if(s == null){
            return;
        }

        try {

            JSONArray result = new JSONArray(s);
            int size = result.length();

            for(int i = 0 ; i < size ; i ++ ){
                JSONObject data = result.optJSONObject(i);
                CategoryModel categoryModel = new CategoryModel(data.optInt("id"), data.optString("category_name"));
                categoryModels.add(categoryModel);
            }

            infoCategoryAdapter.notifyDataSetChanged();
            db.insertInfoCategory(result);

        } catch (JSONException e) {
            e.printStackTrace();
        }
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
}
