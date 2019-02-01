package com.kuchingitsolution.asus.eventmanagement.feed;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kuchingitsolution.asus.eventmanagement.HomeActivity;
import com.kuchingitsolution.asus.eventmanagement.R;
import com.kuchingitsolution.asus.eventmanagement.config.Session;

import java.util.ArrayList;

public class CategoryAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<CategoryModel> mThumbIds = new ArrayList<>();
    private Session session;

    public CategoryAdapter(Context context, ArrayList<CategoryModel> mThumbIds) {
        this.context = context;
        this.mThumbIds = mThumbIds;
        this.session = new Session(context);
    }

    public View getView(int position, final View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View gridView;
        final CategoryModel categoryModel = mThumbIds.get(position);

        if (convertView == null) {

            gridView = new View(context);

            // get layout from mobile.xml
            gridView = inflater.inflate(R.layout.item_category, null);

            // set image based on selected text
            TextView textView = gridView
                    .findViewById(R.id.category_text);

            final LinearLayout grid_layout = gridView
                    .findViewById(R.id.grid_layout);
            final CardView grid_card = gridView
                    .findViewById(R.id.grid_card);

            String category_name = String.format("%s (%s)", categoryModel.getCategory_name(),categoryModel.getHosted_event());
            textView.setText(category_name);

            if(session.getIdValue("category_id") != 0){
                if(session.getIdValue("category_id") == categoryModel.getId()){
                    if (Build.VERSION.SDK_INT > 23 ) {
                        grid_card.setCardBackgroundColor(context.getResources().getColor(R.color.accent, null));
                    } else {
                        grid_card.setCardBackgroundColor(context.getResources().getColor(R.color.accent));
                    }
                }
            }

            grid_card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(categoryModel.getId() == session.getIdValue("category_id")){
                        search_again(0, "search_all");
                    } else {
                        search_again(categoryModel.getId(), categoryModel.getCategory_name());
                    }
                }
            });

        } else {
            gridView = (View) convertView;
        }

        return gridView;
    }

    private void search_again(int category_id, String name){

        session.setIdValue("category_id", category_id);
        session.setKeyValue("category_name", name);

        Intent intent = new Intent(context.getApplicationContext(), HomeActivity.class);
        context.startActivity(intent);

    }

    @Override
    public int getCount() {
        return mThumbIds.size();
    }

    @Override
    public Object getItem(int position) {
        return mThumbIds.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}
