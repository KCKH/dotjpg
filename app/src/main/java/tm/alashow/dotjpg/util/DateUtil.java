/*
 * Copyright (c) 2015  Alashov Berkeli
 * It is licensed under GNU GPL v. 2 or later. For full terms see the file LICENSE.
 */

package tm.alashow.dotjpg.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtil {
    public static SimpleDateFormat hourMinuteFormat = new SimpleDateFormat("HH:mm", Locale.US);

    /**
     * Convert Java {@link Date} objects in just "a few minutes"
     *
     * @param date date object for convert
     * @return converted readable date
     */
    public static String getTimeAgo(Date date) {

        if (date == null) {
            return null;
        }

        String timeAgo;
        Date curDate = currentDate();
        long time = date.getTime();
        long now = curDate.getTime();

        if (time > now || time <= 0) {
            return null;
        }

        int dim = getTimeDistanceInMinutes(time);
        if (dim == 0) {
            if (getTimeDistanceInSeconds(time) < 10) {
                timeAgo = "şuwagtjyk";
            } else {
                timeAgo = getTimeDistanceInSeconds(time) + " sekunt öň";
            }
        } else if (dim == 1) {
            return "1 " + "minut öň";
        } else if (dim >= 2 && dim <= 44) {
            timeAgo = dim + " minut öň";
        } else if (dim >= 45 && dim <= 69) {
            timeAgo = "1 sagat öň";
        } else if (dim >= 70 && dim <= 1439) {
            timeAgo = (Math.round(dim / 60)) + " sagat öň" + ", " + hourMinuteFormat.format(date.getTime());
        } else if (dim >= 1440 && dim <= 2519) {
            timeAgo = "Düýn" + ", " + hourMinuteFormat.format(date.getTime());
        }
//        else if (dim >= 2520 && dim <= 43199)
//            timeAgo = (Math.round(dim / 1440)) + " gün öň" + ", " + hourMinuteFormat.format(date.getTime());
//        else if (dim >= 43200 && dim <= 86399)
//            timeAgo = "1 aý öň, " + getHumanDateWithoutYear(time);
//        else if (dim >= 86400 && dim <= 525599)
//            timeAgo = (Math.round(dim / 43200)) + " aý öň, " + getHumanDateWithoutYear(time);
        else if (dim >= 2520 && dim <= 525599) {
            timeAgo = getHumanDateWithoutYear(time);
        } else {
            timeAgo = getHumanDate(time);
        }
        return timeAgo;
    }

    private static int getTimeDistanceInMinutes(long time) {
        long timeDistance = currentDate().getTime() - time;
        return Math.round((Math.abs(timeDistance) / 1000) / 60);
    }

    private static int getTimeDistanceInSeconds(long time) {
        long timeDistance = currentDate().getTime() - time;
        return Math.round((Math.abs(timeDistance) / 1000));
    }

    private static Date currentDate() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getTime();
    }

    /**
     * @param time timestamp in milliseconds
     * @return "01 Ýan 2015ý 22:15" like date
     */
    private static String getHumanDate(long time) {
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(time);
        return date.get(Calendar.DAY_OF_MONTH) + " " + getShortMonthName(date.get(Calendar.MONTH)) + " " + date.get(Calendar.YEAR) + "ý, " + hourMinuteFormat.format(date.getTime());
    }

    /**
     * @param time timestamp in milliseconds
     * @return "01 Ýan 22:15" like date
     */
    private static String getHumanDateWithoutYear(long time) {
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(time);
        return date.get(Calendar.DAY_OF_MONTH) + " " + getShortMonthName(date.get(Calendar.MONTH)) + " " + hourMinuteFormat.format(date.getTime());
    }

    /**
     * Get Short Localised Month name by month index
     *
     * @param month index of month, in 0-11 range
     * @return short month name
     */
    private static String getShortMonthName(int month) {
        if (month < 0 || month > 11) {
            return "null";
        }
        String[] months = {"Ýan", "Few", "Mart", "Apr", "Maý", "Iýun", "Iýul", "Awg", "Sen", "Okt", "Noý", "Dek"};
        return months[month];
    }
}