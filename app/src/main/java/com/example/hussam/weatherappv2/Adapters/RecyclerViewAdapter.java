package com.example.hussam.weatherappv2.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hussam.weatherappv2.Model.UserLocation;
import com.example.hussam.weatherappv2.R;
import com.example.hussam.weatherappv2.WeatherActivity;
import com.example.hussam.weatherappv2.forecastFramework.models.DataPoint;

import java.util.List;


public class RecyclerViewAdapter extends RecyclerView.Adapter<TextItemViewHolder>
{

    private List<UserLocation> items;
    private List<DataPoint> itemsDataPoints;
    private boolean isLocation = false;
    private WeatherActivity mActivity;
    public RecyclerViewAdapter(List<UserLocation> items, boolean isLocation, WeatherActivity activity)
    {
        this.items = items;
        this.isLocation = isLocation;
        mActivity = activity;
    }

    public RecyclerViewAdapter(List<DataPoint> items)
    {
        this.itemsDataPoints = items;
    }

    @Override
    public TextItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = null;
        if(isLocation){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_list_loc, parent, false);
        }else{
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_list_item, parent, false);
        }
        return new TextItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TextItemViewHolder holder, int position)
    {
        if(isLocation){
            holder.bind(items.get(position),mActivity);
        }else{
            holder.bind(itemsDataPoints.get(position));
        }
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public int getItemCount()
    {
        if(isLocation){
            return items.size();
        }else{
            return itemsDataPoints.size();
        }
    }

}
