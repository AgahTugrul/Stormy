package com.example.stormy.ui;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stormy.R;
import com.example.stormy.databinding.ActivityMainBinding;
import com.example.stormy.model.Current;
import com.example.stormy.model.Forecast;
import com.example.stormy.model.Hour;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getSimpleName();

    Forecast forecast;
    private ImageView iconImageView;
    double longitude;
    double latitude;
    ImageView imageView;
    int PERMISSION_ID = 44;
    FusedLocationProviderClient mFusedLocationClient;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        //37.8267,-122.4233
        latitude = 37.8267;
        longitude= -122.4233;
        getForcast(latitude,longitude);
        imageView = findViewById(R.id.maps_image_button);
        Log.d(TAG, "Main UI code is running");

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void getForcast(double latitude, double longitude) {

        final ActivityMainBinding binding = DataBindingUtil.setContentView(MainActivity.this,R.layout.activity_main);
        TextView darkSky = findViewById(R.id.darkSky_label);
        iconImageView = findViewById(R.id.value_icon_imageView);
        darkSky.setMovementMethod(LinkMovementMethod.getInstance());
        imageView = findViewById(R.id.maps_image_button);

        String APIkey = "7bf35197daba0ba728f8360d2e6d8c50";

        String forecastUrl = "https://api.darksky.net/forecast/"
                +APIkey+ "/" +latitude + "," + longitude;
        Log.v(TAG, "jsonData beginning");
        if (isNetworkAvailable()) {
               OkHttpClient client = new OkHttpClient();

               Request request = new Request.Builder()
                       .url(forecastUrl)
                       .build();

               Call call = client.newCall(request);
               call.enqueue(new Callback() {
                   @Override
                   public void onFailure(@NotNull Call call, @NotNull IOException e) {
                   }

                   @Override
                   public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                       try {
                           String jsonData = response.body().string();
                           Log.v(TAG, jsonData);
                           if (response.isSuccessful()) {
                               forecast = parseForecastData(jsonData);
                               Current current = forecast.getCurrent();
                               final Current displayWeather = new Current(
                                       current.getLocationLabel(),
                                       current.getIcon(),
                                       current.getTime(),
                                       current.getTemperature(),
                                       current.getHumidity(),
                                       current.getPrecipChance(),  //cannot fetch this data
                                       current.getSummary(),
                                       current.getTimezone());
                               binding.setWeather(displayWeather);

                               runOnUiThread(new Runnable() {
                                   @Override
                                   public void run() {
                                       Drawable drawable = getResources().getDrawable(displayWeather.getIconId());
                                       iconImageView.setImageDrawable(drawable);
                                   }
                               });


                           }


                           else {
                               alertUserAboutError();
                           }
                       } catch (IOException | JSONException e) {
                           Log.e(TAG, "IO Exception Caught", e);
                       }
                   }

               });
           }
    }

    private Forecast parseForecastData (String jsonData) throws JSONException {
        Forecast forecast = new Forecast();
        forecast.setCurrent(getCurrentDetails(jsonData));
        forecast.setHourlyForecast(getHourlyForecast(jsonData));
        return forecast;
    }

    private Hour[] getHourlyForecast(String jsonData) throws JSONException {
        JSONObject forecast = new JSONObject(jsonData);
        String timezone = forecast.getString("timezone");

        JSONObject hourlyForecast = forecast.getJSONObject("hourly");
        JSONArray data = hourlyForecast.getJSONArray("data");
        Hour hours [] = new Hour[data.length()];
        for (int i = 0; i <data.length(); i++) {
            JSONObject jsonObject = data.getJSONObject(i);
            Hour hour = new Hour();
            hour.setSummary(jsonObject.getString("summary"));
            hour.setIcon(jsonObject.getString("icon"));
            hour.setTime(jsonObject.getLong("time"));
            hour.setTimezone(timezone);
            hour.setTemperature(jsonObject.getDouble("temperature"));
            hours[i] = hour;
        }

return hours;
    }

    private Current getCurrentDetails(String jsonData) throws JSONException {
        JSONObject forecast = new JSONObject(jsonData);
        String timezone = forecast.getString("timezone");
        Log.d(TAG,"From JSON" + timezone);

        JSONObject currently = forecast.getJSONObject("currently");

        Current current = new Current();
        current.setHumidity(currently.getDouble("humidity"));
        current.setTime(currently.getLong("time"));
        current.setIcon(currently.getString("icon"));
        current.setLocationLabel(timezone);
        current.setPrecipChance(currently.getDouble("precipProbability"));
        current.setSummary(currently.getString("summary"));
        current.setTemperature(currently.getDouble("temperature"));
        current.setTimezone(timezone);

        Log.d(TAG, current.getFormattedTime());
        return current;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private boolean isNetworkAvailable() {
        boolean isAvailable = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());

            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    isAvailable = true;
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    isAvailable = true;
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    isAvailable = true;
                }

            }
        }
        return isAvailable;
    }


    private void alertUserAboutError() {
    AlertDialogFragment alertDialogFragment = new AlertDialogFragment();
    alertDialogFragment.show(getSupportFragmentManager(),"Error Message");
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void onRefreshClick (View view){
        getForcast(latitude,longitude);
        Toast.makeText(MainActivity.this,"Data refreshed",Toast.LENGTH_SHORT).show();
    }

    public void hourlyOnClick (View view)
    {
        Intent intent = new Intent(this, HourlyForecastActivity.class);
        List<Hour> hours = Arrays.asList(forecast.getHourlyForecast());
        intent.putExtra("HourlyList", (Serializable) hours);
        startActivity(intent);
    }
    }




