package com.example.stockapp;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.stockapp.searchPage.PortItem;
import com.example.stockapp.searchPage.SearchActivity;
import com.example.stockapp.searchPage.favoriteItem;
import com.example.stockapp.searchPage.mySharedPreference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class portfolioAdapter extends RecyclerView.Adapter<portfolioAdapter.portfolioViewHolder> implements ItemMoveCallback2.ItemTouchHelperContract{
    private Context mcontext;
    private ArrayList<PortItem> mList;
    private mySharedPreference mSharedPreference;
    public static final String EXTRA_ID = "keyword";
    private RequestQueue requestQueue;
    private static final String TAG = "hello";

    public portfolioAdapter(Context context, ArrayList<PortItem> mList) {
        this.mcontext = context;
        this.mList = mList;
        mSharedPreference = new mySharedPreference();
    }

    public portfolioAdapter.portfolioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.favorite, parent, false);
        return new portfolioAdapter.portfolioViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull portfolioViewHolder holder, int position) {
        PortItem currentItem = (PortItem) mList.get(position);
        portfolioAdapter.portfolioViewHolder portfolioViewHolder = holder;
        String ticker = currentItem.getPortItem();
        Integer share = currentItem.getPortItemName();


        requestQueue = VolleySingleton.getInstance(mcontext).getmRequestQueue();
        String url = "http://zhugexiu.us-east-1.elasticbeanstalk.com/price/" + ticker;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
            try {
                JSONArray jsonArray = response.getJSONArray("description");
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                String Jsonprice = "$" + jsonObject.getString("last");
                String Jsonprice2 = jsonObject.getString("prevClose");
                double last = 0;
                if(jsonObject.getString("last") == null) {
                    last = 0;
                }else{
                    last = Double.parseDouble(jsonObject.getString("last"));
                }
                double prev = Double.parseDouble(Jsonprice2);
                double m = last-prev;
                String change = String.format("%.2f", m);
                //mySharedPreference.addFavorite(this,newlist);
                portfolioViewHolder.P_price.setText(Jsonprice);
                if(m>0){
                    portfolioViewHolder.P_imageView.setImageResource(R.drawable.ic_twotone_trending_up_24);
                    portfolioViewHolder.P_change.setText(Html.fromHtml("<font color='green'>" + change + "</font>"));
                }else if(m<0) {
                    portfolioViewHolder.P_imageView.setImageResource(R.drawable.ic_baseline_trending_down_24);
                    portfolioViewHolder.P_change.setText(Html.fromHtml("<font color='red'>" + change + "</font>"));
                }else{
                    portfolioViewHolder.P_imageView.setImageResource(R.drawable.ic_baseline_delete_24);
                    portfolioViewHolder.P_change.setText(change);
                }
                portfolioViewHolder.P_ticker.setText(ticker);
                portfolioViewHolder.P_name.setText(share+".0 shares");

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, Throwable::printStackTrace);
        requestQueue.add(request);

    }

    public int getItemCount() {
        if(mList != null)
            return mList.size();
        else
            return 0;
    }

    public class portfolioViewHolder extends RecyclerView.ViewHolder {

        TextView P_ticker;
        TextView P_name;
        TextView P_price;
        TextView P_change;
        ImageView P_imageView;
        ImageButton P_To_Detail;
        View rowView;

        public portfolioViewHolder(@NonNull View itemView) {
            super(itemView);
            rowView = itemView;
            P_ticker = itemView.findViewById(R.id.P_ticker);
            P_name = itemView.findViewById(R.id.P_name);
            P_price = itemView.findViewById(R.id.P_price);
            P_change = itemView.findViewById(R.id.P_change);
            P_imageView = itemView.findViewById(R.id.P_imageView);
            P_To_Detail = itemView.findViewById(R.id.imageButton_To_Detail);

            P_To_Detail.setOnClickListener(v -> {
                Intent detailIntent = new Intent(mcontext, SearchActivity.class);
                PortItem clickedItem = (PortItem) mList.get(getAdapterPosition());
                detailIntent.putExtra(EXTRA_ID, clickedItem.getPortItem());
                ActivityOptions options  = ActivityOptions.makeSceneTransitionAnimation((Activity) mcontext);
                mcontext.startActivity(detailIntent, options.toBundle());
            });

        }
    }
    public ArrayList<PortItem> getData() {
        return mList;
    }

    public void notifyAdapterDataSetChanged2() {
        List<PortItem> newList = mSharedPreference.getPort(mcontext);
        if (newList != null && newList.size() != mList.size()){
            mList = (ArrayList) newList;
            notifyDataSetChanged();
        }else if(newList != null&&newList.size() == mList.size()){
            for(int i = 0; i < newList.size();i++) {
                if(mList.get(i) != newList.get(i)) {
                    mList = (ArrayList) newList;
                    notifyDataSetChanged();
                    break;
                }
            }
        }
    }

    public void removeItem(int position) {
        mList.remove(position);
        notifyItemRemoved(position);
        mSharedPreference.savePort(mcontext,mList);
    }
    public void restoreItem(PortItem item, int position) {
        mList.add(position, item);
        notifyItemInserted(position);
        mSharedPreference.savePort(mcontext,mList);
    }

    public void onRowMoved(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(mList, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(mList, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    public void onRowSelected(portfolioViewHolder myViewHolder) {
        myViewHolder.rowView.setBackgroundColor(Color.GRAY);

    }

    public void onRowClear(portfolioViewHolder myViewHolder) {
        myViewHolder.rowView.setBackgroundColor(Color.WHITE);

    }
}
