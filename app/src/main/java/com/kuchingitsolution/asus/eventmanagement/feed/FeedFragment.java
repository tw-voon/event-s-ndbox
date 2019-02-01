package com.kuchingitsolution.asus.eventmanagement.feed;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.kuchingitsolution.asus.eventmanagement.HomeActivity;
import com.kuchingitsolution.asus.eventmanagement.R;
import com.kuchingitsolution.asus.eventmanagement.config.AppStatus;
import com.kuchingitsolution.asus.eventmanagement.config.Config;
import com.kuchingitsolution.asus.eventmanagement.config.DB;
import com.kuchingitsolution.asus.eventmanagement.config.EndlessScrollListener;
import com.kuchingitsolution.asus.eventmanagement.config.EndlessScrollListener2;
import com.kuchingitsolution.asus.eventmanagement.config.Session;
import com.kuchingitsolution.asus.eventmanagement.my_event.EventListAdapter;
import com.kuchingitsolution.asus.eventmanagement.my_event.GetEventAsyncList;
import com.kuchingitsolution.asus.eventmanagement.my_event.MyEventModel;
import com.kuchingitsolution.asus.eventmanagement.search.SearchActivity;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class FeedFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private String TYPE = "FULL_EVENT_LIST", NEXT_PAGE = "";
    private RecyclerView event_lists;
    private FeedFragmentAdapter feedFragmentAdapter;
    private Fragment childFragment;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ArrayList<FeedModel> newsData = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private EndlessScrollListener endlessScrollListener;
    private AVLoadingIndicatorView avi;
    private ImageView no_result;
    private Session session;
    private int page = 1;
    private boolean is_visible = false, is_view_ready = false, has_next_page = false;
    private DB db;
    private ScrollView scrollView;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_feed, container, false);
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        return super.onCreateAnimation(transit, enter, nextAnim);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        childFragment = new InfoCategoryFragment();
        getChildFragmentManager().beginTransaction().add(R.id.info_category_fragment, childFragment, "info_category").commit();
        //transaction.replace(R.id.info_category_fragment, childFragment).commit();
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

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
//        ViewGroup.MarginLayoutParams marginLayoutParams =
//                (ViewGroup.MarginLayoutParams) event_lists.getLayoutParams();
//        marginLayoutParams.setMargins(0, 120, 0, 10);
//        event_lists.setLayoutParams(marginLayoutParams);
        Log.d("marginreset", "resetting margin");
    }

    private void parse_result(final String result){

        if(result == null){
            avi.hide();
            no_result.setImageResource(R.drawable.generic_error);
            no_result.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setRefreshing(false);
            return;
        }

        final int current_size = feedFragmentAdapter.getItemCount();
        Handler handler = new Handler();
        Log.d("resulthere", result);
        final Runnable r = new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject data = new JSONObject(result);
                    JSONObject raw = data.getJSONObject("events");
                    JSONArray responses = data.optJSONArray("response");
                    JSONArray completed_event = raw.optJSONArray("data");
                    int completed_events_size = completed_event.length();

                    has_next_page = data.optJSONArray("next_page_url") != null;
                    NEXT_PAGE = data.optString("next_page_url", null);

                    if(completed_events_size > 0){

                        for (int i = 0 ; i < completed_events_size ; i ++ ){
                            JSONObject each_event = completed_event.getJSONObject(i);
                            JSONArray response = responses.optJSONArray(i);
                            FeedModel myEventModel = new FeedModel(each_event,response);
                            db.insertComplaint(each_event,response);
                            newsData.add(myEventModel);
                        }

                        feedFragmentAdapter.notifyItemRangeInserted(current_size, newsData.size());
                    } else {
                        avi.hide();
                        if(page == 1){
                            no_result.setImageResource(R.drawable.bookmark_empty_3);
                            no_result.setVisibility(View.VISIBLE);
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

    private void parse_offline(final String result){

        if(result == null){
            avi.hide();
            no_result.setImageResource(R.drawable.generic_error);
            no_result.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setRefreshing(false);
            return;
        }

        final int current_size = feedFragmentAdapter.getItemCount();
        Handler handler = new Handler();

        final Runnable r = new Runnable() {
            @Override
            public void run() {
                try {
                    JSONArray data = new JSONArray(result);
                    Log.d("result here", data.toString());
                    int completed_events_size = data.length();
                    if(completed_events_size > 0){

                        for (int i = 0 ; i < completed_events_size ; i ++ ){
                            JSONObject each_event = data.getJSONObject(i);
                            FeedModel myEventModel = new FeedModel(each_event);
                            newsData.add(myEventModel);
                        }

                        feedFragmentAdapter.notifyItemRangeInserted(current_size, newsData.size());
                    } else {
                        avi.hide();
                        if(page == 1){
                            no_result.setImageResource(R.drawable.bookmark_empty_3);
                            no_result.setVisibility(View.VISIBLE);
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

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(getView() != null){
//        if(is_visible)
            setup_element();
//            event_lists = getView().findViewById(R.id.event_lists);
//            avi = getView().findViewById(R.id.avi);
//            session = new Session(getContext());
//
//            no_result = getView().findViewById(R.id.no_result);
//            feedFragmentAdapter = new FeedFragmentAdapter(getContext(), newsData);
//            swipeRefreshLayout = getView().findViewById(R.id.event_refresh);
//            event_lists.setAdapter(feedFragmentAdapter);
//            linearLayoutManager = new LinearLayoutManager(getActivity());
//            event_lists.setLayoutManager(linearLayoutManager);
//            endlessScrollListener = new EndlessScrollListener(linearLayoutManager) {
//                @Override
//                public void onLoadMore(int pages, int totalItemsCount, RecyclerView view) {
//
//                    if(pages > 1){
//                        page = pages;
//                        swipeRefreshLayout.setRefreshing(true);
//                        get_all_events();
//                    }
//
//                }
//            };
//
//            event_lists.setNestedScrollingEnabled(false);
//            swipeRefreshLayout.setOnRefreshListener(this);
//            get_all_events();
        }
    }

    public void send_category_info(String result, String action_type){
        if(isAdded() && getContext() != null) {
            Fragment info_cate = getChildFragmentManager().findFragmentById(R.id.info_category_fragment);
            if (info_cate instanceof InfoCategoryFragment) {
                if(action_type.equals("parse_category_info")){
                    Log.d("testtt -- category  ", result + " -- ");
                    if(AppStatus.getInstance(getContext()).isOnline())
                        ((InfoCategoryFragment) info_cate).parse_category_result(result);
                    else
                        ((InfoCategoryFragment) info_cate).parse_offline();
                } else {
                    Log.d("testttt -- category ", " refreshing -- ");
                    if(AppStatus.getInstance(getContext()).isOnline())
                        ((InfoCategoryFragment) info_cate).get_info_category();
                    else
                        ((InfoCategoryFragment) info_cate).parse_offline();
                }

            }
        }
    }

    private void setup_element(){

        Log.d("loadingviewELEMENT", "view 1");
        if(getView() != null){
            event_lists = getView().findViewById(R.id.event_lists);
            avi = getView().findViewById(R.id.avi);
            session = new Session(getContext());
            db = new DB(getContext());

            no_result = getView().findViewById(R.id.no_result);
            scrollView = getView().findViewById(R.id.scrollView);
            feedFragmentAdapter = new FeedFragmentAdapter(getContext(), newsData);
            swipeRefreshLayout = getView().findViewById(R.id.event_refresh);
            event_lists.setAdapter(feedFragmentAdapter);
            linearLayoutManager = new LinearLayoutManager(getActivity());
            event_lists.setLayoutManager(linearLayoutManager);
            endlessScrollListener = new EndlessScrollListener (linearLayoutManager) {
                @Override
                public void onLoadMore(int pages, int totalItemsCount, RecyclerView view) {

                    if(pages > 1){
                        page = pages;
                        Log.d("pagerss", String.valueOf(page));
                        swipeRefreshLayout.setRefreshing(true);
                        if(AppStatus.getInstance(getActivity()).isOnline()){
                            if(has_next_page)
                                get_all_events();
                        } else  {
                            parse_offline(db.getEvents(page));
                        }

                    }
                }
            };

//            scrollView.set
            event_lists.setNestedScrollingEnabled(false);
            swipeRefreshLayout.setOnRefreshListener(this);
            is_view_ready = true;
            session.setKeyValue("current_position", null);
            session.setKeyValue("current_event", null);
            NEXT_PAGE = Config.GET_FULL_LIST;
//            if(session.getIdValue("category_id") == 0 && session.getKeyValue("category_name").equals("0")){
//                Intent intent = new Intent(getContext(), SearchActivity.class);
//                startActivity(intent);
//                return;
//            }

            if(AppStatus.getInstance(getContext()).isOnline()){
                if(has_next_page)
                    get_all_events();
            } else {
                Toast.makeText(getActivity(), "No internet connection", Toast.LENGTH_SHORT).show();
                parse_offline( db.getEvents(1));
            }

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

    private void get_all_events(){

        Log.d("loadingview-get_alleve", "view 2" + NEXT_PAGE);
        no_result.setVisibility(View.GONE);
        GetEventAsyncList getEventAsyncList = new GetEventAsyncList(getActivity(), TYPE, page);
        getEventAsyncList.execute(Config.GET_FULL_LIST);

    }

    public void add_like(String s, int position, String event_id){
        Log.d("result", s + " -- ");

        if(s == null){
            Toast.makeText(getContext(), "Error occur, Please try again later", Toast.LENGTH_SHORT).show();
        } else {
            try {
                JSONObject likes = new JSONObject(s);
                newsData.get(position).setSupport_this(likes.optInt("status"));
                newsData.get(position).setSupport(likes.optInt("total_like"));
                feedFragmentAdapter.notifyItemChanged(position);
                db.update_response(event_id,likes.optInt("status"), likes.optInt("total_like"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void add_bookmark(String s, int position){
        Log.d("result bookmark", s + " -- ");
    }

    @Override
    public void onRefresh() {

        if(newsData.size() > 0) {
            newsData.clear();
            feedFragmentAdapter.notifyDataSetChanged();
            page = 1;
        }

        swipeRefreshLayout.setRefreshing(true);

        if(AppStatus.getInstance(getContext()).isOnline()){
            get_all_events();
            send_category_info("", "others");
        } else {
            Toast.makeText(getActivity(), "No internet connection", Toast.LENGTH_SHORT).show();
            parse_offline(db.getEvents(1));
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(listener,
                new IntentFilter(TYPE));
        event_lists.addOnScrollListener(endlessScrollListener);
        swipeRefreshLayout.setRefreshing(false);
        if(avi.getVisibility() == View.VISIBLE){
            get_all_events();
        } else {
            update_viewed_item();
        }

    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(listener);
        super.onPause();
    }

    private void update_viewed_item(){
        String position = session.getKeyValue("current_position");
        String viewed_event = session.getKeyValue("current_event");
        Log.d("changes", "current position: " + position + " current_event : " + viewed_event);
        if(position != null && viewed_event != null && !viewed_event.equals("0")){
            String changes = db.get_event_changes(viewed_event);
            Log.d("changes here", changes);

            try {
                JSONArray jsonArray = new JSONArray(changes);
                JSONObject info = jsonArray.getJSONObject(0);

                newsData.get(Integer.valueOf(position)).setSupport(info.optInt("support"));
                newsData.get(Integer.valueOf(position)).setSupport_this(info.optInt("support_this"));
                feedFragmentAdapter.notifyItemChanged(Integer.valueOf(position));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
