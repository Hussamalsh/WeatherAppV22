package com.example.hussam.weatherappv2.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.hussam.weatherappv2.Fragments.*;

public class ViewPagerAdapter extends FragmentPagerAdapter
{

    public ViewPagerAdapter(FragmentManager fm)
    {
        super(fm);
    }

    @Override
    public Fragment getItem(int position)
    {
        Fragment fragment = null;
        if (position == 0)
        {
            fragment = new FragmentA();
        }
        else if (position == 1)
        {
            fragment = new FragmentB();
        }
        else if (position == 2)
        {
            fragment = new FragmentC();
        }
        return fragment;
    }

    @Override
    public int getCount()
    {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position)
    {
        String title = null;
        if (position == 0)
        {
            title = "Daily";
        }
        else if (position == 1)
        {
            title = "Hourly";
        }
        else if (position == 2)
        {
            title = "Locations";
        }
        return title;
    }
}
