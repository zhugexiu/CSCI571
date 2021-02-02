package com.xudong.mynewsapplication.searchPage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.transition.Slide;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.squareup.picasso.Picasso;
import com.xudong.mynewsapplication.DetailActivity;
import com.xudong.mynewsapplication.R;
import com.xudong.mynewsapplication.VolleySingleton;
import com.xudong.mynewsapplication.card.NewsCardsAdapter;
import com.xudong.mynewsapplication.card.NewsItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static android.widget.LinearLayout.VERTICAL;

public class SearchActivity extends AppCompatActivity {

    public static final String EXTRA_ID = "keyword";
    private static final String TAG = "hello";
    private boolean hasCreated = false;
    private String keyword;
    private TextView textViewSearch;
    private TextView textViewProgressBar;
    private ProgressBar progressBar;
    private ImageView imageViewBack;

    //recycler
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private NewsCardsAdapter newsCardsAdapter;
    private ArrayList newsItemArrayList;
    private RequestQueue requestQueue;
    //dialog
    private ImageView imageViewDialog;
    private TextView textViewDialog;
    private ImageView imageViewTwitter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().setEnterTransition(new Slide().setDuration(500));
        getWindow().setExitTransition(new Slide());
        setContentView(R.layout.activity_search);

        Intent intent = getIntent();
        keyword = intent.getStringExtra(EXTRA_ID);
        textViewSearch = findViewById(R.id.textView_search);
        imageViewBack = findViewById(R.id.imageView_getBack);
        textViewProgressBar = findViewById(R.id.textView_progressBar_search);
        progressBar = findViewById(R.id.progressBar_search);
        progressBar.setVisibility(View.VISIBLE);
        textViewProgressBar.setVisibility(View.VISIBLE);

        textViewSearch.setText("Search Results for " + keyword);
        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        recyclerView = findViewById(R.id.recyclerView_search);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(SearchActivity.this));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        newsItemArrayList = new ArrayList<>();
        requestQueue = VolleySingleton.getInstance(SearchActivity.this).getmRequestQueue();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                parseJson();
            }
        }, 5000);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout_search);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (newsCardsAdapter != null)
                    newsCardsAdapter.clear();
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        parseJson();
                    }
                }, 300);
            }
        });


    }

    private void parseJson(){
        String url = "http://xdtestnode-env.eba-vypzvpc6.us-east-1.elasticbeanstalk.com/hw9search/"+keyword;
        newsItemArrayList = new ArrayList<>();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("result");
                            for (int i = 0; i < jsonArray.length(); i++){
                                JSONObject result = jsonArray.getJSONObject(i);
                                String imageUrl = result.getString("image");
                                String summary = result.getString("title");
                                String time = result.getString("date");
                                String section = result.getString("section");
                                String newsId = result.getString("searchId");
                                String webUrl = result.getString("webUrl");
                                newsItemArrayList.add(new NewsItem(imageUrl,summary,time,section,webUrl,newsId));
                            }
                            newsCardsAdapter = new NewsCardsAdapter(SearchActivity.this, newsItemArrayList);
                            recyclerView.setAdapter(newsCardsAdapter);
                            progressBar.setVisibility(View.GONE);
                            textViewProgressBar.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                            hasCreated = true;
                            if (swipeRefreshLayout.isRefreshing()){
                                swipeRefreshLayout.setRefreshing(false);
                            }
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



    @Override
    public void onStart() {
        super.onStart();
        Log.e(TAG,"search 刷新");
        if (hasCreated){
            newsCardsAdapter.notifyDataSetChanged();
        }
    }
}
