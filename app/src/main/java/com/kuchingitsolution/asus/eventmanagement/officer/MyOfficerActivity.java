package com.kuchingitsolution.asus.eventmanagement.officer;

import android.content.Intent;
import android.os.Handler;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.kuchingitsolution.asus.eventmanagement.R;
import com.kuchingitsolution.asus.eventmanagement.config.Config;
import com.kuchingitsolution.asus.eventmanagement.my_event.ViewPagerAdapter;
import com.yarolegovich.lovelydialog.LovelyInfoDialog;

public class MyOfficerActivity extends AppCompatActivity implements OptionOfficerAdapterCallback{

    private Toolbar toolbar;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private PopupWindow mPopupWindow;
    private CoordinatorLayout coordinatorLayout;
    private static int SELECT_OFFICE_CODE = 1011;
    private ViewPagerAdapter adapter;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_officer);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        coordinatorLayout = findViewById(R.id.my_officer);
        viewPager =  findViewById(R.id.viewpager);
        tabLayout =  findViewById(R.id.tabs);
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

        handler.postDelayed(info, 1000);
    }

    final Runnable info = new Runnable() {
        @Override
        public void run() {
            show_guide();
        }
    };

    private void show_guide(){
        new LovelyInfoDialog(this)
                .setTopColorRes(R.color.colorAccent)
                .setIcon(R.drawable.ic_check_white_18dp)
                .setTitle(R.string.my_officer_title)
                .setMessage(R.string.my_officer_desc)
                .setNotShowAgainOptionEnabled(1)
                .show();
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setOffscreenPageLimit(2);
        adapter.addFragment(new ConfirmedOfficerFragment(), getString(R.string.my_officer));
        adapter.addFragment(new PendingOfficerFragment(), getString(R.string.pending_officer));
        viewPager.setAdapter(adapter);
    }

    private void search_officer(){
        Intent intent = new Intent(MyOfficerActivity.this, SearchOfficerActivity.class);
        startActivityForResult(intent, SELECT_OFFICE_CODE);
        overridePendingTransition(R.anim.activity_slide_up, R.anim.stay);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_OFFICE_CODE && resultCode == RESULT_OK) {
            // Create intent with action
            PendingOfficerFragment fragment = (PendingOfficerFragment) getSupportFragmentManager().findFragmentById(viewPager.getId());
            fragment.get_confirmed_list();
            viewPager.setCurrentItem(1, true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.new_officer_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        super.onOptionsItemSelected(menuItem);

        int itemId = menuItem.getItemId();
        switch (itemId){
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.new_officer:
                search_officer();
                Toast.makeText(this, "Add new Officer", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    @Override
    public void onRemoveButtonClicked(String request_id, String type, int position) {
        new RemoveOfficerAsync(this, request_id, type, position).execute(Config.DISAGREE_OFFICER);
    }

    public void refresh_fragment_info(String type, int position){

        Log.d("view page stuff", "type here: " + type + " position : " + position);
        if(type.equals("confirmed")){

            ConfirmedOfficerFragment confirmedOfficerFragment = (ConfirmedOfficerFragment) adapter.getItem(0);
            confirmedOfficerFragment.remove_item(position);

        } else if(type.equals("pending")){

            PendingOfficerFragment pendingOfficerFragment = (PendingOfficerFragment) adapter.getItem(1);
            pendingOfficerFragment.remove_item(position);

        }

    }
}
