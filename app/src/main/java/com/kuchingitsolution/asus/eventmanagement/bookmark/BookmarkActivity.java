package com.kuchingitsolution.asus.eventmanagement.bookmark;

import android.content.DialogInterface;
import android.os.Build;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.kuchingitsolution.asus.eventmanagement.R;
import com.kuchingitsolution.asus.eventmanagement.config.Config;
import com.kuchingitsolution.asus.eventmanagement.config.Session;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class BookmarkActivity extends AppCompatActivity implements OptionBookmarkCallback{

    private Toolbar toolbar;
    private Session session;
    private RecyclerView bookmark_list;
    private BookmarkAdapter bookmarkAdapter;
    private ArrayList<BookmarkModel> bookmarkModels = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private JSONObject form_data = new JSONObject();
    private ImageView no_result;
    private AVLoadingIndicatorView avi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.title_bookmark);
        }

        setup_element();
        get_bookmark_list();
    }

    private void get_bookmark_list(){

        compile_form_data("user_id", session.getKeyValue(Session.USER_ID));
        compile_form_data("type", "GET_BOOKMARKED");

        new BookmarkAsync(this, get_form_data()).execute(Config.GET_BOOKMARKED);
    }

    public void parse_bookmark_result(String s){
        Log.d("result from server ", " : is here : "+ s);

        if(s == null){
            avi.hide();
            Toast.makeText(this, "Error while bookmarked. Please make sure your internet connection is good", Toast.LENGTH_SHORT).show();
            no_result.setVisibility(View.VISIBLE);
            return;
        }

        try {
            JSONObject raw = new JSONObject(s);
            JSONArray bookmarks = raw.getJSONArray("bookmarked_events");
            int length = bookmarks.length();

            if(length == 0){
                no_result.setVisibility(View.VISIBLE);
                avi.hide();
                return;
            }

            for (int i = 0; i < length ; i++){
                JSONObject bookmark = bookmarks.getJSONObject(i);
                BookmarkModel bookmarkModel = new BookmarkModel(bookmark);
                bookmarkModels.add(bookmarkModel);
            }

            bookmarkAdapter.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        avi.hide();
    }

    private void compile_form_data(String key, String value){

        try {
            form_data.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private JSONObject get_form_data(){
        return this.form_data;
    }

    private void remove_form_data(){
        this.form_data = new JSONObject();
    }

    public void remove_bookmark(String s){

        if(s.equals("success")){
            bookmarkModels.remove(form_data.optInt("position"));
            bookmarkAdapter.notifyItemRemoved(form_data.optInt("position"));
            bookmarkAdapter.notifyItemRangeChanged(form_data.optInt("position"), bookmarkAdapter.getItemCount());
            display_status("Successfully removed");

            if(bookmarkAdapter.getItemCount() == 0){
                no_result.setVisibility(View.GONE);
            }

            remove_form_data();
        }
    }

    private void setup_element(){

        session = new Session(this);
        bookmark_list = findViewById(R.id.bookmark_list);
        bookmarkAdapter = new BookmarkAdapter(this, bookmarkModels);
        bookmark_list.setAdapter(bookmarkAdapter);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setInitialPrefetchItemCount(4);
        bookmark_list.setLayoutManager(linearLayoutManager);

        avi = findViewById(R.id.avi);
        no_result = findViewById(R.id.no_result);

        avi.show();
        no_result.setVisibility(View.GONE);

    }

    @Override
    public void OnBookmarkCLicked(int position, String event_id) {

        compile_form_data("position", String.valueOf(position));
        compile_form_data("event_id", event_id);
        compile_form_data("user_id", session.getKeyValue(Session.USER_ID));
        compile_form_data("type", "REMOVE_BOOKMARK");

        showMessage();

    }

    private void remove_bookmark(){ new BookmarkAsync(this, get_form_data()).execute(Config.REMOVE_BOOKMARK); }


    private void showMessage(){

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Logout");
        alertDialogBuilder.setMessage("Are you sure want to remove this bookmark ?");
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                remove_bookmark();
                dialog.dismiss();
            }
        });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                remove_form_data();
                dialogInterface.dismiss();
            }
        });
        alertDialogBuilder.show();
    }

    private void display_status(String message){
        Snackbar snackbar = Snackbar
                .make(findViewById(R.id.bookmark_layout), Html.fromHtml("<font color=\"#ffffff\">"+ message +"</font>"), Snackbar.LENGTH_LONG);
        View snackBarView = snackbar.getView();
        if(Build.VERSION.SDK_INT >= 23 )
            snackBarView.setBackgroundColor(getResources().getColor(R.color.mt_red, null));
        else
            snackBarView.setBackgroundColor(getResources().getColor(R.color.mt_red));
        snackbar.show();
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
