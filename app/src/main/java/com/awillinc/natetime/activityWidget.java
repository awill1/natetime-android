package com.awillinc.natetime;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.support.v4.app.ActivityCompat;
import android.location.Location;
import android.location.LocationManager;

import android.widget.RemoteViews;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Implementation of App Widget functionality.
 */
public class activityWidget extends AppWidgetProvider {

    public static final int LOCATION_PERMISSION_REQUEST_CODE = 101;

    public static final double COLUMBUS_LATITUDE = 39.9612; //#aaroncreated
    public static final double COLUMBUS_LONGITUDE = -82.9988; //"public static final" could be "public final static," public = this string is available outside this method, static = no instance necessary(?), final = value won't change.

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {


        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.activity_widget);
        //views.setTextViewText(R.id.timeWidgetText, widgetText + " hello world");
        views.setTextViewText(R.id.timeWidgetText, PitHelper.getPitString(Calendar.getInstance(), COLUMBUS_LATITUDE, COLUMBUS_LONGITUDE));

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }


    public void updateTime(Context context) {
        // Get the current industrial time
        Calendar c = Calendar.getInstance();
        Date currentTime = c.getTime();

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        String formattedDate = df.format(currentTime); //#aaroncreated c and df. c is the current industrial time. df is a time/date format to work with. #aaroncreated formatteddate, which is the date in format df
        // formattedDate have current date/time

//         if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // Why are we requesting fine location? Coarse should be enough.
//            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
//            System.out.println("No location permissions");
//            return;
//        }

        // Set the default location to be Columbus in case location detection fails
        double latitude = COLUMBUS_LATITUDE;
        double longitude = COLUMBUS_LONGITUDE;

//        // Get the location
//        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        if (locationManager != null) {
//
//            // Only using network to use less battery, and we don't need high precision
//            // see https://stackoverflow.com/questions/6775257/android-location-providers-gps-or-network-provider
//            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//
//            if (location != null) {
//                latitude = location.getLatitude();
//                longitude = location.getLongitude();
//            }
//        }


        // Calculate sunrise and sunset. Source is https://github.com/caarmen/SunriseSunset
        Calendar[] sunriseSunset = ca.rmen.sunrisesunset.SunriseSunset.getSunriseSunset(c, latitude, longitude);
        Date sunrise = sunriseSunset[0].getTime();
        Date sunset = sunriseSunset[1].getTime();

        // Calculate post-industrial time
        // TextView dayLengthTxtView = findViewById(R.id.dayLengthText);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.activity_widget);

        //Get the day length and display it.
        long dlms = sunset.getTime() - sunrise.getTime(); //day length in milliseconds.
        long dlSeconds = dlms / 1000 % 60;
        long dlMinutes = dlms / (60 * 1000) % 60;
        long dlHours = dlms / (60 * 60 * 1000);
        // dayLengthTxtView.setText(String.format(Locale.US,"Day Length: %02d:%02d:%02d", dlHours, dlMinutes, dlSeconds));

        if (currentTime.before(sunrise)) {
            long diff = sunrise.getTime() - currentTime.getTime();
            long diffSeconds = diff / 1000 % 60;
            long diffMinutes = diff / (60 * 1000) % 60;
            long diffHours = diff / (60 * 60 * 1000);
            views.setTextViewText(R.id.timeWidgetText, String.format(Locale.US,"Sunrise - %02d:%02d:%02d", diffHours, diffMinutes, diffSeconds));
        } else if (currentTime.before(sunset)) {
            long diff = currentTime.getTime() - sunrise.getTime();
            long diffSeconds = diff / 1000 % 60;
            long diffMinutes = diff / (60 * 1000) % 60;
            long diffHours = diff / (60 * 60 * 1000);
            views.setTextViewText(R.id.timeWidgetText, String.format(Locale.US,"Sunrise + %02d:%02d:%02d", diffHours, diffMinutes, diffSeconds));
        } else {
            // Get tomorrow's sunrise time then calculate the time until sunrise
            Calendar tomorrow = Calendar.getInstance();
            // TODO: Test tomorrow being Jan 1
            tomorrow.add(Calendar.DAY_OF_YEAR, 1);
            Calendar[] tomorrowSunriseSunset = ca.rmen.sunrisesunset.SunriseSunset.getSunriseSunset(tomorrow, latitude, longitude);

            Date tomorrowSunrise = tomorrowSunriseSunset[0].getTime();
            Date tomorrowSunset = tomorrowSunriseSunset[1].getTime();
            long diff = tomorrowSunrise.getTime() - currentTime.getTime();
            long diffSeconds = diff / 1000 % 60;
            long diffMinutes = diff / (60 * 1000) % 60;
            long diffHours = diff / (60 * 60 * 1000);
            views.setTextViewText(R.id.timeWidgetText, String.format(Locale.US,"Sunrise - %02d:%02d:%02d", diffHours, diffMinutes, diffSeconds));
        }
    }
}

