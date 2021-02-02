package com.example.stockapp.searchPage;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextWatcher;

import com.example.stockapp.showFavoriteItem;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class mySharedPreference {
    public static final String PREFS_NAME = "Test1";
    public static final String FAVORITES = "Test2";
    public static final String FAVORITES2 = "Test3";
    public static final String Worth = "Worth";
    public static final String Portfolio= "Test4";

    public mySharedPreference() {
        super();
    }
    //1

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

    public void removeFavorite(Context context, favoriteItem newsItem) {
        ArrayList<favoriteItem> favorites = getFavorites(context);
        if (favorites != null) {
            favorites.remove(newsItem);
            saveFavorites(context, favorites);
        }
    }

    public void addFavorite(Context context, favoriteItem newsItem) {
        List<favoriteItem> favorites = getFavorites(context);
        if (favorites == null)
            favorites = new ArrayList<favoriteItem>();
        favorites.add(newsItem);
        saveFavorites(context, favorites);
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

    //2 meiyong

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

    //3port

    public void savePort(Context context, List<PortItem> favorites) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = settings.edit();

        Gson gson = new Gson();
        String jsonFavorites = gson.toJson(favorites);

        editor.putString(Portfolio, jsonFavorites);
        editor.apply();
    }

    public void removePort(Context context, PortItem newsItem) {
        ArrayList<PortItem> favorites = getPort(context);
        if (favorites != null) {
            favorites.remove(newsItem);
            savePort(context, favorites);
        }
    }

    public void addPort(Context context, PortItem newsItem) {
        List<PortItem> favorites = getPort(context);
        if (favorites == null)
            favorites = new ArrayList<>();
        favorites.add(newsItem);
        savePort(context, favorites);
    }

    public ArrayList<PortItem> getPort(Context context) {
        SharedPreferences settings;
        List<PortItem> favorites;

        settings = context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);

        if (settings.contains(Portfolio)) {
            String jsonFavorites = settings.getString(Portfolio, null);
            Gson gson = new Gson();
            PortItem[] favoriteItems = gson.fromJson(jsonFavorites,	PortItem[].class);

            favorites = Arrays.asList(favoriteItems);
            favorites = new ArrayList<>(favorites);
        } else
            return null;

        return (ArrayList<PortItem>) favorites;
    }


    //value
    public ArrayList<Double> getValue(Context context) {
        SharedPreferences settings;
        List<Double> value;
        settings = context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
        if(settings.contains(Worth)) {
            String v = settings.getString(Worth,null);
            Gson gson = new Gson();
            Double[] val = gson.fromJson(v, Double[].class);

            value = Arrays.asList(val);
            value = new ArrayList<>(value);
        }else
            return null;
        return  (ArrayList<Double>) value;
    }

    public void saveValue(Context context, List<Double> favorites) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = settings.edit();

        Gson gson = new Gson();
        String jsonFavorites = gson.toJson(favorites);

        editor.putString(Worth, jsonFavorites);
        editor.apply();
    }

    public void UpdateValue(Context context, Double x1) {
        List<Double> favorites = getValue(context);
        if (favorites == null){
            favorites = new ArrayList<Double>();
            favorites.add(0,20000.00);
            saveValue(context,favorites);
        }else{
            List<Double> favorites2 = new ArrayList<>();
            favorites2.add(0,x1);
            saveValue(context, favorites2);
        }

    }



}