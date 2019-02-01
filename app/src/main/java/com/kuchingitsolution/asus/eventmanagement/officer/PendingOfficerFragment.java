package com.kuchingitsolution.asus.eventmanagement.officer;

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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.kuchingitsolution.asus.eventmanagement.R;
import com.kuchingitsolution.asus.eventmanagement.config.Config;
import com.kuchingitsolution.asus.eventmanagement.config.Session;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PendingOfficerFragment extends Fragment {

    private Session session;
    private RecyclerView confirmed_list;
    private OfficerListAdapter officerListAdapter;
    private ArrayList<MyOfficerModel> myOfficerModels;
    private LinearLayoutManager linearLayoutManager;
    public String TYPE = "PENDING_OFFICER_LIST";
    private AVLoadingIndicatorView avi;
    private ImageView no_result;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_officer, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(getView() != null){
            setup_element(getView());
            get_confirmed_list();
        }
    }

    private void setup_element(View item_view){

        myOfficerModels = new ArrayList<>();
        avi = item_view.findViewById(R.id.avi);
        no_result = item_view.findViewById(R.id.no_result);

        confirmed_list = item_view.findViewById(R.id.my_officer_list);
        officerListAdapter = new OfficerListAdapter(getContext(), myOfficerModels);
        confirmed_list.setAdapter(officerListAdapter);
        linearLayoutManager = new LinearLayoutManager(getContext());
        confirmed_list.setLayoutManager(linearLayoutManager);

    }

    public void get_confirmed_list(){
        no_result.setVisibility(View.GONE);
        GetOfficerAsync getOfficerAsync = new GetOfficerAsync(getContext(), TYPE);
        getOfficerAsync.execute(Config.GET_PENDING_OFFICER_LIST);
    }

    private BroadcastReceiver listener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent ) {
            String data = intent.getStringExtra("result");
            Log.d( "Received data : ", "data here : " + data);
//            showMessage(data);
            parse_result(data);
        }
    };

    private void parse_result(String result){

        try {
            JSONObject all_data = new JSONObject(result);
            JSONArray info = all_data.optJSONArray("pendingOfficer");
            int length = info.length();

            if(length == 0){
                no_result.setVisibility(View.VISIBLE);
            }

            for (int i = 0; i < length ; i++){
                JSONObject officer = info.getJSONObject(i);
                MyOfficerModel myOfficerModel = new MyOfficerModel(officer);
                myOfficerModels.add(myOfficerModel);
                Log.d("loop","lopping " + i);
            }
            officerListAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        avi.hide();
    }

    private void showMessage(String message){
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialogBuilder.show();
    }

    public void remove_item(int position){
        myOfficerModels.remove(position);
        officerListAdapter.notifyItemRemoved(position);
        officerListAdapter.notifyItemRangeRemoved(position, officerListAdapter.getItemCount());

        if(officerListAdapter.getItemCount() == 0){
            no_result.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(listener,
                new IntentFilter(TYPE));
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(listener);
        super.onPause();
    }
}
