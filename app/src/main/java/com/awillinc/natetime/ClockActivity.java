package com.awillinc.natetime;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

//Testing testing GitHub 1 2 3. -Nate

public class ClockActivity extends AppCompatActivity { //this creates the CLASS ClockActivity

    public static final int LOCATION_PERMISSION_REQUEST_CODE = 101;

    public static final double COLUMBUS_LATITUDE = 39.9612; //#aaroncreated
    public static final double COLUMBUS_LONGITUDE = -82.9988; //"public static final" could be "public final static," public = this string is available outside this method, static = no instance necessary(?), final = value won't change.

    @Override //asserts that the method is going to override something.
    protected void onCreate(Bundle savedInstanceState) { // this prevents from losing previous information(?) Aaron, are you re-writing the already-existing method onCreate here? void means the method doesn't return anything. protected means protected instead of public. Aaron, Why protected here?
        super.onCreate(savedInstanceState); //this entire line says to run the onCreate Activity IN ADDITION TO the existing Activity and not override the whole thing.
        setContentView(R.layout.activity_clock); //#aaroncreated in Android the visual design is created in xml . And each Activity is associated to a design. R means resource. layout means design. Aaron, you must have set a layout or design in activity_clock that you are calling here?

        Button button = findViewById(R.id.refreshButton); //this looks like the opening line to adding the refresh button to the bottom of the screen and saying what to do when it's pressed. Why button three times? Each one fills a required
        button.setOnClickListener(new View.OnClickListener() { //this says to listen for the button to be clicked and
            public void onClick(View v) { //you did not make v. That looks like a user variable, but actually a v is the view that was clicked. Why have this whole onClick method? why not just have the updateTime method execute? probably because it does all the other standard stuff that happens when a button is clicked like change what it looks like?
                updateTime();
            }
        });

        updateTime(); //why is updateTime here fhe second time? it looks like it will execute because it is there under onClick in line 36.

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
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
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    public void updateTime() { //#aaroncreated OK I'm skipping down to here because I see all these "public voids" which are methods, which means you are either making/defining something or doing something. This method clearly falls under the doing something heading.
        // Get the current industrial time
        Calendar c = Calendar.getInstance();
        Date currentTime = c.getTime();

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        String formattedDate = df.format(currentTime); //#aaroncreated c and df. c is the current industrial time. df is a time/date format to work with. #aaroncreated formatteddate, which is the date in format df
        // formattedDate have current date/time

        // Now we display formattedDate value in TextView
        TextView txtView = findViewById(R.id.industrialTimeText); //#aaroncreated ... I KNOW you created industrialTimeText, but where did you define it? Are you defining it here? What
        txtView.setText(String.format(Locale.US, "Current Industrial Time: %s", formattedDate)); //#aaroncreated this text obviously

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Why are we requesting fine location? Coarse should be enough.
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            System.out.println("No location permissions");
            return;
        }

        // Set the default location to be Columbus in case location detection fails
        double latitude = COLUMBUS_LATITUDE;
        double longitude = COLUMBUS_LONGITUDE;

        // Get the location
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {

            // Only using network to use less battery, and we don't need high precision
            // see https://stackoverflow.com/questions/6775257/android-location-providers-gps-or-network-provider
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            }
        }

        // Now we display locations value in TextView
        TextView locationTxtView = findViewById(R.id.locationText);
        locationTxtView.setText(String.format(Locale.US,"Location: %f°, %f°", latitude, longitude));

        // Calculate sunrise and sunset. Source is https://github.com/caarmen/SunriseSunset
        Calendar[] sunriseSunset = ca.rmen.sunrisesunset.SunriseSunset.getSunriseSunset(c, latitude, longitude);
        Date sunrise = sunriseSunset[0].getTime();
        Date sunset = sunriseSunset[1].getTime();

        // Get the sunrise time
        TextView sunriseTxtView = findViewById(R.id.sunriseText);
        sunriseTxtView.setText(String.format("Sunrise: %tR", sunrise));

        // Get the sunset time
        TextView sunsetTxtView = findViewById(R.id.sunsetText);
        sunsetTxtView.setText(String.format("Sunset: %tR", sunset));

        // Calculate post-industrial time
        TextView nateTimeTxtView = findViewById(R.id.nateTimeText);
        TextView dayLengthTxtView = findViewById(R.id.dayLengthText);

        //Get the day length and display it.
        long dlms = sunset.getTime() - sunrise.getTime(); //day length in milliseconds.
        long dlSeconds = dlms / 1000 % 60;
        long dlMinutes = dlms / (60 * 1000) % 60;
        long dlHours = dlms / (60 * 60 * 1000);
        dayLengthTxtView.setText(String.format(Locale.US,"Day Length: %02d:%02d:%02d", dlHours, dlMinutes, dlSeconds));

        if (currentTime.before(sunrise)) {
            long diff = sunrise.getTime() - currentTime.getTime();
            long diffSeconds = diff / 1000 % 60;
            long diffMinutes = diff / (60 * 1000) % 60;
            long diffHours = diff / (60 * 60 * 1000);
            nateTimeTxtView.setText(String.format(Locale.US,"Post-Industrial Time: %02d:%02d:%02d until sunrise", diffHours, diffMinutes, diffSeconds));
        } else if (currentTime.before(sunset)) {
            long diff = currentTime.getTime() - sunrise.getTime();
            long diffSeconds = diff / 1000 % 60;
            long diffMinutes = diff / (60 * 1000) % 60;
            long diffHours = diff / (60 * 60 * 1000);
            nateTimeTxtView.setText(String.format(Locale.US,"Post-Industrial Time: %02d:%02d:%02d after sunrise", diffHours, diffMinutes, diffSeconds));
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
            nateTimeTxtView.setText(String.format(Locale.US,"Post-Industrial Time: %02d:%02d:%02d until sunrise", diffHours, diffMinutes, diffSeconds));
        }
    }
}