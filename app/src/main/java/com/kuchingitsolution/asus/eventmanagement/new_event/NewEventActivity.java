package com.kuchingitsolution.asus.eventmanagement.new_event;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.material.snackbar.Snackbar;
import com.kuchingitsolution.asus.eventmanagement.HomeActivity;
import com.kuchingitsolution.asus.eventmanagement.R;
import com.kuchingitsolution.asus.eventmanagement.config.Config;
import com.kuchingitsolution.asus.eventmanagement.config.ImageCompressionUtils;
import com.kuchingitsolution.asus.eventmanagement.config.Utility;
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NewEventActivity extends AppCompatActivity implements OpenCameraInterface {

    private Toolbar toolbar;
    private LinearLayout pick_category, pick_start_time, pick_end_time, pick_register_start_time, pick_register_end_time, pick_registration_section, pick_location;
    private RecyclerView image_row;
    private TextView event_title, event_desc, event_extra_info, selected_category, start_datetime, end_datetime, at_location, register_start_time, register_end_time;
    private ImageView location_preview;
    private static int SELECT_CATEGORY_CODE = 1001, REQUEST_CAMERA = 126, PICK_IMAGE_REQUEST = 127, PLACE_PICKER = 128;
    private static String EVENT_REGISTRATION = "event_registration", EVENT_TIME = "event_time";
    private int image_position;
    private JSONObject data = new JSONObject();
    private String temp_dates;
    private Uri tempImgFile;
    private List<ImageModel> imageModels = new ArrayList<>();
    private ImageContainerAdapter imageContainerAdapter;
    private ImageCompressionUtils imageCompressionUtils;
    private AVLoadingIndicatorView avi;

    private AlertDialog.Builder alert;
    private AlertDialog ad;
    private ProgressBar dialog_upload;
    private TextView dialog_upload_status;
    private ImageView status_done;
    private MenuItem menuitem;

    private String event_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        setup_view_element();
        setup_element_listener();
        imageCompressionUtils = new ImageCompressionUtils(this);

    }

    private void setup_view_element() {

        // Linear layout section
        pick_category = findViewById(R.id.pick_category);
        pick_start_time = findViewById(R.id.pick_start_date);
        pick_end_time = findViewById(R.id.pick_end_date);
        pick_register_start_time = findViewById(R.id.pick_start_dateline);
        pick_register_end_time = findViewById(R.id.pick_end_dateline);
        pick_registration_section = findViewById(R.id.pick_registration_section);
        pick_location = findViewById(R.id.pickLocation);

        // Text view
        event_title = findViewById(R.id.event_title);
        event_desc = findViewById(R.id.event_desc);
        event_extra_info = findViewById(R.id.event_extra_info);
        selected_category = findViewById(R.id.selected_category);
        start_datetime = findViewById(R.id.start_date);
        end_datetime = findViewById(R.id.end_date);
        register_start_time = findViewById(R.id.start_dateline);
        register_end_time = findViewById(R.id.end_dateline);
        at_location = findViewById(R.id.location);

        // image view
        location_preview = findViewById(R.id.location_preview);

        // loading bar
        avi = findViewById(R.id.avi);
        avi.hide();

        // image row
        LinearLayoutManager linearLayoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        image_row = findViewById(R.id.image_row);
        // configure the image container
        image_row = findViewById(R.id.image_row);
        imageContainerAdapter = new ImageContainerAdapter(this, imageModels);
        image_row.setAdapter(imageContainerAdapter);
        image_row.setLayoutManager(linearLayoutManager);
        add_default_image();

    }

    // add default image container to load in new captured image
    private void add_default_image() {
        ImageModel defaults = new ImageModel("", "", "");
        imageModels.add(defaults);
        imageContainerAdapter.notifyDataSetChanged();
    }

    private void setup_element_listener() {

        pick_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NewEventActivity.this, CategoryActivity.class);
                intent.putExtra("type", "get_event_category");
                startActivityForResult(intent, SELECT_CATEGORY_CODE);
            }
        });

        pick_start_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickDate("start", EVENT_TIME);
            }
        });

        pick_end_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickDate("end", EVENT_TIME);
            }
        });

        pick_register_start_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickDate("start", EVENT_REGISTRATION);
            }
        });

        pick_register_end_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickDate("end", EVENT_REGISTRATION);
            }
        });

        pick_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectLocation();
            }
        });

    }

    public void selectLocation() {
        boolean result = Utility.checklocationPermission(NewEventActivity.this);
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        Intent intent;
        try {
            if (result) {
                intent = builder.build(NewEventActivity.this);
                startActivityForResult(intent, PLACE_PICKER);
            }
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    /* Select camera or image from gallery */
    public void selectImage() {
        final CharSequence[] items = {getString(R.string.take_photo), getString(R.string.gallery),
                getString(R.string.cancel)};
        AlertDialog.Builder builder = new AlertDialog.Builder(NewEventActivity.this);
        builder.setTitle(R.string.add_photo);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = Utility.check_camera_permission(NewEventActivity.this);
                boolean write_external = Utility.check_write_external_permission(NewEventActivity.this);
                if (items[item].equals(getString(R.string.take_photo))) {
                    if (result && write_external)
                        cameraIntent();
                } else if (items[item].equals(getString(R.string.gallery))) {
                    if (result)
                        galleryIntent();
                } else if (items[item].equals(getString(R.string.cancel))) {
                    dialog.dismiss();
                } else {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void cameraIntent() {

        Intent intent;
        if (Build.VERSION.SDK_INT < 24) {
            intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            tempImgFile = Uri.fromFile(createImageFile());
        } else {
            intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            tempImgFile = FileProvider.getUriForFile(NewEventActivity.this, "com.kuchingitsolution.asus.eventmanagement.provider", getOutputMediaFile());
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, tempImgFile);
        startActivityForResult(intent, REQUEST_CAMERA);

    }

    private File createImageFile() {

        long timeStamp = System.currentTimeMillis();
        String imageFileName = "IMG_" + timeStamp;
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES + "/Event Management");

        if (!storageDir.exists())
            storageDir.mkdirs();

        File images = null;
        try {
            images = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );
        } catch (IOException e) {
            e.printStackTrace();
        }

        return images;
    }

    private static File getOutputMediaFile() {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Event Management");

        if (!mediaStorageDir.exists()) {
            boolean is_dir_make = mediaStorageDir.mkdirs();
            if (!is_dir_make) {
                Log.d("error", "null thing");
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date());
        return new File(mediaStorageDir.getPath() + File.separator +
                "IMG_" + timeStamp + ".jpg");
    }

    private void galleryIntent() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
        startActivityForResult(Intent.createChooser(intent, "Select File"), PICK_IMAGE_REQUEST);
    }

    public void pickDate(final String type, final String action) {

        // Get Current Date
        final Calendar c = Calendar.getInstance();
        final int mYear = c.get(Calendar.YEAR);
        final int mMonth = c.get(Calendar.MONTH);
        final int mDay = c.get(Calendar.DAY_OF_MONTH);
        temp_dates = null;

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

//                        date_time = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
                        temp_dates = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                        //*************Call Time Picker Here ********************
                        pickTime(type, action);
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
        datePickerDialog.show();

    }

    private void pickTime(final String type, final String action) {
        // Get Current Time
        final Calendar c = Calendar.getInstance();
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        if (minute == 0)
                            temp_dates = temp_dates + " " + hourOfDay + ":00";
                        else
                            temp_dates = temp_dates + " " + hourOfDay + ":" + minute;

                        if (action.equals(EVENT_TIME)) {
                            if (type.equals("start")) {
                                generate_time_display(start_datetime, "start_datetime");
//                                start_datetime.setText(temp_dates);
//                                generate_content("start_datetime", temp_dates);
                            } else {
                                generate_time_display(end_datetime, "end_datetime");
//                                end_datetime.setText(temp_dates);
//                                generate_content("end_datetime", temp_dates);
                            }
                        } else {
                            if (type.equals("start")) {
                                generate_time_display(register_start_time, "register_start_time");
//                                start_datetime.setText(temp_dates);
//                                generate_content("start_datetime", temp_dates);
                            } else {
                                generate_time_display(register_end_time, "register_end_time");
//                                end_datetime.setText(temp_dates);
//                                generate_content("end_datetime", temp_dates);
                            }
                        }

                    }
                }, mHour, mMinute, false);

        timePickerDialog.show();
    }

    private void generate_time_display(TextView timing, String form_key) {
        timing.setText(temp_dates);
        generate_content(form_key, temp_dates);

        if (data.has("start_datetime") && data.has("end_datetime")) {
            pick_registration_section.setVisibility(View.VISIBLE);
        } else {
            pick_registration_section.setVisibility(View.GONE);
        }
    }

    private void generate_content(String key, String value) {

        try {
            data.put(key, value);
            Log.d("selected data", data.toString() + " ------------------");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private JSONObject getData() {
        return this.data;
    }

    private String get_single_data(String key) {
        return data.optString(key);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PLACE_PICKER && resultCode == RESULT_OK && data != null) {
            Place place = PlacePicker.getPlace(this, data);

            //userLocation = String.format("%s", place.getName());
            String name = String.format("%s", place.getName());
            String selectedLatitute = String.valueOf(place.getLatLng().latitude);
            String selectedLongitute = String.valueOf(place.getLatLng().longitude);
            String addr = place.getAddress().toString();

            String locations = String.format("at: %s", name);
            at_location.setText(locations);
            at_location.setVisibility(View.VISIBLE);
            avi.show();
            getGoogleStaticMap(selectedLatitute, selectedLongitute);
            generate_content("latitude", selectedLatitute);
            generate_content("longitude", selectedLongitute);
            generate_content("location_name", name);
            generate_content("address", addr);
        }

        if (requestCode == SELECT_CATEGORY_CODE && resultCode == RESULT_OK && data != null) {
            selected_category.setText(data.getStringExtra("category_name"));
            generate_content("category_id", data.getStringExtra("category_id"));
            // Toast.makeText(NewEventActivity.this, "Selected id: " + data.getStringExtra("category_id"), Toast.LENGTH_SHORT).show();
        }

        if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK) {

            String path = tempImgFile.getPath();
            String imagePath;
            Bitmap selectedImage = null;

            if (Build.VERSION.SDK_INT > 23) {
                try {
                    selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), tempImgFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else
                selectedImage = BitmapFactory.decodeFile(path);
            imagePath = imageCompressionUtils.saveImage(selectedImage, this);
//            String imagePath = imageCompressionUtils.compressImage(tempImgFile);
            Log.d("TAG", "File Saved::--->" + tempImgFile.getPath());

            imageModels.get(image_position).setPath(imagePath);
            imageModels.get(image_position).setImage_uri("");
            imageContainerAdapter.notifyDataSetChanged();

            if (image_position + 1 >= imageContainerAdapter.getItemCount() || imageContainerAdapter.getItemCount() == 1)
                add_default_image();
        }

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            String path = imageCompressionUtils.compressImage(uri);
            imageModels.get(image_position).setPath(path);
            imageContainerAdapter.notifyDataSetChanged();

            if (image_position + 1 >= imageContainerAdapter.getItemCount() || imageContainerAdapter.getItemCount() == 1)
                add_default_image();
        }
    }

    private void getGoogleStaticMap(String latitute, String longitute) {

        Picasso.with(this).load(getStaticMap(latitute, longitute)).into(location_preview, new com.squareup.picasso.Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError() {
                avi.hide();
            }
        });
        /*Glide.with(this)
            .load(getStaticMap(latitute, longitute))
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        avi.hide();
                        return false;
                    }
                })
                .skipMemoryCache(false)
                .into(location_preview);*/

        location_preview.setVisibility(View.VISIBLE);

    }

    private String getStaticMap(String lat, String lon) {
        return "http://maps.google.com/maps/api/staticmap?center="
                + lat + "," + lon + "&markers=icon:http://tinyurl.com/2ftvtt6%7C" + lat + "," + lon
                + "&zoom=16&size=400x400&sensor=false&key=AIzaSyA4KRaLDjefODrcg1zFXVS2RhB54vetOx8";
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.submit, menu);
        menuitem = menu.findItem(R.id.submit);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            // action with ID action_settings was selected
            case R.id.submit:
                hideSoftKeyboard();
                menuitem.setEnabled(false);
                if (gathering_info()) {
                    Log.d("result", getData().toString());
                    UploadEvent uploadEvent = new UploadEvent(NewEventActivity.this, getData(), imageModels);
                    uploadEvent.execute(Config.UPLOAD_NEW_EVENT);
                } else {
                    menuitem.setEnabled(true);
                }
                break;
            case android.R.id.home:
                onBackPressed();
                break;

        }
        return true;

    }

    private boolean gathering_info() {

        // collect event title
        String title = event_title.getText().toString().trim();
        if (title.isEmpty()) {
            display_status("Event title is required");
            return false;
        } else {
            generate_content("title", title);
        }
        // collect event desc
        String desc = event_desc.getText().toString().trim();
        if (desc.isEmpty()) {
            display_status("Description is required");
            return false;
        } else {
            generate_content("desc", desc);
        }
        // collect event extra info
        String ex_info = event_extra_info.getText().toString().trim();
        if (!ex_info.isEmpty()) {
            generate_content("extra_info", ex_info);
        }

        // collect event category
        if (get_single_data("category_id").isEmpty()) {
            display_status("Please select one category...");
            return false;
        }

        // collect event start time
        if (get_single_data("start_datetime").isEmpty()) {
            display_status("Please set event start time...");
            return false;
        }

        // collect event end time
        if (get_single_data("end_datetime").isEmpty()) {
            display_status("Please set event end time...");
            return false;
        }

        // collect event registration start time
        if (get_single_data("register_start_time").isEmpty()) {
            display_status("Please set event register start time");
            return false;
        }

        // collect event registration end time
        if (get_single_data("register_end_time").isEmpty()) {
            display_status("Please set event register end time");
            return false;
        }

        // collect event image
        if (imageModels.size() == 0 || imageModels.get(0).getPath().isEmpty()) {
            display_status("Please attach one image...");
            return false;
        }

        // collect event venue
        if (get_single_data("location_name").isEmpty()) {
            display_status("Please choose one location...");
            return false;
        }

        return true;

    }

    public void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    public void upload_start() {
        alert = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_upload, null);

        dialog_upload = dialogView.findViewById(R.id.uploading);
        dialog_upload_status = dialogView.findViewById(R.id.uploading_progress);
        status_done = dialogView.findViewById(R.id.status_done);

        alert.setView(dialogView);
        alert.setCancelable(false);
        alert.create();
        ad = alert.show();
    }

    public void update_progress(Integer[] progress) {
        dialog_upload.setProgress(progress[0]);
        dialog_upload_status.setText(String.valueOf(progress[0]));
        if (progress[0] == 100) {
            status_done.setVisibility(View.VISIBLE);
            dialog_upload.setIndeterminate(true);
        }
    }

    public void upload_done(String result) {
        ad.dismiss();
        if (result.equals("success")) {
            Intent intent = new Intent(NewEventActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        } else {
            showMessage(result);
        }
        menuitem.setEnabled(true);
    }

    private void showMessage(String message) {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(NewEventActivity.this);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialogBuilder.show();
    }

    private void display_status(String message) {
        Snackbar snackbar = Snackbar
                .make(findViewById(R.id.new_event), Html.fromHtml("<font color=\"#ffffff\">" + message + "</font>"), Snackbar.LENGTH_LONG);
        View snackBarView = snackbar.getView();
        if (Build.VERSION.SDK_INT >= 23)
            snackBarView.setBackgroundColor(getResources().getColor(R.color.mt_red, null));
        else
            snackBarView.setBackgroundColor(getResources().getColor(R.color.mt_red));
        snackbar.show();
    }


    @Override
    public void onImageClicked(int position) {
        selectImage();
        this.image_position = position;
    }

    @Override
    public void onImageCloseClicked(int position) {
        if (imageContainerAdapter.getItemCount() > 1) {
            imageModels.remove(position);
            imageContainerAdapter.notifyDataSetChanged();
        } else {
            Toast.makeText(this, "No image can be removed", Toast.LENGTH_SHORT).show();
        }
    }

}
