package com.example.stockapp;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.stockapp.API.ApiCall;
import com.example.stockapp.API.AutoSuggestAdapter;
import com.example.stockapp.searchPage.PortItem;
import com.example.stockapp.searchPage.SearchActivity;
import com.example.stockapp.searchPage.favoriteItem;
import com.example.stockapp.searchPage.mySharedPreference;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static android.widget.GridLayout.VERTICAL;

public class MainActivity extends AppCompatActivity {
    private AutoSuggestAdapter autoSuggestAdapter;
    private Handler handler;
    Toolbar toolbar;
    public static final String EXTRA_ID = "keyword";
    private static final int TRIGGER_AUTO_COMPLETE = 100;
    private static final long AUTO_COMPLETE_DELAY = 300;
    private static final String TAG = "hello";

    private ProgressBar progressBar;
    private TextView progressBar_Text;

    private RecyclerView recyclerView1;
    private RecyclerView recyclerView2;
    private ArrayList<favoriteItem> list = new ArrayList<>();
    private ArrayList<PortItem> list2 = new ArrayList<>();
    private ArrayList<showFavoriteItem> newlist = new ArrayList<>();
    private mySharedPreference mySharedPreference;
    private boolean hasCreated = false;
    private FavoriteAdapter favoriteAdapter;
    private portfolioAdapter portfolioAdapter;
    private RequestQueue requestQueue;
    private List<favoriteItem> markedList = new ArrayList<>();
    private ConstraintLayout constraintLayout;
    private TextView Time;
    private ScrollView scrollView;
    private Toolbar toobar_bottom;
    private TextView bottom;
    private TextView Worth_Number;
    private ArrayList<Double> list1 = new ArrayList<>();
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Make sure this is before calling super.onCreate
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar=findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        constraintLayout = findViewById(R.id.ConstraintLayout);

        //favorite
        mySharedPreference = new mySharedPreference();
        requestQueue = VolleySingleton.getInstance(MainActivity.this).getmRequestQueue();
        list = mySharedPreference.getFavorites(this);
        list2 = mySharedPreference.getPort(this);
        list1 = mySharedPreference.getValue(this);
        if(list1 == null) {
            mySharedPreference.UpdateValue(this,20000.0);
        }
        progressBar= findViewById(R.id.progressBar_main);
        progressBar_Text = findViewById(R.id.textView_progressBar_main);
        progressBar.setVisibility(View.VISIBLE);
        progressBar_Text.setVisibility(View.VISIBLE);
        recyclerView1 = findViewById(R.id.recyclerView_F);
        recyclerView2 = findViewById(R.id.recyclerView_P);
        scrollView = findViewById(R.id.ScrollView_main);
        scrollView.setVisibility(View.INVISIBLE);
        scrollView.smoothScrollTo(0,0);
        toobar_bottom = findViewById(R.id.toolbarBottom);
        toobar_bottom.setVisibility(View.INVISIBLE);
        bottom = findViewById(R.id.textView4);
        Worth_Number = findViewById(R.id.Worth_number);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        recyclerView1.setLayoutManager(gridLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView1.getContext(), VERTICAL);
        recyclerView1.addItemDecoration(dividerItemDecoration);

        GridLayoutManager gridLayoutManager2 = new GridLayoutManager(this, 1);
        recyclerView2.setLayoutManager(gridLayoutManager2);
        DividerItemDecoration dividerItemDecoration2 = new DividerItemDecoration(recyclerView2.getContext(), VERTICAL);
        recyclerView2.addItemDecoration(dividerItemDecoration2);

        if(list == null)
            list = new ArrayList<>();
        if(list2 == null)
            list2 = new ArrayList<>();

        favoriteAdapter = new FavoriteAdapter(MainActivity.this,list);
        portfolioAdapter = new portfolioAdapter(this,list2);


        parse();

        timer=new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Log.d(TAG , "F: Get Json in every 15s");
            }
        },0,15000);


        bottom.setOnClickListener(v1 -> {
            Intent intent = new Intent();
            String url_bottom = "https://www.tiingo.com/";
            intent.setData(Uri.parse(url_bottom));
            intent.setAction(Intent.ACTION_VIEW);
            this.startActivity(intent);
        });


        Time = findViewById(R.id.Time);
        Time.setText(getTime());

        //worthAdapter = new worthAdapter(this);


        ItemTouchHelper.Callback callback = new ItemMoveCallback(favoriteAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView1);

        ItemTouchHelper.Callback callback2 = new ItemMoveCallback2(portfolioAdapter);
        ItemTouchHelper touchHelper2 = new ItemTouchHelper(callback2);
        touchHelper2.attachToRecyclerView(recyclerView2);

        //recyclerView2.setVisibility(View.GONE);
        //recyclerView1.setVisibility(View.GONE);

        hasCreated = true;
        enableSwipeToDeleteAndUndo();

    }


    public void onStart() {
        super.onStart();
        if (hasCreated){
//            Log.e(TAG,"在这刷新");
            favoriteAdapter.notifyAdapterDataSetChanged();
            portfolioAdapter.notifyAdapterDataSetChanged2();
            checkNoBookmark();
        }
    }

    public void checkNoBookmark(){
        markedList = mySharedPreference.getFavorites(this);
        if (markedList == null || markedList.size() == 0){
            //noBookmark.setVisibility(View.VISIBLE);
        } else{
            //noBookmark.setVisibility(View.GONE);
        }

    }

    public void parse() {
        if (list2.size() == 0) {
            Double value = list1.get(0);
            Worth_Number.setText(String.format("%.2f", value));
            recyclerView2.setAdapter(portfolioAdapter);

            recyclerView1.setAdapter(favoriteAdapter);

            String url = "http://zhugexiu.us-east-1.elasticbeanstalk.com/price/a";
            JsonObjectRequest requestnull = new JsonObjectRequest(Request.Method.GET, url, null, responsenull -> {
                try {
                    JSONArray jsonArray100 = responsenull.getJSONArray("description");

                    Timer UiTimer = new Timer();
                    UiTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            runOnUiThread(() -> {
                                progressBar.setVisibility(View.GONE);
                                progressBar_Text.setVisibility(View.GONE);
                                scrollView.setVisibility(View.VISIBLE);
                                toobar_bottom.setVisibility(View.VISIBLE);
                            });
                        }
                    }, 5000); // End of your timer code.

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }, Throwable::printStackTrace);
            requestQueue.add(requestnull);

        } else {
            String search = list2.get(0).getPortItem();
            for (int i = 1; i < list2.size(); i++) {
                search += "," + list2.get(i).getPortItem();
            }


            String url = "http://zhugexiu.us-east-1.elasticbeanstalk.com/price/" + search;
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
                try {
                    Double value = list1.get(0);
                    JSONArray jsonArray = response.getJSONArray("description");
                    double a = 0.0;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String ticker = jsonObject.getString("ticker");
                        String last = jsonObject.getString("last");
                        for (PortItem item : list2) {
                            if (item.getPortItem().equals(ticker)) {
                                Integer share = item.getPortItemName();
                                a += share * Double.parseDouble(last);
                                break;
                            }
                        }
                    }
                    value = value + a;
                    Worth_Number.setText(String.format("%.2f", value));
                    recyclerView2.setAdapter(portfolioAdapter);
                    //Log.d(TAG, "个数"+list2.get(1));

                    recyclerView1.setAdapter(favoriteAdapter);

                    JsonObjectRequest request100 = new JsonObjectRequest(Request.Method.GET, url, null, response100 -> {
                        try {
                            JSONArray jsonArray100 = response100.getJSONArray("description");

                            Timer UiTimer = new Timer();
                            UiTimer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    runOnUiThread(() -> {
                                        progressBar.setVisibility(View.GONE);
                                        progressBar_Text.setVisibility(View.GONE);
                                        scrollView.setVisibility(View.VISIBLE);
                                        toobar_bottom.setVisibility(View.VISIBLE);
                                    });
                                }
                            }, 5000); // End of your timer code.

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }, Throwable::printStackTrace);
                    requestQueue.add(request100);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }, Throwable::printStackTrace);
            requestQueue.add(request);
        }
    }
    @SuppressLint("RestrictedApi")
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search, menu);
        MenuItem searchItem =  menu.findItem(R.id.action_search);
        SearchView searchView = (androidx.appcompat.widget.SearchView)searchItem.getActionView();
        //get autocomplete
        final SearchView.SearchAutoComplete searchAutoComplete = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchAutoComplete.setThreshold(3);
        //searchAutoComplete.setTextCursorDrawable(R.drawable.searchbar_cursor);

        // Create a new ArrayAdapter and add data to search auto complete object.
        autoSuggestAdapter = new AutoSuggestAdapter(this,android.R.layout.simple_dropdown_item_1line);
        searchAutoComplete.setAdapter(autoSuggestAdapter);

        // Listen to search view item on click event.
        searchAutoComplete.setOnItemClickListener((adapterView, view, itemIndex, id) -> {
            String queryString=(String)adapterView.getItemAtPosition(itemIndex);
            String[] a = queryString.split("-");
            searchAutoComplete.setText("" + a[0]);
            Intent searchIntent = new Intent(MainActivity.this, SearchActivity.class);
            searchIntent.putExtra(EXTRA_ID, a[0].trim());
            startActivity(searchIntent, ActivityOptions.makeSceneTransitionAnimation(MainActivity.this).toBundle());
            //FavoriteAdapter.handler.removeCallbacksAndMessage(null);
//                Toast.makeText(MainActivity.this, "you clicked " + queryString, Toast.LENGTH_LONG).show();
        });

        //Listen to enter key
        searchAutoComplete.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                String queryString;
                if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    switch (event.getAction()) {
                        case KeyEvent.ACTION_UP:
                            if (v.getText() != null && !v.getText().toString().equals("")){
                                queryString= "" + v.getText();
                                String[] a = queryString.split("-");
                                searchAutoComplete.setText(a[0]);
                                Intent searchIntent = new Intent(MainActivity.this, SearchActivity.class);
                                searchIntent.putExtra(EXTRA_ID, a[0]);
                                startActivity(searchIntent, ActivityOptions.makeSceneTransitionAnimation(MainActivity.this).toBundle());
                            }
                            else
                                Toast.makeText(MainActivity.this, "you need to enter a keyword", Toast.LENGTH_SHORT).show();
                            return true;
                        default:
                            return true;
                    }
                }
                return false;
            }
        });

        searchAutoComplete.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handler.removeMessages(TRIGGER_AUTO_COMPLETE);
                handler.sendEmptyMessageDelayed(TRIGGER_AUTO_COMPLETE,
                        AUTO_COMPLETE_DELAY);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        handler = new Handler(msg -> {
            if (msg.what == TRIGGER_AUTO_COMPLETE) {
                if (!TextUtils.isEmpty(searchAutoComplete.getText())) {
                    makeApiCall(searchAutoComplete.getText().toString());
                }
            }
            return false;
        });
       // return true;
        return super.onCreateOptionsMenu(menu);
    }




    private void makeApiCall(String text) {
        ApiCall.make(this, text, response -> {
            //parsing logic, please change it as per your requirement
            List<String> stringList = new ArrayList<>();
            List<String> showList = new ArrayList<>();
            try {
                JSONArray array = new JSONArray(response);


                for (int i = 0; i < 5; i++) {
                    JSONObject row = array.getJSONObject(i);
                    stringList.add(row.getString("ticker"));
                    showList.add(row.getString("ticker")+" - "+row.getString("name"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            //IMPORTANT: set data here and notify
            autoSuggestAdapter.setData(showList,stringList);
            autoSuggestAdapter.notifyDataSetChanged();
        }, error -> {

        });
    }

    private void enableSwipeToDeleteAndUndo() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(this) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {


                final int position = viewHolder.getAdapterPosition();
                favoriteItem item = favoriteAdapter.getData().get(position);
                String ticker = item.getfavoriteItem();

                favoriteAdapter.removeItem(position);


                Snackbar snackbar = Snackbar
                        .make(constraintLayout, ticker +" was removed from the list.", Snackbar.LENGTH_LONG);
                snackbar.setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        favoriteAdapter.restoreItem(item, position);
                        recyclerView1.scrollToPosition(position);
                    }
                });

                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();

            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(recyclerView1);
    }


    private String getTime(){
        Date date = new Date();
        String monthString  = (String) DateFormat.format("MMMM",  date);
        String monthNumber  = (String) DateFormat.format("dd",   date);
        String year         = (String) DateFormat.format("yyyy", date);
        return monthString +" "+ monthNumber+", "+ year;
    }



}
