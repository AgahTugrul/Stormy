package com.example.stormy.model;

import com.example.stormy.R;

public class Forecast {
    private Current current;
    private Hour [] hourlyForecast;
    private Daily [] dailyForecast;

    public Current getCurrent() {
        return current;
    }

    public void setCurrent(Current current) {
        this.current = current;
    }

    public Hour[] getHourlyForecast() {
        return hourlyForecast;
    }

    public void setHourlyForecast(Hour[] hourlyForecast) {
        this.hourlyForecast = hourlyForecast;
    }

    public Daily[] getDailyForecast() {
        return dailyForecast;
    }

    public void setDailyForecast(Daily[] dailyForecast) {
        this.dailyForecast = dailyForecast;
    }

    public static int getIconId (String iconString) {
            int iconId= R.drawable.clear_day;
            switch(iconString) {
                case "clear-day":
                    iconId = R.drawable.clear_day;
                    break;
                case "clear-night":
                    iconId = R.drawable.clear_night;
                    break;
                case "rain":
                    iconId = R.drawable.rain;
                    break;
                case "snow":
                    iconId = R.drawable.snow;
                    break;
                case "sleet":
                    iconId = R.drawable.sleet;
                    break;
                case "wind":
                    iconId = R.drawable.wind;
                    break;
                case "fog":
                    iconId = R.drawable.fog;
                    break;
                case "cloudy":
                    iconId = R.drawable.cloudy;
                    break;
                case "partly-cloudy-day":
                    iconId = R.drawable.partly_cloudy;
                    break;
                case "partly-cloudy-night":
                    iconId = R.drawable.cloudy_night;
                    break;
            }
            return iconId;


    }

}
