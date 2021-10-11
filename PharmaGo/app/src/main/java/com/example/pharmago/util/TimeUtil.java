package com.example.pharmago.util;

import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtil {

    private static Calendar calendar;

    public static long parseIntoTimeInMillis(int year, int month, int day) {

        if (calendar == null) calendar = Calendar.getInstance();

        calendar.set(year, month, day, 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTimeInMillis();
    }

    public static String getFormattedDayFromMiliTime(long timeInMillies) {
        Date date = new Date(timeInMillies);

        SimpleDateFormat format = new SimpleDateFormat("dd");

        String dateString = format.format(date);

        return dateString;

    }


    public static String getAmPmtimeByMillies(long timeInMillies) {
        String timeS = "";

        SimpleDateFormat format = new SimpleDateFormat("hh:mm aaa");

        Date date = new Date(timeInMillies);

        timeS = format.format(date);

        return timeS;
    }

    public static String getFormattedDateFromMiliTime(long timeInMillies) {
        Date date = new Date(timeInMillies);

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        String dateString = format.format(date);

        return dateString;

    }

    public static long getFormattedDateFromMiliTime(String dateString) {
        long millies = 0;

        try {

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Date d = dateFormat.parse(dateString);

            millies = d.getTime();
        } catch (Exception e) {
            Log.e("ERROR", e.toString());
        }

        return millies;
    }

    public static String getDateFormatNbc(long timeInMillies) {
        String timeS = "";

        SimpleDateFormat format = new SimpleDateFormat("dd MMMM yyyy");

        Date date = new Date(timeInMillies);

        timeS = format.format(date);

        return timeS;
    }

    public static String getTimeFormatCWM(long timeInMillies)
    {
        String timeS = "";

        SimpleDateFormat format = new SimpleDateFormat("EEEE , dd MMM yyyy  hh:mm aaa");

        Date date = new Date(timeInMillies);

        timeS = format.format(date);

        return timeS;
    }
}
