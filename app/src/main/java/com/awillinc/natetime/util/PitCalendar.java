package com.awillinc.natetime.util;

import android.location.Location;
import ca.rmen.sunrisesunset.SunriseSunset;
import java.util.Calendar;
import java.util.Locale;

/**
 * Represents a post-industrial time calendar
 */
public class PitCalendar  {

    private Calendar m_industrialTime;
    private double m_latitude;
    private double m_longitude;
    private Calendar previousSunrise = null;
    private Calendar previousSunset = null;
    private Calendar nextSunrise = null;
    private Calendar nextSunset = null;

    private int fields[];

    // Only need to calculate to seconds because that is all the supporting library can do
    public final static int PIT_DAYTIME_NIGHTTIME = 0;
    public final static int PIT_HOUR = 1;
    public final static int PIT_MINUTE = 2;
    public final static int PIT_SECOND = 3;
    public final static int PIT_PHASE_HOUR = 4;
    public final static int PIT_PHASE_MINUTE = 5;
    public final static int PIT_PHASE_SECOND = 6;
    protected final static  int FIELD_COUNT = 7;

    public final static int PIT_DAYTIME = 0;
    public final static int PIT_NIGHTTIME = 1;


    /**
     * Creates a new instance of the PitCalendar using the current time at a location
     * @param latitude The latitude of the location for the post-industrial calendar
     * @param longitude The longitude of the location for the post-industrial calendar
     */
    public PitCalendar(double latitude, double longitude, Location location) {
        this(latitude, longitude, Calendar.getInstance());
    }

    /**
     * Creates a new instance of the PitCalendar at a specific time and location
     * @param latitude The latitude of the location for the post-industrial calendar
     * @param longitude The longitude of the location for the post-industrial calendar
     * @param industrialTime The industrial time for the post-industrial calendar
     */
    public PitCalendar(double latitude, double longitude, Calendar industrialTime) {
        m_industrialTime = industrialTime;
        m_latitude = latitude;
        m_longitude = longitude;
        initialize();
    }

    /**
     * Initializes the fields
     */
    private void initialize() {
        fields = new int[FIELD_COUNT];

        // The SunriseSunset library does not handle only sunrise or only sunset days properly.
        //  It returns null for both. This causes a problem near the north pole where there are no
        // sunrise and sunset days. This function hangs as it calculated forward and backward
        // through time looking for sunrises and sunsets. I am adding some counters to prevent this
        // from occurring
        int max_days_to_check = 366;
        int previousDateCounter = 0;
        int nextDateCounter = 0;

        // Calculate the previous sunrise and sunset
        Calendar previousTestDate = (Calendar)m_industrialTime.clone();
        while ((previousSunrise == null ||  previousSunset == null) && previousDateCounter < max_days_to_check) {
            Calendar[] previousTestResult = SunriseSunset.getSunriseSunset(previousTestDate, m_latitude, m_longitude);
            if (previousTestResult != null && previousTestResult[0] != null) {
                if (previousSunrise == null && previousTestResult[0].getTimeInMillis() <= m_industrialTime.getTimeInMillis()) {
                    previousSunrise = previousTestResult[0];
                }
            }
            if (previousTestResult != null && previousTestResult[1] != null) {
                if (previousSunset == null && previousTestResult[1].getTimeInMillis() < m_industrialTime.getTimeInMillis()) {
                    previousSunset = previousTestResult[1];
                }
            }
            previousTestDate.add(Calendar.DATE, -1);
            previousDateCounter++;
        }

        // Calculate the next sunrise and sunset
        Calendar nextTestDate = (Calendar)m_industrialTime.clone();
        while ((nextSunrise == null ||  nextSunset == null) && nextDateCounter < max_days_to_check) {
            Calendar[] nextTestResult = SunriseSunset.getSunriseSunset(nextTestDate, m_latitude, m_longitude);
            if (nextTestResult != null && nextTestResult[0] != null) {
                if (nextSunrise == null && nextTestResult[0].getTimeInMillis() >= m_industrialTime.getTimeInMillis()) {
                    nextSunrise = nextTestResult[0];
                }
            }
            if (nextTestResult != null && nextTestResult[1] != null) {
                if (nextSunset == null && nextTestResult[1].getTimeInMillis() > m_industrialTime.getTimeInMillis()) {
                    nextSunset = nextTestResult[1];
                }
            }
            nextTestDate.add(Calendar.DATE, 1);
            nextDateCounter++;
        }

        // Workaround for the bad polar problem from the library
        if (previousSunrise == null || previousSunset == null || nextSunrise == null || nextSunset == null)
        {
            // This library has a bug around the poles in the days following the equinoxes. Just
            // go with it for now.
            boolean isDay = SunriseSunset.isDay(m_industrialTime, m_latitude, m_longitude);
            if (isDay) {
                fields[PIT_DAYTIME_NIGHTTIME] = PIT_DAYTIME;
                // Set the time to be the time since midnight or to midnight
                fields[PIT_HOUR] = m_industrialTime.get(Calendar.HOUR_OF_DAY);
                fields[PIT_MINUTE] = m_industrialTime.get(Calendar.MINUTE);
                fields[PIT_SECOND] = m_industrialTime.get(Calendar.SECOND);
            }
            else {
                fields[PIT_DAYTIME_NIGHTTIME] = PIT_NIGHTTIME;
                // Set the time to be the time since midnight or to midnight
                fields[PIT_HOUR] = 23 - m_industrialTime.get(Calendar.HOUR_OF_DAY);
                fields[PIT_MINUTE] = 59 - m_industrialTime.get(Calendar.MINUTE);
                fields[PIT_SECOND] = 59 - m_industrialTime.get(Calendar.SECOND);
            }
            // Just use the 24 hour max day
            fields[PIT_PHASE_HOUR] = 24;
            fields[PIT_PHASE_MINUTE] = 0;
            fields[PIT_PHASE_SECOND] = 0;

        }
        else {
            // The way the code should be
            // Calculate and set fields
            long pitTime;
            long phaseDuration;
            if (previousSunrise.getTimeInMillis() > previousSunset.getTimeInMillis()) {
                // It's daytime
                fields[PIT_DAYTIME_NIGHTTIME] = PIT_DAYTIME;

                // Time fields
                pitTime = m_industrialTime.getTimeInMillis() - previousSunrise.getTimeInMillis();
                phaseDuration = nextSunset.getTimeInMillis() - previousSunrise.getTimeInMillis();
            } else {
                // It's nighttime
                fields[PIT_DAYTIME_NIGHTTIME] = PIT_NIGHTTIME;

                pitTime = nextSunrise.getTimeInMillis() - m_industrialTime.getTimeInMillis();
                phaseDuration = nextSunrise.getTimeInMillis() - previousSunset.getTimeInMillis();
            }

            // Time fields
            fields[PIT_HOUR] = (int) (pitTime / (60 * 60 * 1000));
            fields[PIT_MINUTE] = (int) (pitTime / (60 * 1000) % 60);
            fields[PIT_SECOND] = (int) (pitTime / 1000 % 60);

            // Phase fields
            fields[PIT_PHASE_HOUR] = (int) (phaseDuration / (60 * 60 * 1000));
            fields[PIT_PHASE_MINUTE] = (int) (phaseDuration / (60 * 1000) % 60);
            fields[PIT_PHASE_SECOND] = (int) (phaseDuration / 1000 % 60);
        }
    }

    /**
     * Gets the industrial time
     * @return The industrial time
     */
    public Calendar getIndustrialTime() {
        return m_industrialTime;
    }

    /**
     * Returns the value of the given calendar field.
     * @param field the given calendar field.
     * @return the value for the given calendar field.
     */
    public int get(int field) {
        return fields[field];
    }

    /**
     * Return a string representation of this calendar. This method
     * is intended to be used only for debugging purposes, and the
     * format of the returned string may vary between implementations.
     * The returned string may be empty but may not be <code>null</code>.
     *
     * @return  a string representation of this calendar.
     */
    public String toString() {
        // Format is aHH:mm:ss/HH:mm:ss
        String phaseIndicator = get(PIT_DAYTIME_NIGHTTIME) == PIT_DAYTIME ? "+" : "-";
        String pitHour = String.format(Locale.US,"%02d", get(PIT_HOUR));
        String pitMinute = String.format(Locale.US,"%02d", get(PIT_MINUTE));
        String pitSecond = String.format(Locale.US,"%02d", get(PIT_SECOND));
        String phaseHour = String.format(Locale.US,"%02d", get(PIT_PHASE_HOUR));
        String phaseMinute = String.format(Locale.US,"%02d", get(PIT_PHASE_MINUTE));
        String phaseSecond = String.format(Locale.US,"%02d", get(PIT_PHASE_SECOND));
        return String.format(Locale.US, "%s%s:%s:%s/%s:%s:%s", phaseIndicator, pitHour, pitMinute, pitSecond, phaseHour, phaseMinute, phaseSecond);
    }
    // =======================privates===============================

}
