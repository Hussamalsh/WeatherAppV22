package com.example.hussam.weatherappv2.Adapters;

import android.support.v7.widget.RecyclerView;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hussam.weatherappv2.Model.UserLocation;
import com.example.hussam.weatherappv2.Other.Utility;
import com.example.hussam.weatherappv2.Other.WhichFont;
import com.example.hussam.weatherappv2.R;
import com.example.hussam.weatherappv2.SQLiteHelper;
import com.example.hussam.weatherappv2.WeatherActivity;
import com.example.hussam.weatherappv2.forecastFramework.models.DataPoint;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class TextItemViewHolder extends RecyclerView.ViewHolder
{
   // private TextView textView;
    private  ImageView iconView;
    private  TextView dateView;
    private  TextView City__name;
    private  TextView descriptionView;
    private  TextView highTempView;
    private  TextView lowTempView;
    private DataPoint dataPoint;

    //forlocation Tab
    private Button seWeatherB, mDeleteLocB;
    private TextView locationName;
    SQLiteHelper sqLiteHelper;


    public TextItemViewHolder(View view)
    {
        super(view);
        iconView = (ImageView) view.findViewById(R.id.list_item_icon);
        dateView = (TextView) view.findViewById(R.id.list_item_date_textview);
        City__name = (TextView) view.findViewById(R.id.City__name);
        descriptionView = (TextView) view.findViewById(R.id.list_item_forecast_textview);
        highTempView = (TextView) view.findViewById(R.id.list_item_high_textview);
        lowTempView = (TextView) view.findViewById(R.id.list_item_low_textview);

        //forLocation Tab
        seWeatherB = (Button) view.findViewById(R.id.setWeather);
        mDeleteLocB = (Button) view.findViewById(R.id.deleteLoc);
        locationName = (TextView) view.findViewById(R.id.locationName);

    }

    public void bind(DataPoint dataPoint)
    {

       // textView.setText(text);
            SimpleDateFormat sdf = new SimpleDateFormat("E dd.MM.yyyy"); // the format of your date
            sdf.setTimeZone(TimeZone.getTimeZone("GMT+1"));
            Date time=new Date(dataPoint.getTime().getTime());
            String formattedDate = sdf.format(time);

            dateView.setText(formattedDate);

            String icon = dataPoint.getIcon().toString().toLowerCase().trim();
            icon = icon.replace("-","_");

            iconView.setImageResource(Utility.getImgID(icon));

            iconView.setContentDescription(dataPoint.getSummary());

            descriptionView.setTypeface(WhichFont.Fontmethod(descriptionView.getContext(), "fonts/AAfsaneh.ttf"));
            descriptionView.setText(dataPoint.getSummary());


            // For accessibility, add a content description to the icon field
            iconView.setContentDescription(dataPoint.getSummary());

            highTempView.setTypeface(WhichFont.Fontmethod(highTempView.getContext(), ""));

            if(dataPoint.getTemperatureMin() == null){
                highTempView.setText(""+Math.round(dataPoint.getTemperature())+" °C");
            }else {
                highTempView.setText(""+Math.round(dataPoint.getTemperatureMax())+" °C");
            }


            lowTempView.setTypeface(WhichFont.Fontmethod(lowTempView.getContext(), ""));

            if(dataPoint.getTemperatureMin() == null){
                lowTempView.setText("");
            }else {
                lowTempView.setText(""+Math.round(dataPoint.getTemperatureMin())+" °C");
            }
    }

    public void bind(final UserLocation userLoc, WeatherActivity activity)
    {
        final WeatherActivity ac = activity;
        mDeleteLocB.setOnClickListener(new View.OnClickListener()
        {
            @Override
          public void onClick(View view)
          {
            // Request delete
              sqLiteHelper = new SQLiteHelper(mDeleteLocB.getContext());
              sqLiteHelper.deleteLocation(userLoc.getId());
              sqLiteHelper.closeDB();
              ac.updateWeatherUI(true);
          }
        });

        seWeatherB.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                double latitude = Double.parseDouble(userLoc.getUserLat());
                double longitude = Double.parseDouble(userLoc.getUserLang());
                ac.getWeatherFromAPI(latitude,longitude);
            }
        });

        locationName.setText(userLoc.getUserLocName().trim());
    }




}
