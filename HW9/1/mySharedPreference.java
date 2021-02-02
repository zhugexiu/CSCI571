package com.example.stockapp.searchPage;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.stockapp.showFavoriteItem;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class mySharedPreference {
    public static final String PREFS_NAME = "NewsApp";
    public static final String FAVORITES = "NewsBookmark";
    public static final String FAVORITES2 = "APP2";

    public mySharedPreference() {
        super();
    }

    public void saveFavorites(Context context, List<favoriteItem> favorites) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = settings.edit();

        Gson gson = new Gson();
        String jsonFavorites = gson.toJson(favorites);

        editor.putString(FAVORITES, jsonFavorites);
        editor.apply();
    }

    public void saveFavorites2(Context context, List<showFavoriteItem> favorites) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = settings.edit();

        Gson gson = new Gson();
        String jsonFavorites = gson.toJson(favorites);

        editor.putString(FAVORITES2, jsonFavorites);
        editor.apply();
    }

    public void addFavorite(Context context, favoriteItem newsItem) {
        List<favoriteItem> favorites = getFavorites(context);
        if (favorites == null)
            favorites = new ArrayList<favoriteItem>();
        favorites.add(newsItem);
        saveFavorites(context, favorites);
    }

    public void removeFavorite(Context context, favoriteItem newsItem) {
        ArrayList<favoriteItem> favorites = getFavorites(context);
        if (favorites != null) {
            favorites.remove(newsItem);
            saveFavorites(context, favorites);
        }
    }

    public void addFavorite2(Context context, showFavoriteItem newsItem) {
        List<showFavoriteItem> favorites = getFavorites2(context);
        if (favorites == null)
            favorites = new ArrayList<showFavoriteItem>();
        favorites.add(newsItem);
        saveFavorites2(context, favorites);
    }

    public void removeFavorite2(Context context, showFavoriteItem newsItem) {
        ArrayList<showFavoriteItem> favorites = getFavorites2(context);
        if (favorites != null) {
            favorites.remove(newsItem);
            saveFavorites2(context, favorites);
        }
    }



    public ArrayList<favoriteItem> getFavorites(Context context) {
        SharedPreferences settings;
        List<favoriteItem> favorites;

        settings = context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);

        if (settings.contains(FAVORITES)) {
            String jsonFavorites = settings.getString(FAVORITES, null);
            Gson gson = new Gson();
            favoriteItem[] favoriteItems = gson.fromJson(jsonFavorites,	favoriteItem[].class);

            favorites = Arrays.asList(favoriteItems);
            favorites = new ArrayList<>(favorites);
        } else
            return null;

        return (ArrayList<favoriteItem>) favorites;
    }

    public ArrayList<showFavoriteItem> getFavorites2(Context context) {
        SharedPreferences settings;
        List<showFavoriteItem> favorites;

        settings = context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);

        if (settings.contains(FAVORITES2)) {
            String jsonFavorites = settings.getString(FAVORITES2, null);
            Gson gson = new Gson();
            showFavoriteItem[] favoriteItems = gson.fromJson(jsonFavorites,	showFavoriteItem[].class);

            favorites = Arrays.asList(favoriteItems);
            favorites = new ArrayList<>(favorites);
        } else
            return null;

        return (ArrayList<showFavoriteItem>) favorites;
    }
}