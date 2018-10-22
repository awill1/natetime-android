package com.awillinc.natetime;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class PitHelper {

    public static String getPitString(Calendar c, double latitude, double longitude) {
        Date currentTime = c.getTime();
        // Calculate sunrise and sunset. Source is https://github.com/caarmen/SunriseSunset
        Calendar[] sunriseSunset = ca.rmen.sunrisesunset.SunriseSunset.getSunriseSunset(c, latitude, longitude);
        Date sunrise = sunriseSunset[0].getTime();
        Date sunset = sunriseSunset[1].getTime();

        if (currentTime.before(sunrise)) {
            long diff = sunrise.getTime() - currentTime.getTime();
            long diffSeconds = diff / 1000 % 60;
            long diffMinutes = diff / (60 * 1000) % 60;
            long diffHours = diff / (60 * 60 * 1000);
            return String.format(Locale.US,"Sunrise - %02d:%02d:%02d", diffHours, diffMinutes, diffSeconds);
        } else if (currentTime.before(sunset)) {
            long diff = currentTime.getTime() - sunrise.getTime();
            long diffSeconds = diff / 1000 % 60;
            long diffMinutes = diff / (60 * 1000) % 60;
            long diffHours = diff / (60 * 60 * 1000);
            return String.format(Locale.US,"Sunrise + %02d:%02d:%02d", diffHours, diffMinutes, diffSeconds);
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
            return String.format(Locale.US,"Sunrise - %02d:%02d:%02d", diffHours, diffMinutes, diffSeconds);
        }
    }

    public static String getDayLengthString(Calendar c, double latitude, double longitude) {
        // Calculate sunrise and sunset. Source is https://github.com/caarmen/SunriseSunset
        Calendar[] sunriseSunset = ca.rmen.sunrisesunset.SunriseSunset.getSunriseSunset(c, latitude, longitude);
        Date sunrise = sunriseSunset[0].getTime();
        Date sunset = sunriseSunset[1].getTime();

        //Get the day length and display it.
        long dlms = sunset.getTime() - sunrise.getTime(); //day length in milliseconds.
        long dlSeconds = dlms / 1000 % 60;
        long dlMinutes = dlms / (60 * 1000) % 60;
        long dlHours = dlms / (60 * 60 * 1000);
        return String.format(Locale.US,"Day Length: %02d:%02d:%02d", dlHours, dlMinutes, dlSeconds);


    }
}
