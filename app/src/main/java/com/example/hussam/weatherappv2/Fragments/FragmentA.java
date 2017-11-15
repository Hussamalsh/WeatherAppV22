package com.example.hussam.weatherappv2.Fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.hussam.weatherappv2.Adapters.RecyclerViewAdapter;
import com.example.hussam.weatherappv2.R;
import com.example.hussam.weatherappv2.WeatherActivity;

public class FragmentA extends Fragment {

    private RecyclerView recyclerView;
    private ListView list;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        WeatherActivity weatherActivity = (WeatherActivity) getActivity();

        RecyclerViewAdapter adapter = new RecyclerViewAdapter(weatherActivity.getWeatherList(1));

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }
}