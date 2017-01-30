package ru.timurchan.fedata;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Created by tiga1115 on 20.05.2016.
 */
public class DateFormatter implements Serializable {
    public int year;
    public int month;
    public int day;
    public DateFormatter() {}

    public DateFormatter(int year, int month, int day) {
        setDate(year, month, day);
    }

    public void setDate(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public int getYear() { return year; }
    public int getMonth() { return month; }
    public int getDay() { return day; }

    // -----------------------------------------------------

    static String[] months = {
            "января",
            "февраля",
            "марта",
            "апреля",
            "мая",
            "июня",
            "июля",
            "августа",
            "сентября",
            "октября",
            "ноября",
            "декабря",
    };

    static public String[] getMonths() { return months; }

    static public String getHumanDate(int year_, int month_, int day_) {
        if(month_ > 11 || month_ < 0) return "";

        String day = Integer.toString(day_);
        if(day.length() == 1) {
            day = '0' + day;
        }
        String year = Integer.toString(year_);
        String monthName = months[month_];
        return day + " " + monthName + " " + year;
    }

    static public String getDate(int year, int month, int day) {
        return String.format("%04d-%02d-%02d", year, month + 1, day);
    }

    static public String getDate() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return String.format("%04d-%02d-%02d", year, month + 1, day);
    }

    static public String getTime() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = 0;
        int second = 0;
        return String.format("%02d:%02d:%02d", hour, minute, second);
    }

    public String getHumanDate() {
        if(this.year > 0 && this.day > 0)
            return getHumanDate(this.year, this.month, this.day);
        else
            return "";
    }
}