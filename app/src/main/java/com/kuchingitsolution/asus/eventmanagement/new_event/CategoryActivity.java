package com.kuchingitsolution.asus.eventmanagement.new_event;

import android.content.Intent;
import android.os.AsyncTask;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.kuchingitsolution.asus.eventmanagement.R;
import com.kuchingitsolution.asus.eventmanagement.config.ApiAsyncTask;
import com.kuchingitsolution.asus.eventmanagement.config.Config;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CategoryActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ListView categories;
    private List<String> list_view;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<EventTypeModel> eventTypeModels = new ArrayList<>();
    private AsyncTask<String, Integer, String> apiAsyncTask;
    private AVLoadingIndicatorView avi;
    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.title_category);
        }

        Intent intent = getIntent();
        if(intent.hasExtra("type")){
            type = intent.getStringExtra("type");
        }

        categories = (ListView) findViewById(R.id.category_list);
        list_view = new ArrayList<String>();

        avi = findViewById(R.id.avi);
        avi.show();

        setup_category_adapter();
        get_category_list(type);


    }

    private void setup_category_adapter(){

        arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                list_view );

        categories.setAdapter(arrayAdapter);
        categories.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(CategoryActivity.this, NewEventActivity.class);
                intent.putExtra("category_name", adapterView.getAdapter().getItem(i).toString());
                intent.putExtra("category_id", eventTypeModels.get(i).getCategory_id());
                setResult(RESULT_OK, intent);
                finish();
            }
        });

    }

    private void get_category_list(String type){
        String url;
        if(type.equals("get_info_category")){
            url = Config.GET_INFOCATEGORY;
        } else {
            url = Config.GET_ALLCATEGORY;
        }
        apiAsyncTask = new ApiAsyncTask(this).execute(url);
    }

    public void updateList(String result){

        try {
            
            JSONArray jsonArray = new JSONArray(result);
            int length = jsonArray.length();

            for(int i = 0; i < length; i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                /*Populate list view*/
                list_view.add(jsonObject.getString("category_name"));
                EventTypeModel eventTypeModel = new EventTypeModel(jsonObject.getString("category_name"), jsonObject.getString("id"));
                eventTypeModels.add(eventTypeModel);
            }

            arrayAdapter.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        avi.hide();
        Log.d("category_list", result + " ------------------------------ ");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            // action with ID action_settings was selected
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;

    }
}
