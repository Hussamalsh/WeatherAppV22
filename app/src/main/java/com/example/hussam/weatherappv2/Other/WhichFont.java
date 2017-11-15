package com.example.hussam.weatherappv2.Other;

import android.content.Context;
import android.graphics.Typeface;
import java.util.Locale;

public class WhichFont
{

    public WhichFont(){}

    public static Typeface Fontmethod(Context activity, String address){

        String font = address.length() == 0? "fonts/ADastNevis.ttf" : address;

        if (Locale.getDefault().getLanguage().equals("en"))
            font = "fonts/Existence-Light.otf";

        return Typeface.createFromAsset(
                activity.getAssets(), font);
    }
}
