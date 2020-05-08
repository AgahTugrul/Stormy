package com.example.stormy.model;

import android.util.Log;

import java.io.Serializable;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static com.example.stormy.ui.MainActivity.TAG;

public class Daily implements Serializable {
    private String summary;
    private String icon;
    private long time;
    private String timezone;
    private double temperatureMin;
    private long temperatureMinTime;
    private double temperatureMax;
    private long temperatureMaxTime;

    public Daily() {
    }

    public Daily(String summary, String icon, long time, String timezone, double temperatureMin, long temperatureMinTime, double temperatureMax, long temperatureMaxTime) {
        this.summary = summary;
        this.icon = icon;
        this.time = time;
        this.timezone = timezone;
        this.temperatureMin = temperatureMin;
        this.temperatureMinTime = temperatureMinTime;
        this.temperatureMax = temperatureMax;
        this.temperatureMaxTime = temperatureMaxTime;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public int getIcon() {
        return Forecast.getIconId(icon);
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getTime() {
        Log.d(TAG, "getTime: worked ");
        DateFormatSymbols dfs = new DateFormatSymbols(Locale.getDefault());
        Log.d(TAG, "getTime: worked ");
        String weekdays [] =dfs.getWeekdays();
        Log.d(TAG, "getTime: worked ");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone(timezone));
        calendar.setTimeInMillis(time*1000);
        Log.d(TAG, "getTime: worked 4");
        int mDay = calendar.get(Calendar.DAY_OF_WEEK);
        Log.d(TAG, "getTime: worked 5");
        String nameOfDay = weekdays[mDay];
        Log.d(TAG, "getTime: worked 6");
        return nameOfDay;

    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public double getTemperatureMin() {
        return (int)Math.round(temperatureMin);
    }

    public void setTemperatureMin(double temperatureMin) {
        this.temperatureMin = temperatureMin;
    }

    public String getTemperatureMinTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("h a");
        formatter.setTimeZone(TimeZone.getTimeZone(timezone));
        Date date = new Date(temperatureMinTime* 1000);
        return formatter.format(date);
    }

    public void setTemperatureMinTime(long temperatureMinTime) {
        this.temperatureMinTime = temperatureMinTime;
    }

    public double getTemperatureMax() {
        return (int)Math.round(temperatureMax);
    }

    public void setTemperatureMax(double temperatureMax) {
        this.temperatureMax = temperatureMax;
    }

    public String getTemperatureMaxTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("h a");
        formatter.setTimeZone(TimeZone.getTimeZone(timezone));
        Date date = new Date(temperatureMaxTime * 1000);
        return formatter.format(date);
    }

    public void setTemperatureMaxTime(long temperatureMaxTime) {
        this.temperatureMaxTime = temperatureMaxTime;
    }
}
