package org.alexdev.kepler.util;

import org.alexdev.kepler.log.Log;
import org.alexdev.kepler.messages.outgoing.rooms.items.SHOWPROGRAM;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    private static final String LONG_DATE = "dd-MM-yyyy HH:mm:ss";
    private static final String SHORT_DATE = "dd-MM-yyyy";

    /**
     * Returns the current date as "dd-MM-yyyy"
     * @return the date as string
     */
    public static String getShortDate() {
        try {
            Date date = new Date();
            return new SimpleDateFormat(SHORT_DATE).format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Returns the current date as "dd-MM-yyyy"
     * @return the date as string
     */
    public static String getShortDate(long time) {
        try {
            Date date = new Date();
            return new SimpleDateFormat(SHORT_DATE).format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Gets the current formatted date as string.
     *
     * @return the date as string
     */
    public static String getDateAsString() {
        try {
            Date date = new Date();
            return new SimpleDateFormat(LONG_DATE).format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Gets the date given by unix timestamp as string.
     *
     * @param time the unix timestamp
     * @return the date as string
     */
    public static String getDateAsString(long time) {
        try {
            Date date = new Date();
            date.setTime(time * 1000);
            return new SimpleDateFormat(LONG_DATE).format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Gets the readable timestamp.
     *
     * @param timestamp the timestamp
     * @return the readable timestamp
     */
    public static String getReadableTimestamp(long timestamp) {
        try {
            long different = System.currentTimeMillis() - (timestamp * 1000);

            long secondsInMilli = 1000;
            long minutesInMilli = secondsInMilli * 60;
            long hoursInMilli = minutesInMilli * 60;
            long daysInMilli = hoursInMilli * 24;

            long elapsedDays = different / daysInMilli;
            different = different % daysInMilli;

            long elapsedHours = different / hoursInMilli;
            different = different % hoursInMilli;

            long elapsedMinutes = different / minutesInMilli;
            different = different % minutesInMilli;

            long elapsedSeconds = different / secondsInMilli;
            return elapsedDays + " days, " + elapsedHours + " hours, " + elapsedMinutes + " minutes, " + elapsedSeconds + " seconds";

        } catch (Exception e){
            Log.getErrorLogger().error("Error occurred: ", e);
        }

        return null;
    }

    /**
     * Convert seconds to readable English.
     *
     * @param input the seconds input
     * @return the seconds represented as words
     */
    public static String getReadableSeconds(long input) {
        try {
            //long different = System.currentTimeMillis() - (timestamp * 1000);
            long numberOfDays;
            long numberOfHours;
            long numberOfMinutes;
            long numberOfSeconds;

            numberOfDays = input / 86400;
            numberOfHours = (input % 86400 ) / 3600 ;
            numberOfMinutes = ((input % 86400 ) % 3600 ) / 60;
            numberOfSeconds = ((input % 86400 ) % 3600 ) % 60  ;

            return numberOfDays + " days, " + numberOfHours + " hours, " + numberOfMinutes + " minutes, " + numberOfSeconds + " seconds";

        } catch (Exception e){
            Log.getErrorLogger().error("Error occurred: ", e);
        }

        return null;
    }



    /**
     * Gets the current time in seconds.
     *
     * @return the current time in seconds
     */
    public static int getCurrentTimeSeconds() {
        return (int) (System.currentTimeMillis() / 1000);
    }
}
