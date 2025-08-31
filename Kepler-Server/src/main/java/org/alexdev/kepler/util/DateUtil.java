package org.alexdev.kepler.util;

import org.alexdev.kepler.log.Log;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.TimeZone;

public class DateUtil {
    public static final String LONG_DATE = "dd-MM-yyyy HH:mm:ss";
    public static final String CAMERA_DATE = "dd/MM/yyyy HH:mm:ss";
    public static final String SHORT_DATE = "dd-MM-yyyy";
    public static final String SHORT_DATE_TIME = "dd-MM-yyyy hh:mm a";

    /**
     * Returns the current date as "dd-MM-yyyy"
     * @return the date as string
     */
    public static String getCurrentDate(String format) {
        try {
            Date date = new Date();
            return new SimpleDateFormat(format).format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return format;
    }


    /**
     * Returns the current date with custom format.
     *
     * @return the date as string
     */
    public static String getDate(long time, String format) {
        try {
            Date date = new Date();
            date.setTime(time * 1000);
            return new SimpleDateFormat(format).format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return format;
    }


    /**
     * Gets the date given by unix timestamp as string.
     *
     * @param time the unix timestamp
     * @return the date as string
     */
    public static String getFriendlyDate(long time) {
        try {
            Date date = new Date();
            date.setTime(time * 1000);
            return new SimpleDateFormat("MMM dd, yyyy hh:mm:ss a").format(date).replace("am", "AM").replace("pm","PM").replace(".", "");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "[getFriendlyDate error with " + String.valueOf(time) + "]";
    }

    public static long getFromFormat(String format, String date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            var d = sdf.parse(date);
            return d.getTime() / 1000L;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
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
            long uptime = input * 1000;
            long days = (uptime / (1000 * 60 * 60 * 24));
            long hours = (uptime - days * (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
            long minutes = (uptime - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60)) / (1000 * 60);
            long seconds = (uptime - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60) - minutes * (1000 * 60)) / (1000);

            return days + " day(s), " + hours + " hour(s), " + minutes + " minute(s) and " + seconds + " second(s)";

        } catch (Exception e){
            Log.getErrorLogger().error("Error occurred: ", e);
        }

        return null;
    }

    public static String getMarketplaceReadableSeconds(long input) {
        try {
            //long different = System.currentTimeMillis() - (timestamp * 1000);
            long uptime = input * 1000;
            long days = (uptime / (1000 * 60 * 60 * 24));
            long hours = (uptime - days * (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
            long minutes = (uptime - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60)) / (1000 * 60);
            long seconds = (uptime - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60) - minutes * (1000 * 60)) / (1000);

            String r = "";

            if (days == 1) {
                r += days + " day<br>";
            } else {
                r += days + " days<br>";
            }

            if (hours == 1) {
                r += hours + " hour";
            } else {
                r += hours + " hours";
            }

            if (days < 1) {
                return "<font color=\"red\">" + r + "</font>";
            }

            return r;

        } catch (Exception e){
            Log.getErrorLogger().error("Error occurred: ", e);
        }

        return null;
    }

    public static LocalDateTime getDateTimeFromTimestamp(long timestamp) {
        if (timestamp == 0)
            return null;
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), TimeZone
                .getDefault().toZoneId());
    }

    /**
     * Returns the current date as "dd-MM-yyyy"
     * @return the date as string
     */
    public static String getShortDate() {
        Date date = new Date();
        return new SimpleDateFormat(SHORT_DATE).format(date);
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
     * Gets the current time in seconds.
     *
     * @return the current time in seconds
     */
    public static int getCurrentTimeSeconds() {
        return (int) (System.currentTimeMillis() / 1000);
    }
}
