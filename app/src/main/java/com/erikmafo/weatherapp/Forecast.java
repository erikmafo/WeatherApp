package com.erikmafo.weatherapp;

import android.content.Context;
import android.graphics.drawable.Drawable;

/**
 * Created by erik on 10/19/15.
 */
public class Forecast {

    private CurrentForecast mCurrentForecast;


    private Forecast(Builder builder){
        mCurrentForecast = builder.mCurrentForecast;
    }

    public CurrentForecast getCurrentForecast() {
        return mCurrentForecast;
    }

    public static Drawable getIconDrawable(Context context, String icon) {

        Drawable drawable = null;

        if (icon.equals("clear-day")) {
            drawable = context.getDrawable(R.drawable.clear_day);
        } else if (icon.equals("clear-night")) {
            drawable = context.getDrawable(R.drawable.clear_night);
        } else if (icon.equals("rain")) {
            drawable = context.getDrawable(R.drawable.rain);
        } else if (icon.equals("snow")) {
            drawable = context.getDrawable(R.drawable.snow);
        } else if (icon.equals("sleet")) {
            drawable = context.getDrawable(R.drawable.sleet);
        } else if (icon.equals("wind")) {
            drawable = context.getDrawable(R.drawable.clear_day); // todo obtain custom windy icon
        } else if (icon.equals("fog")) {
            drawable = context.getDrawable(R.drawable.fog);
        } else if (icon.equals("cloudy")) {
            drawable = context.getDrawable(R.drawable.cloudy);
        } else if (icon.equals("partly-cloudy-day")) {
            drawable = context.getDrawable(R.drawable.partly_cloudy_day);
        } else if (icon.equals("partly-cloudy-night")){
            drawable = context.getDrawable(R.drawable.partly_cloudy_night);
        }

        return drawable;
    }

    public static class Builder {

        private CurrentForecast mCurrentForecast;

        public Builder setCurrentForecast(CurrentForecast currentForecast) {
            mCurrentForecast = currentForecast;
            return this;
        }

        public Forecast build() {
            return new Forecast(this);
        }

    }


}
