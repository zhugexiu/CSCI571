package com.xudong.mynewsapplication.mainFragment;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.squareup.picasso.Picasso;
import com.xudong.mynewsapplication.DetailActivity;
import com.xudong.mynewsapplication.card.NewsCardsAdapter;
import com.xudong.mynewsapplication.card.NewsItem;
import com.xudong.mynewsapplication.card.NoFirstLineItemDecoration;
import com.xudong.mynewsapplication.R;
import com.xudong.mynewsapplication.VolleySingleton;
import com.xudong.mynewsapplication.card.WeatherCard;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {
    public static final String EXTRA_ID = "newsId";
    private boolean hasCreated = false;


    private RecyclerView recyclerView;
    private NewsCardsAdapter newsCardsAdapter;
    private ArrayList newsItemArrayList;
    private RequestQueue requestQueue;

    private static final String TAG = "hello";
    private HomeViewModel mViewModel;
    private SwipeRefreshLayout swipeRefreshLayout;

    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    //location
    private double latitude;
    private double longitude;
    private String city;
    private String state;
    //progress
    private TextView textView_progressBar;
    private ProgressBar progressBar_home;
    //dialog
    private ImageView imageViewDialog;
    private TextView textViewDialog;
    private ImageView imageViewTwitter;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.home_fragment, container, false);
    }
//    String url1 = "https://cdn.pixabay.com/photo/2020/04/03/15/27/flower-meadow-4999277_1280.jpg";
//    String url2 = "https://cdn.pixabay.com/photo/2020/02/19/11/01/city-4861938_1280.jpg";

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        // TODO: Use the ViewModel
        //open progress bar
        textView_progressBar = getView().findViewById(R.id.textView_progressBar);
        progressBar_home = getView().findViewById(R.id.progressBar_home);
        progressBar_home.setVisibility(View.VISIBLE);
        textView_progressBar.setVisibility(View.VISIBLE);
        //LOCATION
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
        )!= PackageManager.PERMISSION_GRANTED){
//            ActivityCompat.requestPermissions(
//                    getActivity(),
//                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                    REQUEST_CODE_LOCATION_PERMISSION
//            );
            HomeFragment.this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE_LOCATION_PERMISSION);
        } else {
            getCurrentLocation();
        }

        recyclerView = getView().findViewById(R.id.recyclerView_home);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), VERTICAL);
//        recyclerView.addItemDecoration(dividerItemDecoration);
        RecyclerView.ItemDecoration dividerItemDecoration = new NoFirstLineItemDecoration(ContextCompat.getDrawable(getContext(), R.drawable.divider));
        recyclerView.addItemDecoration(dividerItemDecoration);
//        requestQueue = Volley.newRequestQueue(getContext());
        requestQueue = VolleySingleton.getInstance(getContext()).getmRequestQueue();
//        newsItemArrayList = new ArrayList<>();

//        parseJson();
//        RequestQueue queue = Volley.newRequestQueue(getContext());
        swipeRefreshLayout = getView().findViewById(R.id.swipeRefreshLayout_home);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
//                newsItemArrayList.clear();
                getCurrentLocation();
//                newsCardsAdapter.clear();
//                parseJson();
            }
        });

    }

    private void parseJson() {
        String url = "http://xdtestnode-env.eba-vypzvpc6.us-east-1.elasticbeanstalk.com/hw9home/guardian";
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
                            progressBar_home.setVisibility(View.GONE);
                            textView_progressBar.setVisibility(View.GONE);
                            recyclerView.setAdapter(newsCardsAdapter);
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
//
////                                    dialog.setContentView(R.layout.dialog2);
////                                    imageViewDialog = dialog.findViewById(R.id.imageView_dialogPic2);
////                                    textViewDialog = dialog.findViewById(R.id.textViewDialog2);
////                                    imageViewTwitter = dialog.findViewById(R.id.imageView_dialogTwitter2);
//////                                    Picasso.with(getContext()).setLoggingEnabled(true);
////                                    Picasso.with(getContext()).load(clickedItem.getImageUrl()).fit().centerInside().into(imageViewDialog);
////                                    textViewDialog.setText(clickedItem.getSummary());
//
//                                    dialog.show();
//                                    //Log.i(TAG,"LONG CLICK: "+clickedItem.getImageUrl());
//
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

    private void parseWeather(){
        String url = "https://api.openweathermap.org/data/2.5/weather?q="+city+"&units=metric&appid=4f3a4ba892064d114756b84ddc6340f2";
//        Log.i(TAG,url);
        newsItemArrayList = new ArrayList<>();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int temp = (int) Math.round(response.getJSONObject("main").getDouble("temp"));
                            String type = response.getJSONArray("weather").getJSONObject(0).getString("main");
                            String tempString = temp+" \u2103";
//                            Log.i(TAG, "temp: "+tempString);
//                            Log.i(TAG,"type: "+type);
                            String imageUrl;
                            switch (type){
                                case "Clouds" :
                                    imageUrl = "https://csci571.com/hw/hw9/images/android/cloudy_weather.jpg";
                                    break;
                                case "Clear" :
                                    imageUrl = "https://csci571.com/hw/hw9/images/android/clear_weather.jpg";
                                    break;
                                case "Snow" :
                                    imageUrl = "https://csci571.com/hw/hw9/images/android/snowy_weather.jpeg";
                                    break;
                                case "Rain":
                                case "Drizzle":
                                    imageUrl = "https://csci571.com/hw/hw9/images/android/rainy_weather.jpg";
                                    break;
                                case "Thunderstorm":
                                    imageUrl = "https://csci571.com/hw/hw9/images/android/thunder_weather.jpg";
                                    break;
                                default :
                                    imageUrl = "https://csci571.com/hw/hw9/images/android/sunny_weather.jpg";
                            }
//                            Log.i(TAG, "imageurl: " + imageUrl);
//                            String webUrl = "https://csci571.com/hw/hw9/images/android/cloudy_weather.jpg";
                            newsItemArrayList.add(new WeatherCard(imageUrl, city, state, tempString, type));
                            newsCardsAdapter = new NewsCardsAdapter(getContext(), newsItemArrayList);
                            recyclerView.setAdapter(newsCardsAdapter);
                            parseJson();
                            hasCreated = true;
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

    private void getCurrentLocation(){


        final LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationServices.getFusedLocationProviderClient(getActivity())
                .requestLocationUpdates(locationRequest, new LocationCallback(){
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient(getActivity())
                                .removeLocationUpdates(this);
                        if (locationResult != null && locationResult.getLocations().size() > 0){
                            int latestLocationIndex = locationResult.getLocations().size() - 1;
                                latitude =
                                    locationResult.getLocations().get(latestLocationIndex).getLatitude();
                                longitude =
                                    locationResult.getLocations().get(latestLocationIndex).getLongitude();
//                            Log.i(TAG, "lati: "+ latitude + " long: "+ longitude);
                            Geocoder geoCoder = new Geocoder(getContext(), Locale.getDefault());
                            city = "";
                            state = "";
                            try {
                                List<Address> addresses = geoCoder.getFromLocation(latitude, longitude, 1);
                                String add = "";
                                if (addresses.size() > 0) {
                                    city = addresses.get(0).getLocality();
                                    state = addresses.get(0).getAdminArea();
                                }
                            }
                            catch (IOException e) {
                                e.printStackTrace();
                            }
//                            Log.i(TAG, "city: "+ city);
//                            Log.i(TAG, "state: "+ state);
                            parseWeather();
                        }
                    }
                }, Looper.getMainLooper());

    }

    @Override
    public void onStart() {
        super.onStart();
//        Log.e(TAG,"在这刷新");
        if (hasCreated){
            newsCardsAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION && grantResults.length > 0){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getCurrentLocation();
            } else {
                Toast.makeText(getContext(),"Permission denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
