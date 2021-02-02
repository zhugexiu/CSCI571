package com.xudong.mynewsapplication.headlinePager;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;

import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.xudong.mynewsapplication.card.NoFirstLineItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.widget.LinearLayout.VERTICAL;

public class PlaceholderFragment extends Fragment {
    public static final String EXTRA_ID = "newsId";
    private boolean isStarted = false;
    private boolean isVisible = false;
    private boolean hasCreated = false;

    private static final String ARG_SECTION_NUMBER = "section_number";
    private PlaceholderViewModel mViewModel;
    private String section;
    private static final String TAG = "hello";
    //recycle
    private RecyclerView recyclerView;
    private NewsCardsAdapter newsCardsAdapter;
    private ArrayList newsItemArrayList;
    private RequestQueue requestQueue;
    private SwipeRefreshLayout swipeRefreshLayout;
    //progress
    private TextView textView_progressBar;
    private ProgressBar progressBar_other;
    //dialog
    private ImageView imageViewDialog;
    private TextView textViewDialog;
    private ImageView imageViewTwitter;

    public static PlaceholderFragment newInstance() {
        return new PlaceholderFragment();
    }

    public static PlaceholderFragment newInstance(int index) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(PlaceholderViewModel.class);
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        switch (index){
            case 1:
                section = "world";
                break;
            case 2:
                section = "business";
                break;
            case 3:
                section = "politics";
                break;
            case 4:
                section = "sports";
                break;
            case 5:
                section = "technology";
                break;
            case 6:
                section = "science";
                break;
        }
        mViewModel.setIndex(index);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_main, container, false);

        textView_progressBar = root.findViewById(R.id.textView_progressBar);
        progressBar_other = root.findViewById(R.id.progressBar_other);
        progressBar_other.setVisibility(View.VISIBLE);
        textView_progressBar.setVisibility(View.VISIBLE);
        recyclerView = root.findViewById(R.id.recyclerView_other);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//        RecyclerView.ItemDecoration dividerItemDecoration = new NoFirstLineItemDecoration(ContextCompat.getDrawable(getContext(), R.drawable.divider));
//        recyclerView.addItemDecoration(dividerItemDecoration);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        requestQueue = VolleySingleton.getInstance(getContext()).getmRequestQueue();
        swipeRefreshLayout = root.findViewById(R.id.swipeRefreshLayout_other);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (newsCardsAdapter != null)
                    newsCardsAdapter.clear();
                parseJson();
            }
        });
//        final TextView textView = root.findViewById(R.id.section_label);
//        mViewModel.getText().observe(this, new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(PlaceholderViewModel.class);
        // TODO: Use the ViewModel


    }

    @Override
    public void onStart() {
        super.onStart();
        isStarted = true;
//        Log.e(TAG,"fragment start: "+ section);
        if (isVisible && hasCreated){
            newsCardsAdapter.notifyDataSetChanged();
//            Log.e(TAG,"在这刷新");
        }

        if (isVisible && !hasCreated){
            parseJson(); //your request method
//            Log.e(TAG,"onStart 且看见: "+ section);
            hasCreated = true;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        isStarted = false;
//        Log.e(TAG,"fragment stop: "+ section);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        isVisible = isVisibleToUser;
        if (isVisible && isStarted){
//            Log.e(TAG,"看见！！"+section);
            parseJson(); //your request method
        }
    }




    private void parseJson() {
        String url = "http://xdtestnode-env.eba-vypzvpc6.us-east-1.elasticbeanstalk.com/hw9other/?source=guardian&section="+section;
//        Log.i(TAG,"reload "+section+ " progress:"+progressBar_other.getVisibility());
//        Log.i(TAG,"reload " +section+ "progressBar:"+progressBar_other.getVisibility());
//        progressBar_other.setVisibility(View.VISIBLE);
////        textView_progressBar.setVisibility(View.VISIBLE);
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
                            newsCardsAdapter = new NewsCardsAdapter(getContext(), newsItemArrayList);
                            recyclerView.setAdapter(newsCardsAdapter);
                            progressBar_other.setVisibility(View.GONE);
                            textView_progressBar.setVisibility(View.GONE);
//                            Log.i(TAG,"reload "+section+ "finished progressBar:"+progressBar_other.getVisibility());
//                            NewsCardsAdapter.OnItemClickListener newsAdapter = new NewsCardsAdapter.OnItemClickListener() {
//                                @Override
//                                public void onItemClick(int position) {
//                                    Intent detailIntent = new Intent(getContext(), DetailActivity.class);
//                                    NewsItem clickedItem = (NewsItem) newsItemArrayList.get(position);
//                                    detailIntent.putExtra(EXTRA_ID, clickedItem.getNewsId());
//                                    startActivity(detailIntent, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
//                                }
//                            };
//                            NewsCardsAdapter.OnItemLongClickListener newsLongAdapter = new NewsCardsAdapter.OnItemLongClickListener() {
//                                @Override
//                                public void onItemLongClick(int position) {
//                                    final NewsItem clickedItem = (NewsItem) newsItemArrayList.get(position);
//                                    Dialog dialog = new Dialog(getContext());
//
//                                    dialog.setContentView(R.layout.dialog);
//                                    imageViewDialog = dialog.findViewById(R.id.imageView_dialogPic);
//                                    textViewDialog = dialog.findViewById(R.id.textViewDialog);
//                                    imageViewTwitter = dialog.findViewById(R.id.imageView_dialogTwitter);
//                                    Picasso.with(getContext()).load(clickedItem.getImageUrl()).fit().centerInside().into(imageViewDialog);
//                                    textViewDialog.setText(clickedItem.getSummary());
//                                    imageViewTwitter.setOnClickListener(new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View v) {
//                                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/intent/tweet?text=Check out this Link: "
//                                                    + clickedItem.getmWebUrl() + "&hashtags=CSCI571NEWS"));
//                                            startActivity(intent);
//                                        }
//                                    });
//                                    dialog.show();
//                                }
//                            };
//                            newsCardsAdapter.setOnItemClickListener(newsAdapter);
//                            newsCardsAdapter.setOnItemLongClickListener(newsLongAdapter);
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

}
