package com.example.stormy.model;

import com.example.stormy.ui.MainActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class CurrentUserLocation {
    double longitude;
    double latitude;
    String country;

    public CurrentUserLocation(double longitude, double latitude, String country) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.country = country;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
