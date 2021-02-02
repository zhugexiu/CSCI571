package com.xudong.mynewsapplication.mainFragment;

import androidx.lifecycle.ViewModelProviders;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.xudong.mynewsapplication.R;
import com.xudong.mynewsapplication.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class TrendingFragment extends Fragment {

    private TrendingViewModel mViewModel;
    private EditText editText_keywords;
    private LineChart mChart;
    private RequestQueue requestQueue;
    private LineDataSet set;
    private static final String TAG = "hello";
    private String keyword = "CoronaVirus";
    private ArrayList<Entry> yValues = new ArrayList<>();

    public static TrendingFragment newInstance() {
        return new TrendingFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.trending_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(TrendingViewModel.class);
        // TODO: Use the ViewModel
        requestQueue = VolleySingleton.getInstance(getActivity()).getmRequestQueue();
        editText_keywords = getView().findViewById(R.id.editText_trending);
        editText_keywords.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                Log.e(TAG,"Click");
                if (null != event && KeyEvent.KEYCODE_ENTER == event.getKeyCode()) {
//                    Log.e(TAG,"Click");
                    switch (event.getAction()) {
                        case KeyEvent.ACTION_UP:
//                            Log.e(TAG,"Click");
//                            Toast.makeText(getActivity(), "you clicked " + editText_keywords.getText(), Toast.LENGTH_LONG).show();
                            if (editText_keywords.getText() != null && !editText_keywords.getText().toString().equals(""))
                                keyword = editText_keywords.getText().toString();
                            else
                                keyword = "CoronaVirus";
                            getChart();
                            return true;
                        default:
                            return true;
                    }
                }
//                if (actionId == EditorInfo.IME_ACTION_DONE) {
//                    //do here your stuff f
//                    Log.e(TAG,"Click");
//                    if (editText_keywords.getText() != null && !editText_keywords.getText().toString().equals(""))
//                                keyword = editText_keywords.getText().toString();
//                            else
//                                keyword = "CoronaVirus";
//                            getChart();
//                    return true;
//                }
                return false;
            }
        });


        mChart = getView().findViewById(R.id.lineChart_trending);
        mChart.setTouchEnabled(false);
        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.TOP);
        xAxis.setDrawGridLines(false);

        YAxis left = mChart.getAxisLeft();
        YAxis right = mChart.getAxisRight();
        left.setDrawGridLines(false);
        left.setDrawAxisLine(false);
        right.setDrawGridLines(false);
        left.setAxisMaximum(105f);
//        left.setAxisMinimum(0f);
        right.setAxisMaximum(105f);
//        right.setAxisMinimum(0f);
        Legend l = mChart.getLegend();
        l.setFormSize(20f);
        l.setTextSize(20f);
        getChart();
    }

    private void getChart(){
        String url = "http://xdtestnode-env.eba-vypzvpc6.us-east-1.elasticbeanstalk.com/hw9trending/" + keyword;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("results");
                            yValues.clear();
                            for (int i = 0; i < jsonArray.length(); i++){
                                yValues.add(new Entry(i, jsonArray.getInt(i)));
                            }
                            set = new LineDataSet(yValues, "Trending Chart for "+keyword);
                            set.setFillAlpha(255);//Sets the alpha value (transparency) that is used for filling the line surface
                            set.setColor(Color.rgb(51,42,128));
                            set.setCircleColor(Color.rgb(51,42,128));
                            set.setCircleColorHole(Color.rgb(51,42,128));
                            set.setLineWidth(1f);
                            set.setValueTextSize(10f);
                            set.setValueTextColor(Color.rgb(51,42,128));
                            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
                            dataSets.add(set);
                            LineData lineData = new LineData(dataSets);
//                            lineData.setValueFormatter(new MyValueFormatter());
                            mChart.setData(lineData);
                            mChart.invalidate();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        requestQueue.add(request);
    }

    public class MyValueFormatter implements IValueFormatter {

        private DecimalFormat mFormat;

        public MyValueFormatter() {
            mFormat = new DecimalFormat("###,###,##0"); // use one decimal
        }

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            // write your logic here
            return mFormat.format(value); // e.g. append a dollar-sign
        }
    }

}
