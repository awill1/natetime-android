package com.awillinc.natetime.util;

import org.junit.Test;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static org.junit.Assert.*;

public class PitCalendarTest {

    // Locations used for testing
    private static final double AUCKLAND_LATITUDE = -36.840556;
    private static final double AUCKLAND_LONGITUDE = 174.74;
    private static final String AUCKLAND_TIMEZONE = "Pacific/Auckland";
    private static final double BARROW_LATITUDE = 71.290556;
    private static final double BARROW_LONGITUDE = -156.788611;
    private static final String BARROW_TIMEZONE = "US/Alaska";
    private static final double COLUMBUS_LATITUDE = 39.9612;
    private static final double COLUMBUS_LONGITUDE = -82.9988;
    private static final String COLUMBUS_TIMEZONE = "US/Eastern";
    private static final double DENVER_LATITUDE = 39.76185;
    private static final double DENVER_LONGITUDE = -104.881105;
    private static final String DENVER_TIMEZONE = "US/Mountain";
    private static final double HELSINKI_LATITUDE = 60.170833;
    private static final double HELSINKI_LONGITUDE = 24.9375;
    private static final String HELSINKI_TIMEZONE = "Europe/Helsinki";
    private static final double KAMPALA_LATITUDE = 0.313611;
    private static final double KAMPALA_LONGITUDE = 32.581111;
    private static final String KAMPALA_TIMEZONE = "Africa/Kampala";
    private static final double KIRITIMATI_ISLAND_LATITUDE = 1.866667;
    private static final double KIRITIMATI_ISLAND_LONGITUDE = -157.4;
    private static final String KIRITIMATI_TIMEZONE = "Pacific/Kiritimati";
    private static final double LONDON_LATITUDE = 51.507222;
    private static final double LONDON_LONGITUDE = -118.25;
    private static final String LONDON_TIMEZONE = "Europe/London";
    private static final double LOS_ANGELES_LATITUDE = 34.05;
    private static final double LOS_ANGELES_LONGITUDE = -0.1275;
    private static final String LOS_ANGELES_TIMEZONE = "US/Pacific";
    private static final double MADRID_LATITUDE = 40.383333;
    private static final double MADRID_LONGITUDE = -3.716667;
    private static final String MADRID_TIMEZONE = "Europe/London";
    private static final double NORTH_POLE_LATITUDE = 90.0;
    private static final double NORTH_POLE_LONGITUDE = 0.0;
    private static final String NORTH_POLE_TIMEZONE = "GMT";
    private static final double PHOENIX_LATITUDE = 33.45;
    private static final double PHOENIX_LONGITUDE = -112.066667;
    private static final String PHOENIX_TIMEZONE = "US/Arizona";
    private static final double QUITO_LATITUDE = -0.233333;
    private static final double QUITO_LONGITUDE = -78.516667;
    private static final String QUITO_TIMEZONE = "America/Guayaquil";
    private static final double RIO_DE_JANEIRO_LATITUDE = -22.908333;
    private static final double RIO_DE_JANEIRO_LONGITUDE = -43.196389;
    private static final String RIO_DE_JANEIRO_TIMEZONE = "America/Sao_Paulo";
    private static final double SAINT_PETERSBURG_LATITUDE = 59.9375;
    private static final double SAINT_PETERSBURG_LONGITUDE = 30.308611;
    private static final String SAINT_PETERSBURG_TIMEZONE = "Europe/Moscow";
    private static final double SOUTH_POLE_LATITUDE = -90.0;
    private static final double SOUTH_POLE_LONGITUDE = 0.0;
    private static final String SOUTH_POLE_TIMEZONE = "GMT";
    private static final double ST_LOUIS_LATITUDE = 59.9375;
    private static final double ST_LOUIS_LONGITUDE = 30.308611;
    private static final String ST_LOUIS_TIMEZONE = "US/Central";
    // Made up points
    private static final double NULL_ISLAND_LATITUDE = 0.0;
    private static final double NULL_ISLAND_LONGITUDE = 0.0;
    private static final String NULL_ISLAND_TIMEZONE = "GMT";
    private static final double NOT_NULL_ISLAND_LATITUDE = 0.0;
    private static final double NOT_NULL_ISLAND_LONGITUDE = 180.0;
    private static final String NOT_NULL_ISLAND_TIMEZONE = "Etc/GMT+12";

    @Test
    public void ColumbusTest() {
        verifyPit(COLUMBUS_TIMEZONE, 2018, Calendar.OCTOBER, 21, 1, 31, 12, COLUMBUS_LATITUDE, COLUMBUS_LONGITUDE, "-06:18:40/13:02:51");
        verifyPit(COLUMBUS_TIMEZONE, 2018, Calendar.OCTOBER, 21, 12, 54, 47, COLUMBUS_LATITUDE, COLUMBUS_LONGITUDE, "+05:04:54/10:55:45");
        verifyPit(COLUMBUS_TIMEZONE, 2018, Calendar.OCTOBER, 21, 23, 10, 30, COLUMBUS_LATITUDE, COLUMBUS_LONGITUDE, "-08:40:28/13:05:21");
    }


    @Test
    public void LondonTest() {
        verifyPit(LONDON_TIMEZONE, 2018, Calendar.OCTOBER, 21, 1, 31, 12, LONDON_LATITUDE, LONDON_LONGITUDE, "+10:04:22/10:24:09");
        verifyPit(LONDON_TIMEZONE, 2018, Calendar.OCTOBER, 21, 12, 54, 47, LONDON_LATITUDE, LONDON_LONGITUDE, "-02:33:46/13:37:35");
        verifyPit(LONDON_TIMEZONE, 2018, Calendar.OCTOBER, 21, 23, 10, 30, LONDON_LATITUDE, LONDON_LONGITUDE, "+07:41:56/10:20:22");
    }


    @Test
    public void NorthPoleTest() {
        verifyPit(NORTH_POLE_TIMEZONE, 2018, Calendar.OCTOBER, 14, 23, 10, 30, NORTH_POLE_LATITUDE, NORTH_POLE_LONGITUDE, "4000:00:00/4383:00:00");
    }

    public void verifyPit(String timeZone, int year, int month, int date, int hourOfDay, int minute, int second, double latitude, double longitude, String expected) {
        TimeZone tz = TimeZone.getTimeZone(timeZone);
        Calendar c = Calendar.getInstance(tz);
        c.set(year, month, date, hourOfDay, minute, second);
        PitCalendar pc = new PitCalendar(latitude, longitude, c);

        assertEquals(expected, pc.toString());
    }
}