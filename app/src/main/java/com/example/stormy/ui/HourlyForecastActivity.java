package com.example.stormy.ui;

import android.os.Bundle;

import com.example.stormy.R;
import com.example.stormy.adapters.HourlyAdapter;
import com.example.stormy.databinding.ActivityHourlyForecastBinding;
import com.example.stormy.model.Hour;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import android.view.View;

import java.util.List;

public class HourlyForecastActivity extends AppCompatActivity {
    private HourlyAdapter adapter;
    private ActivityHourlyForecastBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this,R.layout.activity_hourly_forecast);

        adapter = new HourlyAdapter(getHourData(),this);
        binding.hourlyListItems.setAdapter(adapter);

    }

    private List<Hour> getHourData() {
        return List<Hour>;
    }

}
