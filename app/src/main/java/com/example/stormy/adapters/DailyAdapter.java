package com.example.stormy.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stormy.R;
import com.example.stormy.databinding.DailyListItemBinding;
import com.example.stormy.model.Daily;

import java.util.List;


public class DailyAdapter extends RecyclerView.Adapter<DailyAdapter.mViewHolder> {

    private List<Daily> daily;
    private Context context;

    public DailyAdapter(List<Daily> daily, Context context) {
        this.daily = daily;
        this.context = context;
    }


    @NonNull
    @Override
    public DailyAdapter.mViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        DailyListItemBinding dailyListItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.daily_list_item,
                parent,
                false );
        return new mViewHolder(dailyListItemBinding);
    }


    @Override
    public void onBindViewHolder(@NonNull mViewHolder holder, int position) {
        Daily daily = this.daily.get(position);
        holder.dailyListItemBinding.setDaily(daily);
    }

    @Override
    public int getItemCount() {
        return daily.size();
    }

    public class mViewHolder extends RecyclerView.ViewHolder {
        DailyListItemBinding dailyListItemBinding;
        public mViewHolder(DailyListItemBinding dailyListLayoutBinding) {
            super(dailyListLayoutBinding.getRoot());
            dailyListItemBinding = dailyListLayoutBinding;
        }
    }

}
