package com.xudong.mynewsapplication.card;

import java.util.Objects;

public class NewsItem {
    private String mImageUrl;
    private String mSummary;
    private String mTime;
    private String mSection;
    private String mNewsId;
    private String mWebUrl;
    private int viewType;

    public NewsItem (String imageUrl, String summary, String time, String section, String webUrl, String id){
        mImageUrl = imageUrl;
        mSummary = summary;
        mTime = time;
        mSection = section;
        mNewsId = id;
        mWebUrl = webUrl;
        viewType = 0;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public String getSummary() {
        return mSummary;
    }

    public String getTime() {
        return mTime;
    }

    public String getSection() {
        return mSection;
    }

    public String getNewsId() {
        return mNewsId;
    }

    public String getmWebUrl() {
        return mWebUrl;
    }

    public int getViewType() {
        return viewType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NewsItem newsItem = (NewsItem) o;
        return Objects.equals(mSummary, newsItem.mSummary);
//        &&
//                Objects.equals(mTime, newsItem.mTime);
    }

    @Override
    public int hashCode() {
//        return Objects.hash(mSummary, mTime);
        return Objects.hash(mSummary);
    }
}
