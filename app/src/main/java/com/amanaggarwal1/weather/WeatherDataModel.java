package com.amanaggarwal1.weather;

import org.json.JSONException;
import org.json.JSONObject;

public class WeatherDataModel {

    //Member variables are declared here
        private String mTemperature ;
        private String mCity ;
        private String mIconName;
        private int mCondition;


        public static WeatherDataModel fromJson(JSONObject jsonObject) {
            try {
                WeatherDataModel weatherDataModel = new WeatherDataModel();

                weatherDataModel.mCity = jsonObject.getString("name");
                weatherDataModel.mCondition = jsonObject.getJSONArray("weather").getJSONObject(0).getInt("id");
                updateWeatherIcon(weatherDataModel.mCondition);

                double tempResult = jsonObject.getJSONObject("main").getDouble("temp") - 273.15;
                int RoundedValue = (int) Math.rint(tempResult);

                weatherDataModel.mTemperature = Integer.toString(RoundedValue);

                return weatherDataModel;

            }catch (JSONException e) {
                e.printStackTrace();
                return  null;
            }
        }


    // To get the weather image name from the condition:
    private static String updateWeatherIcon(int condition) {

        if (condition >= 0 && condition < 300) {
            return "tstorm1";
        } else if (condition >= 300 && condition < 500) {
            return "light_rain";
        } else if (condition >= 500 && condition < 600) {
            return "shower3";
        } else if (condition >= 600 && condition <= 700) {
            return "snow4";
        } else if (condition >= 701 && condition <= 771) {
            return "fog";
        } else if (condition >= 772 && condition < 800) {
            return "tstorm3";
        } else if (condition == 800) {
            return "sunny";
        } else if (condition >= 801 && condition <= 804) {
            return "cloudy2";
        } else if (condition >= 900 && condition <= 902) {
            return "tstorm3";
        } else if (condition == 903) {
            return "snow5";
        } else if (condition == 904) {
            return "sunny";
        } else if (condition >= 905 && condition <= 1000) {
            return "tstorm3";
        }

        return "dunno";
    }


    public String getTemperature() {
        return mTemperature + "°";
    }

    public String getCity() {
        return mCity;
    }

    public String getIconName() {
        return mIconName;
    }
}
