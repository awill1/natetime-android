package com.awillinc.natetime;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

//Testing testing GitHub 1 2 3. -Nate

public class ClockActivity extends AppCompatActivity {

    public static final int LOCATION_PERMISSION_REQUEST_CODE = 101;

    public static final double COLUMBUS_LATITUDE = 39.9612;
    public static final double COLUMBUS_LONGITUDE = -82.9988;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock);

        Button button = (Button) findViewById(R.id.refreshButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                updateTime();
            }
        });

        updateTime();

    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    updateTime();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    public void updateTime() {
        // Get the current industrial time
        Calendar c = Calendar.getInstance();
        Date currentTime = c.getTime();

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = df.format(currentTime);
        // formattedDate have current date/time

        // Now we display formattedDate value in TextView
        TextView txtView = (TextView) findViewById(R.id.industrialTimeText);
        txtView.setText("Current Industrial Time: " + formattedDate);

        // Get the location
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        String provideGps = locationManager.GPS_PROVIDER;
        String provideNetwork = locationManager.NETWORK_PROVIDER;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            System.out.println("No location permissions");
            return;
        }

        double latitude = COLUMBUS_LATITUDE;
        double longitude = COLUMBUS_LONGITUDE;

        // TODO: Use device location instead of hardcoded version
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (location == null) {
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }

        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }

        // Now we display locations value in TextView
        TextView locationTxtView = (TextView) findViewById(R.id.locationText);
        locationTxtView.setText(String.format("Location: %f° Lat, %f° Lon" , latitude, longitude));

        // Calculate sunrise and sunset. Source is https://github.com/caarmen/SunriseSunset
        Calendar[] sunriseSunset = ca.rmen.sunrisesunset.SunriseSunset.getSunriseSunset(c, latitude, longitude);
        Date sunrise = sunriseSunset[0].getTime();
        Date sunset = sunriseSunset[1].getTime();
        // Get the sunrise time
        TextView sunriseTxtView = (TextView) findViewById(R.id.sunriseText);
        sunriseTxtView.setText(String.format("Sunrise: %tR" , sunrise));

        // Get the sunset time
        TextView sunsetTxtView = (TextView) findViewById(R.id.sunsetText);
        sunsetTxtView.setText(String.format("Sunset: %tR" , sunset));

        // Calculate nate time
        TextView nateTimeTxtView = (TextView) findViewById(R.id.nateTimeText);
        if (currentTime.before(sunrise)) {
            long diff = sunrise.getTime() - currentTime.getTime();
            long diffSeconds = diff / 1000 % 60;
            long diffMinutes = diff / (60 * 1000) % 60;
            long diffHours = diff / (60 * 60 * 1000);
            nateTimeTxtView.setText(String.format("Nate Time: %02d:%02d:%02d until sunrise", diffHours, diffMinutes, diffSeconds));
        }
        else if (currentTime.before(sunset)) {
            long diff = currentTime.getTime() - sunrise.getTime();
            long diffSeconds = diff / 1000 % 60;
            long diffMinutes = diff / (60 * 1000) % 60;
            long diffHours = diff / (60 * 60 * 1000);
            nateTimeTxtView.setText(String.format("Nate Time: %02d:%02d:%02d after sunrise", diffHours, diffMinutes, diffSeconds));
        }
        else {
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
            nateTimeTxtView.setText(String.format("Nate Time: %02d:%02d:%02d until sunrise", diffHours, diffMinutes, diffSeconds));
        }

    }
}
