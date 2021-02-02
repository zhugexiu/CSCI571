package com.example.stockapp;

import java.util.Objects;

public class showFavoriteItem {
    private String mticker;
    private String mprice;
    private String mname;
    private String mchange;

    public showFavoriteItem (String ticker, String price, String name, String change){
        mticker = ticker;
        mprice = price;
        mname = name;
        mchange = change;
    }

    public String getfavoriteItem() {
        return mticker;
    }

    public String getfavoriteItemName() {
        return mname;
    }

    public String getfavoriteItemprice() {
        return mprice;
    }

    public String getfavoriteItemChange() {
        return mchange;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        showFavoriteItem fItem = (showFavoriteItem) o;
        return Objects.equals(mticker, fItem.mticker);
//        &&
//                Objects.equals(mTime, newsItem.mTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mticker);
    }
}
