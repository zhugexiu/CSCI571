package com.example.stockapp.searchPage;

import java.util.Objects;

public class favoriteItem {
    private String mticker;
    private String mname;

    public favoriteItem (String ticker,String name){
        mticker = ticker;
        mname = name;
    }

    public String getfavoriteItem() {
        return mticker;
    }

    public String getFavoriteItemName() {return mname;}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        favoriteItem fItem = (favoriteItem) o;
        return Objects.equals(mticker, fItem.mticker);
//        &&
//                Objects.equals(mTime, newsItem.mTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mticker);
    }
}
