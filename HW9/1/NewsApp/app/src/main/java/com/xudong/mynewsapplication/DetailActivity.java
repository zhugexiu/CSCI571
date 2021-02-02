package com.xudong.mynewsapplication;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.transition.Slide;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.squareup.picasso.Picasso;
import com.xudong.mynewsapplication.bookmark.mySharedPreference;
import com.xudong.mynewsapplication.card.NewsItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.xudong.mynewsapplication.mainFragment.HomeFragment.EXTRA_ID;

public class DetailActivity extends AppCompatActivity {
    private String link;
    private mySharedPreference mSharedPreference;
    private RequestQueue requestQueue;
    private String newsId;
    private TextView textViewDesc;
    private TextView textViewTitle;
    private TextView textViewSection;
    private TextView textViewDate;
    private TextView textViewBottom;
    private ImageView imageViewDetail;
    private TextView textViewToolbarTitle;
    private ImageView imageViewToolbarTwitter;
    private ImageButton imageButton;

    private ScrollView scrollViewDetail;
    private Toolbar toolbar;
    private TextView textViewProgressBar;
    private ProgressBar progressBar;

    private ImageView imageViewGetBack;
    private ImageView imageViewGetBack2;

    private NewsItem detailItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().setEnterTransition(new Slide().setDuration(500));
        getWindow().setExitTransition(new Slide());
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        newsId = intent.getStringExtra(EXTRA_ID);
        mSharedPreference = new mySharedPreference();

        imageViewGetBack = findViewById(R.id.imageView_getBack);
        imageViewGetBack2 = findViewById(R.id.imageView_getBack2);
        imageViewGetBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        imageViewGetBack2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        scrollViewDetail = findViewById(R.id.ScrollView_detail);
        textViewProgressBar = findViewById(R.id.textView_progressBar_detail);
        progressBar = findViewById(R.id.progressBar_detail);
        toolbar = findViewById(R.id.toolbar_getDetail);
        textViewProgressBar.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        toolbar.setVisibility(View.VISIBLE);

        textViewToolbarTitle = findViewById(R.id.textView_detailToolbarTitle);
        imageViewToolbarTwitter = findViewById(R.id.imageView_detailTwitter);
        imageViewToolbarTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                browser();
            }
        });
        imageButton = findViewById(R.id.imageButton_detailBookmark);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tag = imageButton.getTag().toString();
                if (tag.equals("no")){
                    mSharedPreference.addFavorite(DetailActivity.this, detailItem);
                    Toast.makeText(DetailActivity.this, "\"" + detailItem.getSummary()
                            +"\" was added to Bookmarks", Toast.LENGTH_SHORT).show();
                    imageButton.setTag("yes");
                    Drawable bookmarked = ResourcesCompat.getDrawable(v.getResources(), R.drawable.ic_bookmark_black_clicked_24dp, null);
                    imageButton.setBackground(bookmarked);
                }else {
                    mSharedPreference.removeFavorite(DetailActivity.this, detailItem);
                    Toast.makeText(DetailActivity.this, "\"" + detailItem.getSummary()
                            +"\" was removed from Bookmarks", Toast.LENGTH_SHORT).show();
                    imageButton.setTag("no");
                    Drawable no_bookmarked = ResourcesCompat.getDrawable(v.getResources(), R.drawable.ic_bookmark_black_nonclicked_24dp, null);
                    imageButton.setBackground(no_bookmarked);
                }
            }
        });
        imageViewDetail = findViewById(R.id.imageView_detail);
        textViewDesc = findViewById(R.id.textView_detail_desc);
        textViewTitle = findViewById(R.id.textView_detail_title);
        textViewSection = findViewById(R.id.textView_detail_section);
        textViewDate = findViewById(R.id.textView_detail_date);
        textViewBottom = findViewById(R.id.textView_detail_bottomLink);

        requestQueue = VolleySingleton.getInstance(this).getmRequestQueue();

        parseDetail();

        Timer UiTimer = new Timer();
        UiTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textViewProgressBar.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        toolbar.setVisibility(View.GONE);
                        scrollViewDetail.setVisibility(View.VISIBLE);
                    }
                });
            }
        }, 2000); // End of your timer code.
    }


    private void parseDetail() {

        String url = "http://xdtestnode-env.eba-vypzvpc6.us-east-1.elasticbeanstalk.com/hw9article/p?source=guardian&id="+newsId;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject jsonObject = response.getJSONObject("result");
                            String imageUrl = jsonObject.getString("image");
                            String summary = jsonObject.getString("title");
                            String time = jsonObject.getString("date");
                            String section = jsonObject.getString("section");
                            String newsId = jsonObject.getString("searchId");
                            String webUrl = jsonObject.getString("webUrl");
                            detailItem = new NewsItem(imageUrl,summary,time,section,webUrl,newsId);
                            Picasso.with(DetailActivity.this).load(imageUrl).fit().into(imageViewDetail);
                            textViewDesc.setText(Html.fromHtml(jsonObject.getString("desc")));
//                            String text = textViewDesc.getText().toString();
//                            SpannableString spanText = new SpannableString(text);
//                            textViewDesc.setOnTouchListener(new TouchTextView(spanText));
//                            textViewDesc.setText(spanText);
//                            textViewDesc.setMovementMethod(LinkMovementMethod.getInstance());
                            textViewTitle.setText(summary);
                            textViewToolbarTitle.setText(summary);
                            textViewSection.setText(section);
                            textViewDate.setText(getFormattedTime(time));
                            //bookmark check
                            if (checkBookmarkItem(detailItem)){
                                imageButton.setTag("yes");
                                Drawable bookmarked = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_bookmark_black_clicked_24dp, null);
                                imageButton.setBackground(bookmarked);
                            } else {
                                imageButton.setTag("no");
                                Drawable no_bookmarked = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_bookmark_black_nonclicked_24dp, null);
                                imageButton.setBackground(no_bookmarked);
                            }
//                            Picasso.with(DetailActivity.this).load(jsonObject.getString("image")).fit().into(imageViewDetail);
//                            textViewDesc.setText(Html.fromHtml(jsonObject.getString("desc")));
//                            String text = textViewDesc.getText().toString();
//                            SpannableString spanText = new SpannableString(text);
//                            textViewDesc.setOnTouchListener(new TouchTextView(spanText));
//                            textViewDesc.setText(spanText);
////                            textViewDesc.setMovementMethod(LinkMovementMethod.getInstance());
//                            textViewTitle.setText(jsonObject.getString("title"));
//                            textViewToolbarTitle.setText(jsonObject.getString("title"));
//                            textViewSection.setText(jsonObject.getString("section"));
//                            textViewDate.setText(jsonObject.getString("date"));
                            //textViewBottom
                            link = jsonObject.getString("webUrl");
                            String linkedText =
                                    String.format("<a href=\"%s\">View Full Article</a> ", webUrl);
                            textViewBottom.setText(Html.fromHtml(linkedText));
                            textViewBottom.setMovementMethod(LinkMovementMethod.getInstance());
//                            textViewProgressBar.setVisibility(View.GONE);
//                            progressBar.setVisibility(View.GONE);
//                            toolbar.setVisibility(View.GONE);
//                            scrollViewDetail.setVisibility(View.VISIBLE);
                        } catch (JSONException | ParseException e) {
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

    public void browser(){

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/intent/tweet?text=Check out this Link: "+link+"&hashtags=CSCI571NEWS"));
        startActivity(intent);
    }

    static class TouchTextView implements View.OnTouchListener {
        Spannable spannable;

        public TouchTextView (Spannable spannable){
            this.spannable = spannable;
        }
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int action = event.getAction();
            if(!(v instanceof TextView)){
                return false;
            }
            TextView textView  = (TextView) v;
            if (action == MotionEvent.ACTION_UP ||
                    action == MotionEvent.ACTION_DOWN) {
                int x = (int) event.getX();
                int y = (int) event.getY();

                x -= textView.getTotalPaddingLeft();
                y -= textView.getTotalPaddingTop();

                x += textView.getScrollX();
                y += textView.getScrollY();

                Layout layout = textView.getLayout();
                int line = layout.getLineForVertical(y);
                int off = layout.getOffsetForHorizontal(line, x);

                ClickableSpan[] link = spannable.getSpans(off, off, ClickableSpan.class);

                if (link.length != 0) {
                    if (action == MotionEvent.ACTION_UP) {
                        link[0].onClick(textView);
                    } else if (action == MotionEvent.ACTION_DOWN) {
                        Selection.setSelection(spannable,
                                spannable.getSpanStart(link[0]),
                                spannable.getSpanEnd(link[0]));
                    }

                    return true;
                } else {
                    Selection.removeSelection(spannable);
                }
            }

            return false;
        }
    }

    public boolean checkBookmarkItem(NewsItem newsItem){
        boolean check = false;
        List<NewsItem> BookmarkedItemsInSharedPreference = mSharedPreference.getFavorites(DetailActivity.this);
        if (BookmarkedItemsInSharedPreference != null) {
            for (NewsItem news : BookmarkedItemsInSharedPreference) {
                if (news.equals(newsItem)) {
                    check = true;
                    break;
                }
            }
        }
        return check;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getFormattedTime (String oTime) throws ParseException {
        Instant instant = Instant.parse(oTime);
        ZoneId z = ZoneId.of( "America/Los_Angeles" );
        ZonedDateTime zdt = instant.atZone(z);
        Date newsTime = Date.from(zdt.toInstant());
        String str = new SimpleDateFormat("dd MMM yyyy").format(newsTime);
        return str;
    }
}
