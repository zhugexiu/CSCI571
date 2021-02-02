package com.xudong.mynewsapplication.bookmark;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.xudong.mynewsapplication.card.NewsItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class mySharedPreference {
    public static final String PREFS_NAME = "NewsApp";
    public static final String FAVORITES = "NewsBookmark";

    public mySharedPreference() {
        super();
    }

    public void saveFavorites(Context context, List<NewsItem> favorites) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = settings.edit();

        Gson gson = new Gson();
        String jsonFavorites = gson.toJson(favorites);

        editor.putString(FAVORITES, jsonFavorites);
        editor.commit();
    }

    public void removeFavorite(Context context, NewsItem newsItem) {
        ArrayList<NewsItem> favorites = getFavorites(context);
        if (favorites != null) {
            favorites.remove(newsItem);
            saveFavorites(context, favorites);
        }
    }

    public void addFavorite(Context context, NewsItem newsItem) {
        List<NewsItem> favorites = getFavorites(context);
        if (favorites == null)
            favorites = new ArrayList<NewsItem>();
        favorites.add(newsItem);
        saveFavorites(context, favorites);
    }

    public ArrayList<NewsItem> getFavorites(Context context) {
        SharedPreferences settings;
        List<NewsItem> favorites;

        settings = context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);

        if (settings.contains(FAVORITES)) {
            String jsonFavorites = settings.getString(FAVORITES, null);
            Gson gson = new Gson();
            NewsItem[] favoriteItems = gson.fromJson(jsonFavorites,	NewsItem[].class);

            favorites = Arrays.asList(favoriteItems);
            favorites = new ArrayList<>(favorites);
        } else
            return null;

        return (ArrayList<NewsItem>) favorites;
    }
}
