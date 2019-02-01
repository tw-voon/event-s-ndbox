package com.kuchingitsolution.asus.eventmanagement.config;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Helper {

    public static void showMessage(Context context){
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle("Logout");
        alertDialogBuilder.setMessage("Are you sure want to remove this bookmark ?");
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialogBuilder.show();
    }

    public static long get_time_milli(String time){
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        long time_milli = 0;

        try {
            Date date = dateFormat.parse(time);
            time_milli = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return time_milli;
    }

    public static String get_date(String type, String time){

        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        String day = null;

        switch (type){
            case "day":
                try {
                    SimpleDateFormat dateFormat2 = new SimpleDateFormat(
                            "dd", Locale.getDefault());
                    Date date = dateFormat.parse(time);
                    day = dateFormat2.format(date);
                } catch (ParseException e){
                    e.printStackTrace();
                }
                break;

            case "month":
                try {
                    SimpleDateFormat dateFormat3 = new SimpleDateFormat(
                            "MMM", Locale.getDefault());
                    Date date = dateFormat.parse(time);
                    day = dateFormat3.format(date);
                } catch (ParseException e){
                    e.printStackTrace();
                }
                break;

            case "year":
                try {
                    SimpleDateFormat dateFormat3 = new SimpleDateFormat(
                            "yyyy", Locale.getDefault());
                    Date date = dateFormat.parse(time);
                    day = dateFormat3.format(date);
                } catch (ParseException e){
                    e.printStackTrace();
                }
                break;

            case "full":
                try {
                    SimpleDateFormat dateFormat3 = new SimpleDateFormat(
                            "yyyy-MM-dd", Locale.getDefault());
                    Date date = dateFormat.parse(time);
                    day = dateFormat3.format(date);
                } catch (ParseException e){
                    e.printStackTrace();
                }
                break;
        }

        return day;
    }

    public static String get_date(String time){

        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        String day = null;

        SimpleDateFormat dateFormat2 = new SimpleDateFormat(
                "dd-MM-yyyy, HH:mm a", Locale.getDefault());
        Date date = null;

        try {
            date = dateFormat.parse(time);
            day = dateFormat2.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return day;
    }

    public static String get_time(String time){

        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        SimpleDateFormat dateFormat2 = new SimpleDateFormat(
                "E, d-MMM-yyyy HH:mm", Locale.getDefault());

        try {
            Date date = dateFormat.parse(time);
            return String.valueOf(dateFormat2.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return time;

    }

    public static String get_time_range(String time){

        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        SimpleDateFormat dateFormat2 = new SimpleDateFormat(
                "EEE, d MMM yyyy HH:mm:ss", Locale.getDefault());

        try {
            Date date = dateFormat.parse(time);
            Date now = dateFormat.parse(getDateTime());
            long diff = now.getTime() - date.getTime();

            long diffMinutes = diff / (60 * 1000) % 60;
            long diffHours = diff / (60 * 60 * 1000) % 24;
            long diffDays = diff / (24 * 60 * 60 * 1000);


            if(diffDays>0) {
                return String.valueOf(dateFormat2.format(date));
            } else if(diffHours>=1) {
                return String.format("%s hour ago", diffHours);
            } else if(diffMinutes<60) {
                return String.format("%s minute ago",diffMinutes);
            } else {
                return String.valueOf(0);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return time;

    }

    private static String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }
}
