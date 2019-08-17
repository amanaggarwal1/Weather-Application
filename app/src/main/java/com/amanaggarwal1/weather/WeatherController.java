package com.amanaggarwal1.weather;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


public class WeatherController extends AppCompatActivity {

    // Constants:
    final String WEATHER_URL = "http://api.openweathermap.org/data/2.5/weather";
    // App ID to use OpenWeather data
    final String APP_ID = "7fdb827ab5f3c9d24aecdaa2a7768ff4";
    // Time between location updates (5000 milliseconds or 5 seconds)
    final long MIN_TIME = 5000;
    // Distance between location updates (1000m or 1km)
    final float MIN_DISTANCE = 1000;
    final  int REQUEST_CODE=123;

    String LOCATION_PROVIDER = LocationManager.GPS_PROVIDER;

    // Member Variables:
    Boolean mUseLocation = true;
    TextView mCityLabel;
    ImageView mWeatherImage;
    TextView mTemperatureLabel;

    LocationManager mLocationManager;
    LocationListener mLocationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_controller_layout);

        // Linking the elements in the layout to Java code
        mCityLabel = findViewById(R.id.locationTV);
        mWeatherImage = findViewById(R.id.weatherSymbolIV);
        mTemperatureLabel = findViewById(R.id.tempTV);
        ImageButton changeCityButton = findViewById(R.id.changeCityButton);

        changeCityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(WeatherController.this, ChangeCityController.class);
                startActivity(myIntent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("Weather", "onResume() started");

        Intent myIntent = getIntent();
        String city = myIntent.getStringExtra("City");

        if (city != null) {
            getWeatherForNewCity(city);

        } else {
            Log.d("Weather", "Getting weather for current location");
            if (mUseLocation) getWeatherForCurrentLocattion();
        }
    }


    private void  getWeatherForNewCity(String city){

        RequestParams params = new RequestParams();
        params.put("q",city);
        params.put("appid", APP_ID);
        letsDoSomeNetworking(params);

    }


    private void getWeatherForCurrentLocattion() {

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d("Weather", "onLocationChange() called");
                String Longitude = String.valueOf(location.getLongitude());
                String Latitude = String.valueOf(location.getLatitude());

                Log.d("Weather","Longitude is " + Longitude);
                Log.d("Weather", "Latitude is " + Latitude);

                RequestParams params= new RequestParams();
                params.put("lat", Latitude);
                params.put("lon", Longitude);
                params.put("appid", APP_ID);
                letsDoSomeNetworking(params);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.d("Weather", "onProviderDisabled() called");
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        mLocationManager.requestLocationUpdates(LOCATION_PROVIDER, MIN_TIME, MIN_DISTANCE, mLocationListener);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("Weather", "Permission has been granted");
                getWeatherForCurrentLocattion();
            } else
                Log.d("Weather", "Permission denied");
        }
    }


    private void letsDoSomeNetworking(RequestParams params) {

        AsyncHttpClient client = new AsyncHttpClient();

        // Making an HTTP GET request by providing a URL and the parameters.
        client.get(WEATHER_URL, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                Log.d("Weather", "Success! JSON: " + response.toString());
                WeatherDataModel weatherData = WeatherDataModel.fromJson(response);
                updateUI(weatherData);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response) {

                Log.e("Weather", "Fail " + e.toString());
                Toast.makeText(WeatherController.this, "Request Failed", Toast.LENGTH_SHORT).show();

                Log.d("Weather", "Status code " + statusCode);
                Log.d("Weather", "Here's what we got instead " + response.toString());
            }

        });
    }


    void updateUI(WeatherDataModel weatherData)
    {
        Log.d("Weather", "updateUI() called");
        mTemperatureLabel.setText(weatherData.getTemperature());
        mCityLabel.setText(weatherData.getCity());
        int resourceID = getResources().getIdentifier(weatherData.getIconName(), "drawable", getPackageName());
        mWeatherImage.setImageResource(resourceID);

    }


    @Override
    protected void onPause(){
        super.onPause();

        if(mLocationManager != null)
                mLocationManager.removeUpdates(mLocationListener);
    }



}