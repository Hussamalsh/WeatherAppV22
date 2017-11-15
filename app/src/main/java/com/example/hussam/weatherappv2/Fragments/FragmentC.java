package com.example.hussam.weatherappv2.Fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hussam.weatherappv2.Adapters.RecyclerViewAdapter;
import com.example.hussam.weatherappv2.Model.UserLocation;
import com.example.hussam.weatherappv2.R;
import com.example.hussam.weatherappv2.WeatherActivity;

import java.util.ArrayList;
import java.util.List;


public class FragmentC extends Fragment
{
    private RecyclerView recyclerView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(
                R.layout.fragment, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        WeatherActivity weatherActivity = (WeatherActivity) getActivity();
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(weatherActivity.getAllLocations(),true,weatherActivity);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

    }
}