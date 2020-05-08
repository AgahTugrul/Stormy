package com.example.stormy.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;

import com.example.stormy.R;
import com.example.stormy.adapters.DailyAdapter;
import com.example.stormy.databinding.ActivityDailyForecastBinding;
import com.example.stormy.model.Daily;
import com.example.stormy.model.Hour;

import java.util.ArrayList;
import java.util.List;

public class DailyForecastActivity extends AppCompatActivity {
    DailyAdapter dailyAdapter;
    ActivityDailyForecastBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_forecast);

        Intent intent = getIntent();
        List<Daily> dailyList =
                (ArrayList<Daily>) intent.getSerializableExtra("DailyList");

        binding = DataBindingUtil.setContentView(this,R.layout.activity_daily_forecast);
        binding.dailyRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        dailyAdapter = new DailyAdapter(dailyList,this);
        binding.dailyRecyclerView.setHasFixedSize(true);
        binding.dailyRecyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
//        binding.hourlyListItems.setItemAnimator(new FadeInAnimator());
//        binding.hourlyListItems.setItemAnimator(new SlideInUpAnimator(new OvershootInterpolator(1f)));
        binding.dailyRecyclerView.setAdapter(dailyAdapter);


    }
}
