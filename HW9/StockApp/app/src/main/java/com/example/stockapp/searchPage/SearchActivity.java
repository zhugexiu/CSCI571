package com.example.stockapp.searchPage;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.transition.Slide;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.stockapp.FavoriteAdapter;
import com.example.stockapp.MainActivity;
import com.example.stockapp.R;
import com.example.stockapp.VolleySingleton;
import com.example.stockapp.card.NewsCardsAdapter;
import com.example.stockapp.card.NewsItem;
import com.example.stockapp.portfolioAdapter;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import static android.view.View.INVISIBLE;
import static android.widget.LinearLayout.VERTICAL;



public class SearchActivity extends AppCompatActivity {

    public static final String EXTRA_ID = "keyword";
    private static final String TAG = "hello";

    private RecyclerView recyclerView;
    private NewsCardsAdapter newsCardsAdapter;
    private ArrayList newsItemArrayList;
    private ArrayList list1;
    private RequestQueue requestQueue;
    private mySharedPreference mSharedPreference;

    private boolean hasCreated = false;
    private String keyword;
    private TextView textViewSearch;
    private TextView textViewProgressBar;

    private ProgressBar progressBar;
    private ImageView imageViewBack;
    private ScrollView scrollViewDetail;

    private TextView ticker;
    private TextView name;
    private TextView price;
    private TextView changeprice;
    private WebView WebView;
    private TextView desc;
    private TextView show;
    private boolean isshow;
    private GridView simpleList;
    private ArrayList list=new ArrayList<>();

    private TextView resfirst;
    private TextView titlefirst;
    private TextView timefirst;
    private ImageView imagefirst;
    private CardView cardView1;
    private TextView portfolio;
    private TextView portfolio2;

    private ImageButton imageButton;
    private favoriteItem favoriteItem;
    private Button trade;

    private TextView title;
    private TextView info;
    private TextView buy_info;
    private EditText share;
    private Button buy;
    private Button sell;

    private String chartData= "";
    private FavoriteAdapter favoriteAdapter;
    private com.example.stockapp.portfolioAdapter portfolioAdapter;
    private ArrayList<favoriteItem> list3 = new ArrayList<>();
    private ArrayList<PortItem> list2 = new ArrayList<>();
    private List<favoriteItem> markedList = new ArrayList<>();
    private Boolean hasParsonCreated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().setEnterTransition(new Slide().setDuration(500));
        getWindow().setExitTransition(new Slide());
        setContentView(R.layout.activity_search);

        //获取keyword
        Intent intent = getIntent();
        keyword = intent.getStringExtra(EXTRA_ID);

        mSharedPreference = new mySharedPreference();
        scrollViewDetail = findViewById(R.id.ScrollView_detail);
        WebView = findViewById(R.id.WebView);
        cardView1 = findViewById(R.id.CardView1);


        textViewSearch = findViewById(R.id.textView_search);
        imageViewBack = findViewById(R.id.imageView_getBack);
        textViewProgressBar = findViewById(R.id.textView_progressBar_search);
        progressBar = findViewById(R.id.progressBar_search);

        scrollViewDetail.setVisibility(View.GONE);

        if(scrollViewDetail.getVisibility()==View.GONE) {
            progressBar.setVisibility(View.VISIBLE);
            textViewProgressBar.setVisibility(View.VISIBLE);
        }else{
            progressBar.setVisibility(View.GONE);
            textViewProgressBar.setVisibility(View.GONE);
        }



        imageButton = findViewById(R.id.imageButton);
        imageButton.setOnClickListener(v -> {
            String tag = imageButton.getTag().toString();
            if (tag.equals("no")) {
                mSharedPreference.addFavorite(SearchActivity.this, favoriteItem);
                Toast.makeText(SearchActivity.this, "\"" + favoriteItem.getfavoriteItem()
                        + "\" was added to favorites", Toast.LENGTH_SHORT).show();
                imageButton.setTag("yes");
                Drawable bookmarked = ResourcesCompat.getDrawable(v.getResources(), R.drawable.ic_baseline_star_24, null);
                imageButton.setBackground(bookmarked);
            } else {
                mSharedPreference.removeFavorite(SearchActivity.this, favoriteItem);
                Toast.makeText(SearchActivity.this, "\"" + favoriteItem.getfavoriteItem()
                        + "\" was removed from favorites", Toast.LENGTH_SHORT).show();
                imageButton.setTag("no");
                Drawable no_bookmarked = ResourcesCompat.getDrawable(v.getResources(), R.drawable.ic_baseline_star_border_24, null);
                imageButton.setBackground(no_bookmarked);
            }
        });

        mSharedPreference = new mySharedPreference();


        list3 = mSharedPreference.getFavorites(this);
        list2 = mSharedPreference.getPort(this);


        favoriteAdapter = new FavoriteAdapter(this, list3);
        portfolioAdapter = new portfolioAdapter(this, list2);


        imageViewBack.setOnClickListener(v -> onBackPressed());

        recyclerView = findViewById(R.id.recycleView_search);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(SearchActivity.this));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        newsItemArrayList = new ArrayList<>();

        //mAdapter = new MyAdapter(myDataset);
        //recyclerView.setAdapter(mAdapter);
        ticker = findViewById(R.id.ticker);
        name = findViewById(R.id.name);
        price = findViewById(R.id.price);
        changeprice = findViewById(R.id.changeprice);
        desc = findViewById(R.id.description);
        show = findViewById(R.id.show);


        imagefirst = findViewById(R.id.imageViewfirst);
        resfirst = findViewById((R.id.resfirst));
        titlefirst = findViewById(R.id.titlefirst);
        timefirst = findViewById(R.id.timefirst);

        desc.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (desc.getLineCount() > 2) {
                    desc.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    desc.setEllipsize(TextUtils.TruncateAt.END);//收起
                    desc.setLines(2);
                    show.setText("Show more..");
                    isshow = false;
                } else {
                    show.setText(null);
                }
            }
        });

        requestQueue = VolleySingleton.getInstance(SearchActivity.this).getmRequestQueue();

        WebSettings settings = WebView.getSettings();
        settings.setJavaScriptEnabled(true);
        //WebView.clearCache(true);
        settings.setDomStorageEnabled(true);
        //settings.setAllowFileAccessFromFileURLs(true);
        //settings.setAllowFileAccess(true);
        WebView.setWebViewClient(new WebViewClient());


        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isshow) {
                    //isshow=false;
                    desc.setEllipsize(TextUtils.TruncateAt.END);//收起
                    desc.setLines(2);
                    show.setText("Show more..");
                } else {
                    // isshow=true;
                    //  tv_des.setEllipsize(TextUtils.TruncateAt.END);//收起
                    desc.setEllipsize(null);//展开
                    desc.setSingleLine(false);//这个方法是必须设置的，否则无法展开
                    show.setText("Show less");
                }
                isshow = !isshow;
            }
        });
        getHist(keyword);
        parseNews();
        parseDetail();
        parsePrice();
        parseStats();

        //gridview
        simpleList = findViewById(R.id.simpleGridView);

        //buy
        portfolio = findViewById(R.id.portfolioInformation);
        portfolio2 = findViewById(R.id.portfolioInformation2);

        trade = findViewById(R.id.buy);
        list1 = new ArrayList<>();
        list1 = mSharedPreference.getValue(this);
        if (list1 == null) {
            mSharedPreference.UpdateValue(this, 20000.0);
        }
        hasCreated = true;

    }

    public void onStart() {
        super.onStart();
        if (hasCreated){
//            Log.e(TAG,"在这刷新");
            favoriteAdapter.notifyAdapterDataSetChanged();
            portfolioAdapter.notifyAdapterDataSetChanged2();
            checkNoBookmark();
        }
        if(hasParsonCreated) {
            progressBar.setVisibility(View.GONE);
            textViewProgressBar.setVisibility(View.GONE);
        }

    }

    private void getHist(String ticker) {
        String url = "http://zhugexiu.us-east-1.elasticbeanstalk.com/history/" + ticker;
        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
                            try {
                                JSONArray responseArray = response.getJSONArray("description");
                                for (int i = 0; i < responseArray.length(); i++) {
                                    chartData += responseArray.getJSONObject(i).getString("date") + ",";
                                    chartData += responseArray.getJSONObject(i).getString("open") + ",";
                                    chartData += responseArray.getJSONObject(i).getString("high") + ",";
                                    chartData += responseArray.getJSONObject(i).getString("low") + ",";
                                    chartData += responseArray.getJSONObject(i).getString("close") + ",";
                                    chartData += responseArray.getJSONObject(i).getString("volume") + ",,";
                                }
                                WebView.setWebViewClient(new WebViewClient(){
                                    public void onPageFinished(WebView view, String url){
                                        WebView.loadUrl("javascript:setData('" + chartData.substring(0, chartData.length() - 2) + "')");
                                    }
                                });
                                WebView.loadUrl("file:///android_asset/chart.html");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }, Throwable::printStackTrace);
        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy( 50000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(jsonArrayRequest);
    }

    private void parseDetail() {
        String url = "http://zhugexiu.us-east-1.elasticbeanstalk.com/details/" + keyword;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
            try {
                JSONObject jsonObject = response.getJSONObject("description");
                String Jsonticker = jsonObject.getString("ticker");
                String Jsonname = jsonObject.getString("name");
                String Jsondesc = jsonObject.getString("description");
                ticker.setText(Jsonticker);
                name.setText(Jsonname);
                desc.setText(Jsondesc);
                favoriteItem = new favoriteItem(Jsonticker,Jsonname);

                if (checkBookmarkItem(favoriteItem)){
                    imageButton.setTag("yes");
                    Drawable bookmarked = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_baseline_star_24, null);
                    imageButton.setBackground(bookmarked);
                } else {
                    imageButton.setTag("no");
                    Drawable no_bookmarked = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_baseline_star_border_24, null);
                    imageButton.setBackground(no_bookmarked);
                }

                String url2 = "http://zhugexiu.us-east-1.elasticbeanstalk.com/price/" + keyword;
                JsonObjectRequest request2 = new JsonObjectRequest(Request.Method.GET, url2, null, response2 -> {
                    try {
                        JSONArray jsonArray2 = response2.getJSONArray("description");
                        JSONObject jsonObject2 = jsonArray2.getJSONObject(0);
                        double last = Double.parseDouble(jsonObject2.getString("last"));
                        if(checkPortItem(Jsonticker)) {
                            List<PortItem> PortItemInSharedPreference = mSharedPreference.getPort(SearchActivity.this);
                            if(PortItemInSharedPreference != null) {
                                for (PortItem i : PortItemInSharedPreference) {
                                    if (i.getPortItem().equals(Jsonticker)) {
                                        Integer sharem = i.getPortItemName();
                                        double value = sharem * last;
                                        portfolio.setText("Shares Owned: " + sharem);
                                        portfolio2.setText("Market Value: $" + value);
                                    }
                                }
                            }
                        }else{
                            portfolio.setText("You have 0 shares of " + Jsonticker);
                            portfolio2.setText("Shart tradings!");
                        }

                        trade.setOnClickListener(v -> {
                            Dialog dialog = new Dialog(v.getContext());
                            dialog.setContentView(R.layout.dialog_trade);
                            AlertDialog.Builder alert = new AlertDialog.Builder(this);
                            title = dialog.findViewById(R.id.D1_Title);
                            info = dialog.findViewById(R.id.D1_info);
                            buy_info = dialog.findViewById(R.id.D1_buy_info);
                            share = dialog.findViewById(R.id.D1_share);
                            buy = dialog.findViewById(R.id.button_buy);
                            sell = dialog.findViewById(R.id.button_sell);
                            list1 = new ArrayList<>();
                            list1 = mSharedPreference.getValue(this);
                            Double value = (Double) list1.get(0);
                            //Log.v(TAG, "parseDetail: "+ value);
                            info.setText("0 x $"+last+"/share = $0");
                            title.setText("Trade " + Jsonname + " shares");

                            share.addTextChangedListener(new TextWatcher(){
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                    info.setText("0 x $"+last+"/share = $0");
                                }

                                public void onTextChanged(CharSequence s, int start, int before, int count) {
                                    if(s.length() == 0) {
                                        info.setText(s+" x $"+last+"/share = $0");
                                    }else{
                                        double sv = Double.parseDouble(s.toString());
                                        double all = sv * last;
                                        info.setText(s+" x $"+last+"/share = $"+all);
                                    }

                                }
                                public void afterTextChanged(Editable s) {
                                    if(share.getText().toString().isEmpty()){
                                        info.setText("0 x $"+last+"/share = $0");

                                    }else{

                                    double sv = Double.parseDouble(share.getText().toString());
                                    Double all = sv * last;
                                    buy.setOnClickListener(v1 -> {
                                        if(sv<=0) {
                                            Toast.makeText(SearchActivity.this, "Cannot buy less than 0 shares", Toast.LENGTH_SHORT).show();
                                        }else if(all > value){
                                            Toast.makeText(SearchActivity.this, "Not enough money to buy", Toast.LENGTH_SHORT).show();
                                        }else{
                                            double after = value - all;
                                            mSharedPreference.UpdateValue(getApplicationContext(),after);
                                            ArrayList<PortItem> list = mSharedPreference.getPort(getApplicationContext());
                                            if(list == null){
                                                ArrayList<PortItem> newlist = new ArrayList<>();
                                                newlist.add(new PortItem(Jsonticker,Integer.parseInt(share.getText().toString())));
                                                mSharedPreference.savePort(getApplicationContext(),newlist);
                                            }else{
                                                if(checkPortItem(Jsonticker)) {
                                                    for(int i = 0; i < list.size();i++) {
                                                        if (list.get(i).getPortItem().equals(Jsonticker)){
                                                            Integer share_number = list.get(i).getPortItemName() + Integer.parseInt(share.getText().toString());
                                                            //list.remove(i);
                                                            //list.add(new PortItem(Jsonticker,share_number));
                                                            list.set(i,new PortItem(Jsonticker,share_number));
                                                            mSharedPreference.savePort(getApplicationContext(),list);
                                                            //mSharedPreference.removePort(getApplicationContext(),i);
                                                            //PortItem new_P = new PortItem(Jsonticker,share_number);
                                                            //mSharedPreference.addPort(getApplicationContext(),new_P);
                                                            break;
                                                        }
                                                    }
                                                }else{
                                                    list.add(new PortItem(Jsonticker,Integer.parseInt(share.getText().toString())));
                                                    mSharedPreference.savePort(getApplicationContext(),list);
                                                }
                                            }

                                            List<PortItem> PortItemInSharedPreference = mSharedPreference.getPort(SearchActivity.this);
                                            if(PortItemInSharedPreference != null) {
                                                for (PortItem i : PortItemInSharedPreference) {
                                                    if (i.getPortItem().equals(Jsonticker)) {
                                                        Integer sharem = i.getPortItemName();
                                                        double value = sharem * last;
                                                        portfolio.setText("Shares Owned: " + sharem);
                                                        portfolio2.setText("Market Value: $" + value);
                                                        break;
                                                    }
                                                }
                                            }
                                            dialog.dismiss();
                                            Dialog dialog2 = new Dialog(v.getContext());
                                            dialog2.setContentView(R.layout.cong);
                                            TextView dialog_t1 = dialog2.findViewById(R.id.con_text1);
                                            ImageButton dialog2_button = dialog2.findViewById(R.id.dialog2_button);
                                            dialog_t1.setText("You have successfully bought " + share.getText().toString() + " shares of " + Jsonticker);
                                            dialog2_button.setOnClickListener(v_dialog2 ->{
                                                dialog2.dismiss();
                                            });
                                            dialog2.show();
                                        }
                                    });

                                    sell.setOnClickListener(v2 -> {
                                        if(sv<=0) {
                                            Toast.makeText(SearchActivity.this, "Cannot sell less than 0 shares", Toast.LENGTH_SHORT).show();
                                        }else{
                                            ArrayList<PortItem> list = mSharedPreference.getPort(getApplicationContext());
                                            if(list == null) {
                                                Toast.makeText(SearchActivity.this, "Not enough shares to sell", Toast.LENGTH_SHORT).show();
                                            }else{
                                                if(checkPortItem(Jsonticker)) {
                                                    for(int i = 0; i < list.size();i++) {
                                                        if (list.get(i).getPortItem().equals(Jsonticker)){
                                                            Integer original_share_number = list.get(i).getPortItemName();
                                                            if(original_share_number<Integer.parseInt(share.getText().toString())) {
                                                                Toast.makeText(SearchActivity.this, "Not enough shares to sell", Toast.LENGTH_SHORT).show();
                                                            }else{
                                                                Integer share_number = original_share_number - Integer.parseInt(share.getText().toString());
                                                                if(share_number == 0) {
                                                                    mSharedPreference.removePort(getApplicationContext(),new PortItem(Jsonticker,original_share_number));
                                                                    portfolio.setText("You have 0 shares of " + Jsonticker);
                                                                    portfolio2.setText("Shart tradings!");
                                                                }else{
                                                                    list.set(i,new PortItem(Jsonticker,share_number));
                                                                    mSharedPreference.savePort(getApplicationContext(),list);
                                                                }
                                                                double after = value + all;
                                                                mSharedPreference.UpdateValue(getApplicationContext(),after);
                                                                List<PortItem> PortItemInSharedPreference = mSharedPreference.getPort(SearchActivity.this);
                                                                if(PortItemInSharedPreference != null) {
                                                                    if(share_number==0) {
                                                                        portfolio.setText("You have 0 shares of " + Jsonticker);
                                                                        portfolio2.setText("Shart tradings!");
                                                                    }else{
                                                                        double value = share_number * last;
                                                                        portfolio.setText("Shares Owned: " + share_number);
                                                                        portfolio2.setText("Market Value: $" + value);
                                                                    }



                                                                }else{
                                                                    portfolio.setText("You have 0 shares of " + Jsonticker);
                                                                    portfolio2.setText("Shart tradings!");
                                                                }

                                                                dialog.dismiss();
                                                                Dialog dialog2 = new Dialog(v.getContext());
                                                                dialog2.setContentView(R.layout.cong);
                                                                TextView dialog_t1 = dialog2.findViewById(R.id.con_text1);
                                                                ImageButton dialog2_button = dialog2.findViewById(R.id.dialog2_button);
                                                                dialog_t1.setText("You have successfully sold " + share.getText().toString() + " shares of " + Jsonticker);
                                                                dialog2_button.setOnClickListener(v_dialog2 ->{
                                                                    dialog2.dismiss();
                                                                });
                                                                dialog2.show();
                                                                break;
                                                            }
                                                        }
                                                    }

                                                }else{
                                                    Toast.makeText(SearchActivity.this, "Not enough shares to sell", Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                        }

                                    });

                                }}
                            });
                            String value_String = String.format("%.2f", value);
                            buy_info.setText("$"+value_String+" available to buy "+Jsonticker);



                            dialog.show();
                        });

                        textViewProgressBar.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        scrollViewDetail.setVisibility(View.VISIBLE);
                        WebView.setVisibility(View.VISIBLE);
                        hasParsonCreated = true;
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, Throwable::printStackTrace);
                requestQueue.add(request2);



            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, Throwable::printStackTrace);
        requestQueue.add(request);
    }

    public void checkNoBookmark(){
        markedList = mSharedPreference.getFavorites(this);
        if (markedList == null || markedList.size() == 0){
            //noBookmark.setVisibility(View.VISIBLE);
        } else{
            //noBookmark.setVisibility(View.GONE);
        }

    }

    private void parsePrice() {
        String url = "http://zhugexiu.us-east-1.elasticbeanstalk.com/price/" + keyword;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
            try {
                JSONArray jsonArray = response.getJSONArray("description");
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                String Jsonprice = "$" + jsonObject.getString("last");
                price.setText(Jsonprice);
                String Jsonprice2 = jsonObject.getString("prevClose");
                double last = Double.parseDouble(jsonObject.getString("last"));
                double prev = Double.parseDouble(Jsonprice2);
                double c = last-prev;
                if(c==0){
                    String s1 = "$"+String.format("%.2f", c);
                    changeprice.setText(Html.fromHtml("<font color='gray'>" + s1 + "</font>"));
                }else if(c > 0) {
                    String s2 = "$"+String.format("%.2f", c);
                    changeprice.setText(Html.fromHtml("<font color='green'>" + s2 + "</font>"));
                }else{
                    String s3 = "-$"+String.format("%.2f", Math.abs(c));
                    changeprice.setText(Html.fromHtml("<font color='red'>" + s3 + "</font>"));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, Throwable::printStackTrace);
        requestQueue.add(request);
    }

    private void parseStats() {
        String url = "http://zhugexiu.us-east-1.elasticbeanstalk.com/price/" + keyword;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
            try {
                JSONArray jsonArray = response.getJSONArray("description");
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                list.add(new Item("Current Price: "+jsonObject.getString("last")));
                list.add(new Item("Low: "+jsonObject.getString("low")));
                if(jsonObject.getString("bidPrice").equals("null")) {
                    list.add(new Item("Bid Price: 0"));
                }else{
                    list.add(new Item("Bid Price: "+jsonObject.getString("bidPrice")));
                }

                list.add(new Item("Open Price: "+jsonObject.getString("open")));

                if(jsonObject.getString("mid").equals("null")){
                    list.add(new Item("Mid: 0"));
                }else{
                    list.add(new Item("Mid: "+jsonObject.getString("mid")));
                }
                list.add(new Item("High: "+jsonObject.getString("high")));
                list.add(new Item("Volume: "+jsonObject.getString("volume")));
                Log.d(TAG, "hhhhhhhhhh"+1);
                MyAdapter myAdapter = new MyAdapter(this,R.layout.grid_view_items,list);
                simpleList.setAdapter(myAdapter);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, Throwable::printStackTrace);
        requestQueue.add(request);
    }

    private void parseNews() {
        String url = "http://zhugexiu.us-east-1.elasticbeanstalk.com/hw9news/" + keyword;
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONObject firstres = response.getJSONObject(0);
                        String u = firstres.getString("urlToImage");
                        String URL = firstres.getString("url");
                        String res1 = firstres.getJSONObject("source").getString("name");
                        String title1 = firstres.getString("title");
                        String time1 = getFormattedTime(firstres.getString("publishedAt"));
                        Picasso.get().load(u).fit().centerInside().into(imagefirst);
                        resfirst.setText(firstres.getJSONObject("source").getString("name"));
                        titlefirst.setText(firstres.getString("title"));
                        timefirst.setText(getFormattedTime(firstres.getString("publishedAt")));

                        cardView1.setOnClickListener(v1 -> {
                            Intent intent = new Intent();
                            intent.setData(Uri.parse(URL));
                            intent.setAction(Intent.ACTION_VIEW);
                            this.startActivity(intent);
                        });


                        cardView1.setOnLongClickListener(v -> {
                            final NewsItem clickedItem = new NewsItem(u, title1, time1, URL, res1);
                            Dialog dialog = new Dialog(v.getContext());
                            dialog.setContentView(R.layout.dialog);
                            ImageView imageViewDialog = dialog.findViewById(R.id.imageView_dialogPic);
                            TextView textViewDialog = dialog.findViewById(R.id.textViewDialog);
                            ImageView imageViewTwitter = dialog.findViewById(R.id.imageView_dialogTwitter);
                            ImageView imageViewChrome = dialog.findViewById(R.id.imageView_dialogChrome);

                            Picasso.get().load(clickedItem.getImageUrl()).fit().centerInside().into(imageViewDialog);
                            textViewDialog.setText(clickedItem.getSummary());
                            imageViewTwitter.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/intent/tweet?text=Check out this Link: "
                                            + clickedItem.getmWebUrl() + "&hashtags=CSCI571NEWS"));
                                    startActivity(intent);
                                }
                            });
                            imageViewChrome.setOnClickListener(v1 -> {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse( clickedItem.getmWebUrl()));
                                    startActivity(intent);
                            });
                            dialog.show();
                            return true;
                        });


                        for (int i = 1; i < response.length(); i++) {
                            JSONObject result = response.getJSONObject(i);
                            String imageUrl = result.getString("urlToImage");
                            String summary = result.getString("title");
                            String time = result.getString("publishedAt");
                            String webUrl = result.getString("url");
                            String res = result.getJSONObject("source").getString("name");
                            newsItemArrayList.add(new NewsItem(imageUrl, summary, time, webUrl, res));
                        }
                        newsCardsAdapter = new NewsCardsAdapter(SearchActivity.this, newsItemArrayList);
                        recyclerView.setAdapter(newsCardsAdapter);
                        recyclerView.setVisibility(View.VISIBLE);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, Throwable::printStackTrace
        );
        requestQueue.add(request);
    }

    private String getFormattedTime(String oTime){
        String str = "";
        String suffix = "ago";

        Date nowTime = new Date();
        Instant instant = Instant.parse(oTime);
        ZoneId z = ZoneId.of( "America/Los_Angeles" );
        ZonedDateTime zdt = instant.atZone(z);
        Date newsTime = Date.from(zdt.toInstant());
        long dateDiff = nowTime.getTime() - newsTime.getTime();
        long second = TimeUnit.MILLISECONDS.toSeconds(dateDiff);
        long minute = TimeUnit.MILLISECONDS.toMinutes(dateDiff);
        long hour   = TimeUnit.MILLISECONDS.toHours(dateDiff);
        long day  = TimeUnit.MILLISECONDS.toDays(dateDiff);
        if (second < 60) {
            str = second+"s "+suffix;
        } else if (minute < 60) {
            str = minute+"m "+suffix;
        } else if (hour < 24) {
            str = hour+"h "+suffix;
        } else{
            str = day+"d "+suffix;
        }
        return str;
    }

    public boolean checkBookmarkItem(favoriteItem newsItem){
        boolean check = false;
        List<favoriteItem> BookmarkedItemsInSharedPreference = mSharedPreference.getFavorites(SearchActivity.this);
        if (BookmarkedItemsInSharedPreference != null) {
            for (favoriteItem news : BookmarkedItemsInSharedPreference) {
                if (news.equals(newsItem)) {
                    check = true;
                    break;
                }
            }
        }
        return check;
    }

    public boolean checkPortItem(String p) {
        boolean check = false;
        List<PortItem> PortItemInSharedPreference = mSharedPreference.getPort(SearchActivity.this);
        if(PortItemInSharedPreference != null) {
            for (PortItem i : PortItemInSharedPreference) {
                if (i.getPortItem().equals(p)) {
                    check = true;
                    break;
                }
            }
        }
        return check;
    }

}
