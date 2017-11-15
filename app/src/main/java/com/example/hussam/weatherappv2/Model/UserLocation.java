package com.example.hussam.weatherappv2.Model;

public class UserLocation
{
    private String id;
    private String userLocName;
    private String userLat;
    private String userLang;

    public UserLocation(){}

    public UserLocation(String loc, String lat, String lang)
    {
        this.userLocName = loc;
        this.userLat = lat;
        this.userLang = lang;
    }

    public String getUserLocName()
    {
        return userLocName;
    }

    public String getUserLat()
    {
        return userLat;
    }

    public String getUserLang()
    {
        return userLang;
    }

    public void setUserLocName(String userLocName)
    {
        this.userLocName = userLocName;
    }

    public void setUserLat(String userLat)
    {
        this.userLat = userLat;
    }

    public void setUserLang(String userLang)
    {
        this.userLang = userLang;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }
}
