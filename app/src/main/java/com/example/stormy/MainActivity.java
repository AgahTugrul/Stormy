package com.example.stormy;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stormy.databinding.ActivityMainBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
    FusedLocationProviderClient fusedLocationProviderClient;
    int LOCATION_REQUEST_CODE = 10001;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationProviderClient.getLastLocation();
        getForcast(latitude,longitude);
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
        fusedLocationProviderClient.getLastLocation();
        getForcast(latitude,longitude);
        Toast.makeText(MainActivity.this,"Data refreshed",Toast.LENGTH_SHORT).show();
    }
    @Override
    protected void onStart() {
        super.onStart();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getLastLocation();
        } else {
            askLocationPermission();
        }
    }

    private void getLastLocation() {
        Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();

        locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    //We have a location
                    //Log.d(TAG, "onSuccess: " + location.toString());
                    Log.d(TAG, "onSuccess: " + location.getLatitude());
                    Log.d(TAG, "onSuccess: " + location.getLongitude());
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                } else  {
                    Log.d(TAG, "onSuccess: Location was null...");
                }
            }
        });

        locationTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "onFailure: " + e.getLocalizedMessage() );
            }
        });
    }

    private void askLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                Log.d(TAG, "askLocationPermission: you should show an alert dialog...");
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                getLastLocation();
            } else {
                //Permission not granted
            }
        }
    }

    }

