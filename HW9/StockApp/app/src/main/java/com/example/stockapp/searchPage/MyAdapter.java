package com.example.stockapp.searchPage;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.stockapp.R;
import java.util.ArrayList;


public class MyAdapter extends ArrayAdapter {
    private static final String TAG = "Hello";
    private Context mcontext;
    ArrayList<Item> birdList = new ArrayList<>();
    private LayoutInflater layoutInflater;

    public MyAdapter(Context context, int textViewResourceId,ArrayList objects) {
        super(context, textViewResourceId, objects);
        this.mcontext = context;
        this.birdList = objects;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View grid;
        int childCount = parent.getChildCount();
        //Log.d(TAG, "getView: position = " + position + ", childCount = " + childCount);
        LayoutInflater inflater = (LayoutInflater) mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {

            grid = new View(mcontext);
            grid = inflater.inflate(R.layout.grid_view_items, parent, false);
        } else {
            grid = (View) convertView;
        }


        View v = convertView;
        v = inflater.inflate(R.layout.grid_view_items, null);
        TextView textView = v.findViewById(R.id.stats_textview);
        textView.setText(birdList.get(position).getbirdName());
        return v;

    }

}
