package com.xudong.mynewsapplication.card;

public class WeatherCard {
    private String mImageUrl;
    private String mCity;
    private String mState;
    private String mTemperature;
    private String mType;
    private int viewType;

    public WeatherCard(String mImageUrl, String mCity, String mState, String mTemperature, String mType) {
        this.mImageUrl = mImageUrl;
        this.mCity = mCity;
        this.mState = mState;
        this.mTemperature = mTemperature;
        this.mType = mType;
        viewType = 1;//weather card
    }

    public String getmImageUrl() {
        return mImageUrl;
    }

    public String getmCity() {
        return mCity;
    }

    public String getmState() {
        return mState;
    }

    public String getmTemperature() {
        return mTemperature;
    }

    public String getmType() {
        return mType;
    }
}
