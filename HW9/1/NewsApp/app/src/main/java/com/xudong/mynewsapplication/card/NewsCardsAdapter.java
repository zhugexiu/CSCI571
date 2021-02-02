package com.xudong.mynewsapplication.card;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.xudong.mynewsapplication.DetailActivity;
import com.xudong.mynewsapplication.MainActivity;
import com.xudong.mynewsapplication.R;
import com.xudong.mynewsapplication.bookmark.mySharedPreference;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class NewsCardsAdapter extends RecyclerView.Adapter {//<NewsCardsAdapter.NewsCardsViewHolder>
    private Context mcontext;
    private ArrayList mNewsCardsList;
    private mySharedPreference mSharedPreference;
    private OnItemClickListener mListener;
    private OnItemLongClickListener mLongListener;
    public static final String EXTRA_ID = "newsId";
    private static final String TAG = "hello";

    public interface OnItemClickListener {
        void onItemClick (int position);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener){
        mLongListener = listener;
    }

    public NewsCardsAdapter(Context context, ArrayList mNewsCardsList) {
        this.mcontext = context;
        this.mNewsCardsList = mNewsCardsList;
        mSharedPreference = new mySharedPreference();
    }

    @Override
    public int getItemViewType(int position) {
        if (mNewsCardsList.get(position) instanceof NewsItem)
            return 0;
        return 1;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mcontext);
//        View v = LayoutInflater.from(mcontext).inflate(R.layout.news_item, parent, false);
        View view;
        if (viewType == 0){
            view = layoutInflater.inflate(R.layout.news_item, parent,false);
            return new NewsCardsViewHolder(view);
        }

        view = layoutInflater.inflate(R.layout.weather_card, parent, false);
        return new WeatherCardsViewHolder(view);
        //return new NewsCardsViewHolder(v);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (mNewsCardsList.get(position) instanceof NewsItem) {
            //bind news card
            NewsCardsViewHolder newsCardsViewHolder = (NewsCardsViewHolder) holder;
            NewsItem currentItem = (NewsItem) mNewsCardsList.get(position);
            String imageUrl = currentItem.getImageUrl();
            String summary = currentItem.getSummary();
            String time = currentItem.getTime();
            String section = currentItem.getSection();

            newsCardsViewHolder.mTextViewSummary.setText(summary);
            newsCardsViewHolder.mTextViewTime.setText(getFormattedTime(time));
            newsCardsViewHolder.mTextViewSection.setText(section);
            Picasso.with(mcontext).load(imageUrl).fit().centerInside().into(newsCardsViewHolder.mImageView);
            //bookmark check
            if (checkBookmarkItem(currentItem)){
                //bookmarked
                newsCardsViewHolder.mImageButton.setTag("yes");
                Drawable bookmarked = ResourcesCompat.getDrawable(mcontext.getResources(), R.drawable.ic_bookmark_black_clicked_24dp, null);
                newsCardsViewHolder.mImageButton.setBackground(bookmarked);
            } else {
                newsCardsViewHolder.mImageButton.setTag("no");
                Drawable no_bookmarked = ResourcesCompat.getDrawable(mcontext.getResources(), R.drawable.ic_bookmark_black_nonclicked_24dp, null);
                newsCardsViewHolder.mImageButton.setBackground(no_bookmarked);
            }

        } else {
            //bind weather card
            WeatherCardsViewHolder weatherCardsViewHolder = (WeatherCardsViewHolder) holder;
            WeatherCard currentItem = (WeatherCard) mNewsCardsList.get(position);
            String imageUrl = currentItem.getmImageUrl();
            String city = currentItem.getmCity();
            String state = currentItem.getmState();
            String temperature = currentItem.getmTemperature();
            String type = currentItem.getmType();

            weatherCardsViewHolder.mTextViewCity.setText(city);
            weatherCardsViewHolder.mTextViewState.setText(state);
            weatherCardsViewHolder.mTextViewTemperature.setText(temperature);
            weatherCardsViewHolder.mTextViewType.setText(type);
            Picasso.with(mcontext).load(imageUrl).fit().centerInside().into(weatherCardsViewHolder.mImageView);
        }

    }

    @Override
    public int getItemCount() {
        return mNewsCardsList.size();
    }

    public class NewsCardsViewHolder extends RecyclerView.ViewHolder {
        ImageView mImageView;
        TextView mTextViewSummary;
        TextView mTextViewTime;
        TextView mTextViewSection;
        ImageButton mImageButton;

        public NewsCardsViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.imageView_newsCard);
            mTextViewSummary = itemView.findViewById(R.id.textView_summary);
            mTextViewTime = itemView.findViewById(R.id.textView_time);
            mTextViewSection = itemView.findViewById(R.id.textView_section);
            mImageButton = itemView.findViewById(R.id.imageButton_bookmark);


            mImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String tag = mImageButton.getTag().toString();
                    if (tag.equals("no")){
                        mSharedPreference.addFavorite(mcontext, (NewsItem) mNewsCardsList.get(getAdapterPosition()));
                        Toast.makeText(mcontext, "\"" + ((NewsItem) mNewsCardsList.get(getAdapterPosition())).getSummary()
                                +"\" was added to Bookmarks", Toast.LENGTH_SHORT).show();
                        mImageButton.setTag("yes");
                        Drawable bookmarked = ResourcesCompat.getDrawable(v.getResources(), R.drawable.ic_bookmark_black_clicked_24dp, null);
                        mImageButton.setBackground(bookmarked);
                    }else {
                        mSharedPreference.removeFavorite(mcontext, (NewsItem) mNewsCardsList.get(getAdapterPosition()));
                        Toast.makeText(mcontext, "\"" + ((NewsItem) mNewsCardsList.get(getAdapterPosition())).getSummary()
                                +"\" was removed from Bookmarks", Toast.LENGTH_SHORT).show();
                        mImageButton.setTag("no");
                        Drawable no_bookmarked = ResourcesCompat.getDrawable(v.getResources(), R.drawable.ic_bookmark_black_nonclicked_24dp, null);
                        mImageButton.setBackground(no_bookmarked);
                    }
                }
            });
            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Intent detailIntent = new Intent(mcontext, DetailActivity.class);
                    NewsItem clickedItem = (NewsItem) mNewsCardsList.get(getAdapterPosition());
                    detailIntent.putExtra(EXTRA_ID, clickedItem.getNewsId());
//                    startActivity(detailIntent, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
                    ActivityOptions options  = ActivityOptions.makeSceneTransitionAnimation((Activity) mcontext);
                    mcontext.startActivity(detailIntent, options.toBundle());
//                    if (mListener != null) {
//                        int position = getAdapterPosition();
//                        if (position != RecyclerView.NO_POSITION) {
//                            mListener.onItemClick(position);
//                        }
//                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    final NewsItem clickedItem = (NewsItem) mNewsCardsList.get(getAdapterPosition());
                    Dialog dialog = new Dialog(v.getContext());
                    dialog.setContentView(R.layout.dialog);
                    ImageView imageViewDialog = dialog.findViewById(R.id.imageView_dialogPic);
                    TextView textViewDialog = dialog.findViewById(R.id.textViewDialog);
                    ImageView imageViewTwitter = dialog.findViewById(R.id.imageView_dialogTwitter);
                    final ImageButton imageButton = dialog.findViewById(R.id.imageButton_dialogBookmark);
                    if (checkBookmarkItem(clickedItem)){
                        //bookmarked
                        imageButton.setTag("yes");
                        Drawable bookmarked = ResourcesCompat.getDrawable(mcontext.getResources(), R.drawable.ic_bookmark_black_clicked_24dp, null);
                        imageButton.setBackground(bookmarked);
                    } else {
                        imageButton.setTag("no");
                        Drawable no_bookmarked = ResourcesCompat.getDrawable(mcontext.getResources(), R.drawable.ic_bookmark_black_nonclicked_24dp, null);
                        imageButton.setBackground(no_bookmarked);
                    }
                    Picasso.with(v.getContext()).load(clickedItem.getImageUrl()).fit().centerInside().into(imageViewDialog);
                    textViewDialog.setText(clickedItem.getSummary());
                    imageViewTwitter.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/intent/tweet?text=Check out this Link: "
                                    + clickedItem.getmWebUrl() + "&hashtags=CSCI571NEWS"));
//                            startActivity(intent);
                            mcontext.startActivity(intent);
                        }
                    });
                    imageButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String tag = imageButton.getTag().toString();
                            if (tag.equals("no")){
                                mSharedPreference.addFavorite(mcontext, (NewsItem) mNewsCardsList.get(getAdapterPosition()));
                                Toast.makeText(mcontext, "\"" + ((NewsItem) mNewsCardsList.get(getAdapterPosition())).getSummary()
                                        +"\" was added to Bookmarks", Toast.LENGTH_SHORT).show();
                                imageButton.setTag("yes");
                                Drawable bookmarked = ResourcesCompat.getDrawable(v.getResources(), R.drawable.ic_bookmark_black_clicked_24dp, null);
                                imageButton.setBackground(bookmarked);
//                                mImageButton.setTag("yes");
//                                mImageButton.setBackground(bookmarked);
                                notifyDataSetChanged();
                            }else {
                                mSharedPreference.removeFavorite(mcontext, (NewsItem) mNewsCardsList.get(getAdapterPosition()));
                                Toast.makeText(mcontext, "\"" + ((NewsItem) mNewsCardsList.get(getAdapterPosition())).getSummary()
                                        +"\" was removed from Bookmarks", Toast.LENGTH_SHORT).show();
                                imageButton.setTag("no");
                                Drawable no_bookmarked = ResourcesCompat.getDrawable(v.getResources(), R.drawable.ic_bookmark_black_nonclicked_24dp, null);
                                imageButton.setBackground(no_bookmarked);
//                                mImageButton.setTag("no");
//                                mImageButton.setBackground(no_bookmarked);
                                notifyDataSetChanged();
                            }
                        }
                    });
                    dialog.show();
//                    if (mLongListener != null) {
//                        int position = getAdapterPosition();
//                        if (position != RecyclerView.NO_POSITION){
//                            mLongListener.onItemLongClick(position);
//                        }
//                    }
                    return true;
                }
            });
        }
    }

    public class WeatherCardsViewHolder extends RecyclerView.ViewHolder {
        ImageView mImageView;
        TextView mTextViewCity;
        TextView mTextViewState;
        TextView mTextViewTemperature;
        TextView mTextViewType;

        public WeatherCardsViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.imageView_weather);
            mTextViewCity = itemView.findViewById(R.id.textView_city);
            mTextViewState = itemView.findViewById(R.id.textView_state);
            mTextViewTemperature = itemView.findViewById(R.id.textView_temperature);
            mTextViewType = itemView.findViewById(R.id.textView_type);
        }
    }

    public void clear() {
        mNewsCardsList.clear();
        notifyDataSetChanged();
    }

    public boolean checkBookmarkItem(NewsItem newsItem){
        boolean check = false;
        List<NewsItem> BookmarkedItemsInSharedPreference = mSharedPreference.getFavorites(mcontext);
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
    public String getFormattedTime(String oTime){
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
}
