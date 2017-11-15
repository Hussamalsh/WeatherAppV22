package com.example.hussam.weatherappv2.Other;

import com.example.hussam.weatherappv2.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Utility
{
    public static int getImgID(String icon)
    {
        if(icon.equals("rain"))
        {
            return R.drawable.ic_rain;
        }else if(icon.equals("clear_day"))
        {
            return R.drawable.ic_clear;
        }else if(icon.equals("clear_night"))
        {
            return R.drawable.ic_clear;
        }else if(icon.equals("cloudy"))
        {
            return R.drawable.ic_cloudy;
        }else if(icon.equals("fog"))
        {
            return R.drawable.ic_fog;
        }else if(icon.equals("partly_cloudy_day"))
        {
            return R.drawable.ic_light_clouds;
        }else if(icon.equals("snow"))
        {
            return R.drawable.ic_snow;
        }else if(icon.equals("sleet"))
        {
            return R.drawable.ic_storm;
        } else
        {
            return R.drawable.ic_storm;
        }
    }


    public static String getTime(Date date)
    {
        Date time=new Date(date.getTime());
        SimpleDateFormat sdf = new SimpleDateFormat("E dd.MM.yyyy - HH:mm"); // the format of your date
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+1"));
        String formattedDate = sdf.format(time);

        return formattedDate;
    }


    public static float kelvinToCelsius(float kelvinTemp)
    {
        return kelvinTemp - 273.15f;
    }


    public static int setImg(String icon)
    {
        if(icon.equals("rain"))
        {
            return R.drawable.rain;
        }else if(icon.equals("clear_day"))
        {
            return R.drawable.clear_day;
        }else if(icon.equals("clear_night"))
        {
            return R.drawable.clear_night;
        }else if(icon.equals("cloudy"))
        {
            return R.drawable.cloudy;
        }else if(icon.equals("fog"))
        {
            return R.drawable.fog;
        }else if(icon.equals("partly_cloudy_day"))
        {
            return R.drawable.partly_cloudy_day;
        }else if(icon.equals("snow"))
        {
            return R.drawable.snow;
        }else if(icon.equals("sleet"))
        {
            return R.drawable.sleet;
        }else
        {
            return R.drawable.default1;
        }
    }


}
