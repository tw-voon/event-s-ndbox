package com.kuchingitsolution.asus.eventmanagement.my_event;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.kuchingitsolution.asus.eventmanagement.config.EndlessScrollListener;
import com.kuchingitsolution.asus.eventmanagement.config.Session;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class RunningEventFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private String TYPE = "RUNNING_EVENT_LIST";
    private RecyclerView ongoing_list;
    private EventListAdapter eventListAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ArrayList<MyEventModel> newsData = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private EndlessScrollListener endlessScrollListener;
    private AVLoadingIndicatorView avi;
    private ImageView no_result;
    private Session session;
    private int page = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_event_list, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(getView() != null){

            ongoing_list = getView().findViewById(R.id.event_lists);
            avi = getView().findViewById(R.id.avi);
            session = new Session(getContext());

            no_result = getView().findViewById(R.id.no_result);
            swipeRefreshLayout = getView().findViewById(R.id.event_refresh);
            eventListAdapter = new EventListAdapter(getContext(), newsData);
            ongoing_list.setAdapter(eventListAdapter);
            linearLayoutManager = new LinearLayoutManager(getActivity());
            ongoing_list.setLayoutManager(linearLayoutManager);
            endlessScrollListener = new EndlessScrollListener(linearLayoutManager) {
                @Override
                public void onLoadMore(int pages, int totalItemsCount, RecyclerView view) {

                    if(pages > 1){
                        page = pages;
                        swipeRefreshLayout.setRefreshing(true);
                        get_running_events();
                    }
                }
            };
//            endlessRecyclerViewScrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
//                @Override
//                public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
//                    Log.d("page", String.valueOf(page) + " " + totalItemsCount);
//                    getComplaint.load_data(page, Config.URL_GET_UNSOLVE);
//                }
//            };
            ongoing_list.setNestedScrollingEnabled(false);
            swipeRefreshLayout.setOnRefreshListener(this);
            get_running_events();
        }
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

    private void parse_result(final String result){

        if(result == null){
            no_result.setImageResource(R.drawable.generic_error);
            no_result.setVisibility(View.VISIBLE);
            avi.hide();
            swipeRefreshLayout.setRefreshing(false);
        }

        final int current_size = eventListAdapter.getItemCount();
        Handler handler = new Handler();

        final Runnable r = new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject data = new JSONObject(result);
                    JSONObject jsonObject = data.getJSONObject("on_going_events");
                    JSONArray on_going_event = jsonObject.getJSONArray("data");
                    int on_going_events = on_going_event.length();
                    if(on_going_events > 0){

                        for (int i = 0 ; i < on_going_events ; i ++ ){
                            JSONObject each_event = on_going_event.getJSONObject(i);
                            MyEventModel myEventModel = new MyEventModel(each_event);
                            newsData.add(myEventModel);
                        }

                        eventListAdapter.notifyItemRangeChanged(current_size, newsData.size());

                        if(eventListAdapter.getItemCount() == 0){
                            no_result.setImageResource(R.drawable.events_empty_data_set);
                            no_result.setVisibility(View.VISIBLE);
                        } else {
                            no_result.setVisibility(View.GONE);
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        handler.post(r);
        avi.hide();
        swipeRefreshLayout.setRefreshing(false);

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

    private void get_running_events(){
        no_result.setVisibility(View.GONE);
        GetEventAsyncList getEventAsyncList = new GetEventAsyncList(getActivity(), TYPE, page);
        getEventAsyncList.execute(Config.EVENT_GETS_OWN_ONGOING);
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(listener,
                new IntentFilter(TYPE));
        ongoing_list.addOnScrollListener(endlessScrollListener);
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(listener);
        super.onPause();
    }

    @Override
    public void onRefresh() {

        if(newsData.size() > 0)
            newsData.clear();

        swipeRefreshLayout.setRefreshing(true);
        get_running_events();
    }
}
