package ru.timurchan.fedata;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class HumanDateConverter {
    public enum Format {
        Human,
        Simple
    }

    public static String convert(String date) {
        if(date == null || date.isEmpty()) return "";
        List<String> myList = new ArrayList<>(Arrays.asList(date.split("-")));
        int year = Integer.valueOf(myList.get(0));
        int month = Integer.valueOf(myList.get(1)) - 1;
        int day = Integer.valueOf(myList.get(2));

        return DateFormatter.getHumanDate(year, month, day);
    }

    public static String convert(String date, String time) {
        if(date == null || date.isEmpty()) {
            return time != null && !time.isEmpty() ? convertTime(time) : "";
        } else {
            return HumanDateConverter.convert(date) + (!(time == null) && !time.isEmpty() ? (", " + convertTime(time)) : "");
        }
    }

    public static String convertTime(String time) {
        return (time != null && !time.isEmpty() ? time.substring(0, 5) : "");
    }

    public static String convertDateEpoch(final String epochTime, final Format format) {
        try {
            int unixTime = Integer.parseInt(epochTime);
            return convertDateEpoch(unixTime, format);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String convertDateEpoch(int epochTime, final Format format) {
        String dateAsText = new SimpleDateFormat("yyyy-MM-dd")
                .format(new Date(epochTime * 1000L));

        if(format == Format.Simple) {
            return dateAsText;
        } else {
            String humanDate = HumanDateConverter.convert(dateAsText);
            return humanDate;
        }
    }

    public static String convertTimeEpoch(final String epochTime) {
        try {
            int unixTime = Integer.parseInt(epochTime);
            return convertTimeEpoch(unixTime);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String convertTimeEpoch(int epochTime) {
        String timeAsText = new SimpleDateFormat("HH:mm")
                .format(new Date(epochTime * 1000L));
        String humanDate = HumanDateConverter.convertTime(timeAsText);
        return humanDate;
    }

    // human format only
    public static String convertDateTimeEpoch(final String epochTime) {
        try {
            int unixTime = Integer.parseInt(epochTime);
            return convertDateTimeEpoch(unixTime);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return "";
    }

    // human format only
    public static String convertDateTimeEpoch(int epochTime) {
        String dateAsText = new SimpleDateFormat("yyyy-MM-dd")
                .format(new Date(epochTime * 1000L));
        String timeAsText = new SimpleDateFormat("HH:mm")
                .format(new Date(epochTime * 1000L));
        String humanDate = HumanDateConverter.convert(dateAsText, timeAsText);
        return humanDate;
    }
}
