package com.example.stormy;

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

import com.example.stormy.databinding.ActivityMainBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getSimpleName();

    CurrentWeather currentWeather;
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
        getLastLocation();
        getForcast(latitude,longitude);
        imageView = findViewById(R.id.maps_image_button);
        Log.d(TAG, "Main UI code is running");

    }

    private void getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        new OnCompleteListener<Location>() {
                            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                Location location = task.getResult();
                                if (location == null) {
                                    requestNewLocationData();
                                } else {
                                    latitude=location.getLatitude();
                                    longitude= location.getLongitude();
                                    getForcast(latitude,longitude);
                                    Log.d(TAG, "onComplete: "+location.toString() );
                                    
                                }
                            }
                        }
                );
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }

    private void requestNewLocationData() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );
    }
    private LocationCallback mLocationCallback = new LocationCallback() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            latitude=mLastLocation.getLatitude();
            longitude= mLastLocation.getLongitude();
            getForcast(latitude,longitude);
        }
    };

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
                               currentWeather = getCurrentDetails(jsonData);
                               final CurrentWeather displayWeather = new CurrentWeather(
                                       currentWeather.getLocationLabel(),
                                       currentWeather.getIcon(),
                                       currentWeather.getTime(),
                                       currentWeather.getTemperature(),
                                       currentWeather.getHumidity(),
                                       currentWeather.getPrecipChance(),  //cannot fetch this data
                                       currentWeather.getSummary(),
                                       currentWeather.getTimezone());
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

    private CurrentWeather getCurrentDetails(String jsonData) throws JSONException {
        JSONObject forecast = new JSONObject(jsonData);
        String timezone = forecast.getString("timezone");
        Log.d(TAG,"From JSON" + timezone);

        JSONObject currently = forecast.getJSONObject("currently");

        CurrentWeather currentWeather = new CurrentWeather();
        currentWeather.setHumidity(currently.getDouble("humidity"));
        currentWeather.setTime(currently.getLong("time"));
        currentWeather.setIcon(currently.getString("icon"));
        currentWeather.setLocationLabel("Alcatraz island, CA");
        currentWeather.setPrecipChance(currently.getDouble("precipProbability"));
        currentWeather.setSummary(currently.getString("summary"));
        currentWeather.setTemperature(currently.getDouble("temperature"));
        currentWeather.setTimezone(timezone);

        Log.d(TAG, currentWeather.getFormattedTime());
        return currentWeather;
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
    private boolean checkPermissions(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                return true;
            }
        return false;
    }
    private void requestPermissions(){
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                // Granted. Start getting the location information
            }
        }
    }
    private boolean isLocationEnabled(){
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    }




