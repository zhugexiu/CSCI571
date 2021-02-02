package com.example.stockapp.searchPage;

import java.util.Objects;

public class PortItem {
    private String mticker;
    private Integer mshare;

    public PortItem (String ticker,Integer share){
        mticker = ticker;
        mshare = share;
    }

    public String getPortItem() {
        return mticker;
    }

    public Integer getPortItemName() {return mshare;}

    public void setPortItemValue(Integer m) {
        mshare = m;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PortItem fItem = (PortItem) o;
        return Objects.equals(mticker, fItem.mticker);
//        &&
//                Objects.equals(mTime, newsItem.mTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mticker);
    }
}