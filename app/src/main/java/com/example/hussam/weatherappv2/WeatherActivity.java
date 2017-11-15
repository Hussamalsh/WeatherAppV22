package com.example.hussam.weatherappv2;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hussam.weatherappv2.Model.UserLocation;
import com.example.hussam.weatherappv2.Other.Utility;
import com.example.hussam.weatherappv2.forecastFramework.ForecastClient;
import com.example.hussam.weatherappv2.forecastFramework.ForecastConfiguration;
import com.example.hussam.weatherappv2.forecastFramework.models.DataBlock;
import com.example.hussam.weatherappv2.forecastFramework.models.DataPoint;
import com.example.hussam.weatherappv2.forecastFramework.models.Forecast;

import com.example.hussam.weatherappv2.Adapters.ViewPagerAdapter;
import com.example.hussam.weatherappv2.forecastFramework.models.Unit;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.Manifest;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class WeatherActivity extends AppCompatActivity
{
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;

    @Nullable
    private Forecast mForecast;
    private LocationManager mLocationManager;

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private static final String TAG = WeatherActivity.class.getSimpleName();
    /**
     * Provides the entry point to the Fused Location Provider API.
     */
    private FusedLocationProviderClient mFusedLocationClient;
    /**
     * Represents a geographical location.
     */
    protected Location mLastLocation;
    private String mLatitudeLabel;
    private String mLongitudeLabel;
    private String mCityName;

    private TextView todayTemperature;
    private TextView todayDescription;
    private TextView todayWind;
    private TextView todayPressure;
    private TextView todayHumidity;
    private TextView todaySunrise;
    private TextView todaySunset;
    private TextView lastUpdate;
    private TextView todayIcon;
    private ImageView iconView;

    private List<DataPoint> dailyWeatherList = new ArrayList<>();
    private List<DataPoint> hourlyWeatherList = new ArrayList<>();

    private String userEmail;
    SQLiteHelper sqLiteHelper;

    String shareCurrentLocationInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        userEmail = intent.getStringExtra("email");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initTextBoxes();
        sqLiteHelper = new SQLiteHelper(this); //not here


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        ForecastConfiguration configuration = new ForecastConfiguration.Builder("b27a634cf3bf13e80efddcfe4f404e58").setCacheDirectory(getCacheDir()).build();
        ForecastClient.create(configuration);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        viewPager = (ViewPager) findViewById(R.id.viewPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/

                shareCurrentLocationInfo = "City Name = "+mCityName +"\n"
                                         +"TodayTemperature = " + todayTemperature.getText()+"\n"
                                         +"Weather Description = " + todayDescription.getText()+"\n"
                                         +"Wind Speed = " + todayWind.getText()+"\n"
                                         +"Pressure = " + todayPressure.getText()+"\n"
                                         +"todaySunrise = " + todaySunrise.getText()+"\n"
                                         +"todaySunset = " + todaySunset.getText()+"\n";


                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT,shareCurrentLocationInfo);
                sendIntent.setType("text/plain");

                startActivity( Intent.createChooser(sendIntent,"Share via"));
            }
        });

        updateWeatherUI(false);

    }


    private void initTextBoxes()
    {
        // Initialize textboxes
        todayTemperature = (TextView) findViewById(R.id.todayTemperature);
        todayDescription = (TextView) findViewById(R.id.todayDescription);
        todayWind = (TextView) findViewById(R.id.todayWind);
        todayPressure = (TextView) findViewById(R.id.todayPressure);
        todayHumidity = (TextView) findViewById(R.id.todayHumidity);
        todaySunrise = (TextView) findViewById(R.id.todaySunrise);
        todaySunset = (TextView) findViewById(R.id.todaySunset);
        iconView = (ImageView) findViewById(R.id.todayIcon);
        iconView.setImageResource(Utility.getImgID("rain"));
    }


    @Override
    public void onStart()
    {
        super.onStart();
        if (!checkPermissions())
        {
            requestPermissions();
        } else {
            getLastLocation();
        }
    }


    private void setWeatherData()
    {
        double latitude = Double.parseDouble(mLatitudeLabel);
        double longitude = Double.parseDouble(mLongitudeLabel);
        getWeatherFromAPI(latitude, longitude);
    }

    public void getWeatherFromAPI(double latitude, double longitude )
    {
        ForecastClient.getInstance()
                .getForecast(latitude, longitude,null,null,Unit.SI,null,false, new Callback<Forecast>()
                {
                    @Override
                    public void onResponse(Call<Forecast> forecastCall, Response<Forecast> response)
                    {
                        if (response.isSuccessful())
                        {
                            Forecast forecast = response.body();
                            //set currently
                            DataPoint currently  =  forecast.getCurrently();
                            //set daily
                            DataBlock dailyBlock = forecast.getDaily();
                            dailyWeatherList = dailyBlock.getDataPoints();    //All days
                            DataPoint day = dailyWeatherList.get(0);
                            //set hourly
                            DataBlock hourlyBlock = forecast.getHourly();
                            hourlyWeatherList = hourlyBlock.getDataPoints();
                            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm a"); // the format of your date
                            sdf.setTimeZone(TimeZone.getTimeZone("GMT+1"));
                            String formattedDate;
                            todayTemperature.setText(""+Math.round(currently.getTemperature()) +" °C");
                            todayDescription.setText(currently.getSummary());
                            todayHumidity.setText("Humidity: "+currently.getHumidity().toString()+" %");
                            todayPressure.setText("Pressure: "+Math.round(currently.getPressure()) +" hpa");
                            Date time=new Date(day.getSunriseTime().getTime());
                            formattedDate = sdf.format(time);
                            todaySunrise.setText("Sunrise: "+formattedDate/*currently.getSunriseTime().toString()*/);
                            time=new Date(day.getSunsetTime().getTime());
                            formattedDate = sdf.format(time);
                            todaySunset.setText("Sunset: "+formattedDate /*currently.getSunsetTime().toString()*/);
                            todayWind.setText("Wind: "+currently.getWindSpeed().toString() +" m/s");

                            String icon = currently.getIcon().toString().toLowerCase().trim();
                            icon = icon.replace("-","_");
                            iconView.setImageResource(Utility.setImg(icon));

                            mCityName = forecast.getTimezone();
                            setTitle(mCityName= mCityName.substring(mCityName.indexOf("/")+1));
                            updateWeatherUI(false);
                        }
                    }
                    @Override
                    public void onFailure(Call<Forecast> forecastCall, Throwable t)
                    {
                        Toast.makeText(getApplicationContext(),"fel", Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void updateWeatherUI(boolean showloc)
    {
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        if(showloc)
            viewPager.setCurrentItem(2);
    }


    public List<DataPoint> getWeatherList(int i )
    {
        if (i == 1)
        {
            return dailyWeatherList;
        }
        return hourlyWeatherList;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId())
        {
            case R.id.action_Map:
                // User chose the "map" action, mark the current item
                // as a favorite...
                // Do something in response to button
              Intent intent = new Intent(this, MapActivity.class);
                //EditText editText = (EditText) findViewById(R.id.editText);
                intent.putExtra("lat", mLatitudeLabel);
                intent.putExtra("long", mLongitudeLabel);
                startActivity(intent);
                return true;

            case R.id.action_SaveLocBar:
                // User chose the "Settings" item, show the app settings UI...
                saveUserLocation();
                updateWeatherUI(true);
                return true;

            case R.id.action_logOut:

                SharedPreferences sharedPref = getSharedPreferences("mypref",0);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.clear();      //its clear all data.
                editor.commit();  //Don't forgot to commit  SharedPreferences.
               // editor.remove("email");//its remove name field from your SharedPreferences
                sqLiteHelper.closeDB();
                // User chose the "Settings" item, show the app settings UI...
                Intent intent2 = new Intent(this, LogInActivity.class);
                //EditText editText = (EditText) findViewById(R.id.editText);
                startActivity(intent2);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveUserLocation()
    {
        AlertDialog.Builder alertbuilder= new AlertDialog.Builder(this);
        alertbuilder.setMessage("You want to save this Location = " + mCityName)
                .setPositiveButton("Ok",new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        if(userEmail.contains("@"))
                        {
                            long id1 =  sqLiteHelper.createNewLoc(new UserLocation(mCityName,mLatitudeLabel,mLongitudeLabel),userEmail.trim());

                            if(id1== -1)
                            {
                                showSnackbar("The location already exists!");
                            }
                            updateWeatherUI(true);
                        }
                    }
                })
                .setNegativeButton("No",null)
                .create()
                .show();
    }

    public List<UserLocation> getAllLocations()
    {
        List<UserLocation> userLoc = sqLiteHelper.getAllLocationsByEmail(userEmail.trim());
        return userLoc;
    }


    /**
     * Return the current state of the permissions needed.
     */
    private boolean checkPermissions()
    {
        int permissionState = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions()
    {
        boolean shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            showSnackbar(R.string.permission_rationale, android.R.string.ok, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Request permission
                    startLocationPermissionRequest();
                }
            });

        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            startLocationPermissionRequest();
        }
    }

    private void startLocationPermissionRequest()
    {
        ActivityCompat.requestPermissions(WeatherActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_PERMISSIONS_REQUEST_CODE);
    }


    /**
     * Shows a {@link Snackbar} using {@code text}.
     *
     * @param text The Snackbar text.
     */
    private void showSnackbar(final String text)
    {
        View container = findViewById(R.id.main_activity_container);
        if (container != null)
        {
            Snackbar.make(container, text, Snackbar.LENGTH_LONG).show();
        }
    }

    /**
     * Shows a {@link Snackbar}.
     *
     * @param mainTextStringId The id for the string resource for the Snackbar text.
     * @param actionStringId   The text of the action item.
     * @param listener         The listener associated with the Snackbar action.
     */
    private void showSnackbar(final int mainTextStringId, final int actionStringId, View.OnClickListener listener) {
        Snackbar.make(findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_LONG)
                .setAction(getString(actionStringId), listener).show();
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
                getLastLocation();
            } else {
                // Permission denied.

                // Notify the user via a SnackBar that they have rejected a core permission for the
                // app, which makes the Activity useless. In a real app, core permissions would
                // typically be best requested during a welcome-screen flow.

                // Additionally, it is important to remember that a permission might have been
                // rejected without asking the user for permission (device policy or "Never ask
                // again" prompts). Therefore, a user interface affordance is typically implemented
                // when permissions are denied. Otherwise, your app could appear unresponsive to
                // touches or interactions which have required permissions.
                showSnackbar(R.string.permission_denied_explanation, R.string.settings,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
            }
        }
    }

    /**
     * Provides a simple way of getting a device's location and is well suited for
     * applications that do not require a fine-grained location and that do not need location
     * updates. Gets the best and most recent location currently available, which may be null
     * in rare cases when a location is not available.
     * <p>
     * Note: this method should be called after location permission has been granted.
     */
    @SuppressWarnings("MissingPermission")
    private void getLastLocation() {
        mFusedLocationClient.getLastLocation()
                .addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            mLastLocation = task.getResult();
                            mLatitudeLabel = ""+mLastLocation.getLatitude();
                            mLongitudeLabel = ""+mLastLocation.getLongitude();
                            setWeatherData();
                        } else {
                            Log.w(TAG, "getLastLocation:exception", task.getException());
                            showSnackbar(getString(R.string.no_location_detected));
                        }
                    }
                });
    }

}
