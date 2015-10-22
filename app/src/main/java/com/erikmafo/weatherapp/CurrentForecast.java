package com.erikmafo.weatherapp;

/**
 * Created by erik on 10/19/15.
 */
public class CurrentForecast {

    private double mTemperature;
    private double mApparentTemperature;
    private String mSummary;
    private String mIcon;
    private double mPrecipProbability;

    public double getTemperature() {
        return mTemperature;
    }

    public String getIcon() {
        return mIcon;
    }

    public String getSummary() {
        return mSummary;
    }

    public double getApparentTemperature() {

        return mApparentTemperature;
    }

    public double getPrecipProbability() {
        return mPrecipProbability;
    }

    private CurrentForecast(Builder builder) {

        mTemperature = builder.mTemperature;
        mApparentTemperature = builder.mApparentTemperature;
        mSummary = builder.mSummary;
        mIcon = builder.mIcon;
        mPrecipProbability = builder.mPrecipProbability;

    }


    public static class Builder {

        private double mTemperature;
        private double mApparentTemperature;
        private String mSummary;
        private String mIcon;
        private double mPrecipProbability;

        public Builder setTemperature(double temperature) {
            mTemperature = temperature;
            return this;
        }

        public Builder setApparentTemperature(double apparentTemperature) {
            mApparentTemperature = apparentTemperature;
            return this;
        }

        public Builder setSummary(String summary) {
            mSummary = summary;
            return this;
        }

        public Builder setIcon(String icon) {
            mIcon = icon;
            return this;
        }

        public Builder setPrecipProbability(double precipProbability) {
            mPrecipProbability = precipProbability;
            return this;
        }


        public CurrentForecast build() {
            return new CurrentForecast(this);
        }


    }
}
