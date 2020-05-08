package com.example.stormy.ui;

import android.content.Intent;
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
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.view.animation.OvershootInterpolator;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.FadeInAnimator;
import jp.wasabeef.recyclerview.animators.ScaleInAnimator;
import jp.wasabeef.recyclerview.animators.SlideInDownAnimator;
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

public class HourlyForecastActivity extends AppCompatActivity {
    private HourlyAdapter adapter;
    private ActivityHourlyForecastBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        List<Hour> hours =
                (ArrayList<Hour>) intent.getSerializableExtra("HourlyList");


        binding = DataBindingUtil.setContentView(this,R.layout.activity_hourly_forecast);
        binding.hourlyListItems.setLayoutManager(new LinearLayoutManager(this));
        adapter = new HourlyAdapter(hours,this);
        binding.hourlyListItems.setHasFixedSize(true);
        binding.hourlyListItems.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
//        binding.hourlyListItems.setItemAnimator(new FadeInAnimator());
//        binding.hourlyListItems.setItemAnimator(new SlideInUpAnimator(new OvershootInterpolator(1f)));
        binding.hourlyListItems.setAdapter(adapter);



    }



}
