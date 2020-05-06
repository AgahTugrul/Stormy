package com.example.stormy.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stormy.R;
import com.example.stormy.databinding.HourlyListItemBinding;
import com.example.stormy.model.Hour;

import java.util.List;

//manage the views from the hourly_list_item layout
public class HourlyAdapter extends RecyclerView.Adapter<HourlyAdapter.ViewHolder>{

    private List<Hour> hours;
    Context context;

    public HourlyAdapter(List<Hour> hours, Context context) {
        this.hours = hours;
        this.context = context;
    }


    @NonNull
    @Override
    public HourlyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        HourlyListItemBinding binding = DataBindingUtil.inflate(LayoutInflater.
                from(parent.getContext()),
                R.layout.hourly_list_item,parent,
                false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Hour hour = hours.get(position);
        holder.hourlyListItemBinding.setHour(hour);

    }

    @Override
    public int getItemCount() {
        return hours.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        //binding variables
        //constructer to do view lookups for each subview
        public HourlyListItemBinding hourlyListItemBinding;
        public ViewHolder(HourlyListItemBinding hourlyLayoutBinding){
            super(hourlyLayoutBinding.getRoot());
            hourlyListItemBinding = hourlyLayoutBinding;
        }
    }
}

