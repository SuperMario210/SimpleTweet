package com.codepath.apps.restclienttemplate.util;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Utility class for formatting dates
 */
public class DateFormatter {
    /**
     * @return formatted time the tweet was posted
     * @param rawJsonDate createAt string from the tweet
     */
    public static String formatTime(String rawJsonDate) {
        // Parse the date into a SimpleDateFormat object
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            // Format the SimpleDateFormat to get the time
            Date date = sf.parse(rawJsonDate);
            String timeFormat = "hh:mm aa";
            SimpleDateFormat tf = new SimpleDateFormat(timeFormat, Locale.ENGLISH);
            relativeDate = tf.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }

    /**
     * @return formatted date the tweet was posted
     * @param rawJsonDate createAt string from the tweet
     */
    public static String formatDate(String rawJsonDate) {
        // Parse the date into a SimpleDateFormat object
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            // Format the SimpleDateFormat to get the date
            Date date = sf.parse(rawJsonDate);
            String timeFormat = "dd MMM yy";
            SimpleDateFormat tf = new SimpleDateFormat(timeFormat, Locale.ENGLISH);
            relativeDate = tf.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }

    /**
     * @return formatted timestamp to display on the timeline
     * @param rawJsonDate createAt string from the tweet
     */
    public static String formatTimestamp(String rawJsonDate) {
        // Parse the date into a SimpleDateFormat object
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        Date date;
        long nowMs, thenMs;
        try {
            // Get the represented date in milliseconds
            date = sf.parse(rawJsonDate);
            thenMs = date.getTime();
            nowMs = System.currentTimeMillis();
        } catch (ParseException e) {
            Log.e("DateFormatter", "Couldn't parse date", e);
            return "";
        }

        // Calculate difference in milliseconds
        long diff = nowMs - thenMs;

        // Calculate difference in seconds
        long diffSeconds = diff / (1000);
        long diffMinutes = diff / (60 * 1000);
        long diffHours = diff / (60 * 60 * 1000);
        long diffDays = diff / (24 * 60 * 60 * 1000);

        // Formate timestamp
        if (diffSeconds < 10) {
            return "now";
        } else if (diffSeconds < 60) {
            return diffSeconds + "s";
        } else if (diffMinutes < 60) {
            return diffMinutes + "m";
        } else if (diffHours < 24) {
            return diffHours + "h";
        } else if (diffDays < 7) {
            return diffDays + "d";
        } else if (date.getYear() == new Date().getYear()){
            SimpleDateFormat todate = new SimpleDateFormat("MMM dd",
                    Locale.ENGLISH);
            return todate.format(date);
        } else {
            SimpleDateFormat todate = new SimpleDateFormat("dd MMM yy",
                    Locale.ENGLISH);
            return todate.format(date);
        }
    }
}
