package com.xudong.mynewsapplication.bookmark;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Context;
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
import com.xudong.mynewsapplication.R;
import com.xudong.mynewsapplication.card.NewsItem;
import com.xudong.mynewsapplication.mainFragment.BookmarkFragment;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BookmarkAdapter extends RecyclerView.Adapter<BookmarkAdapter.BookmarkViewHolder> {
    private Context mContext;
    private ArrayList mNewsCardsList;
    private mySharedPreference mSharedPreference;
    private BookmarkFragment fragment;
    public static final String EXTRA_ID = "newsId";

    public BookmarkAdapter(Context context, List<NewsItem> markedList, BookmarkFragment fragment) {
        this.mContext = context;
        this.mNewsCardsList = (ArrayList) markedList;
        this.fragment = fragment;
        mSharedPreference = new mySharedPreference();
    }

    @NonNull
    @Override
    public BookmarkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_bookmark, parent, false);
        return new BookmarkViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull BookmarkViewHolder holder, int position) {
        NewsItem currentItem = (NewsItem) mNewsCardsList.get(position);
        BookmarkViewHolder bookmarkViewHolder = holder;
        String imageUrl = currentItem.getImageUrl();
        String summary = currentItem.getSummary();
        String time = currentItem.getTime();
        String section = currentItem.getSection();


        Picasso.with(mContext).load(imageUrl).fit().centerInside().into(bookmarkViewHolder.mImageView);
        bookmarkViewHolder.mTextViewTitle.setText(summary);
        try {
            bookmarkViewHolder.mTextViewDate.setText(getFormattedTime(time));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        bookmarkViewHolder.mTextViewSection.setText(section);
    }

    @Override
    public int getItemCount() {
        return mNewsCardsList.size();
    }

    public class BookmarkViewHolder extends RecyclerView.ViewHolder{

        ImageView mImageView;
        TextView mTextViewTitle;
        ImageButton mImageButton;
        TextView mTextViewDate;
        TextView mTextViewSection;

        public BookmarkViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.imageView_bookmarkCard);
            mTextViewTitle = itemView.findViewById(R.id.textView_bookmarkCard_title);
            mImageButton = itemView.findViewById(R.id.imageButton_bookmarkButton);
            mTextViewDate = itemView.findViewById(R.id.textView_bookmarkCard_date);
            mTextViewSection = itemView.findViewById(R.id.textView_bookmarkCard_section);

            mImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSharedPreference.removeFavorite(mContext, (NewsItem) mNewsCardsList.get(getAdapterPosition()));
                    Toast.makeText(mContext, "\"" + ((NewsItem) mNewsCardsList.get(getAdapterPosition())).getSummary()
                            +"\" was removed from Bookmarks", Toast.LENGTH_SHORT).show();
                    mNewsCardsList.remove(getAdapterPosition());
                    notifyDataSetChanged();
                    fragment.checkNoBookmark();
                }
            });

            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Intent detailIntent = new Intent(mContext, DetailActivity.class);
                    NewsItem clickedItem = (NewsItem) mNewsCardsList.get(getAdapterPosition());
                    detailIntent.putExtra(EXTRA_ID, clickedItem.getNewsId());
//                    startActivity(detailIntent, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
                    ActivityOptions options  = ActivityOptions.makeSceneTransitionAnimation((Activity) mContext);
                    mContext.startActivity(detailIntent, options.toBundle());
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    final NewsItem clickedItem = (NewsItem) mNewsCardsList.get(getAdapterPosition());
                    final Dialog dialog = new Dialog(v.getContext());
                    dialog.setContentView(R.layout.dialog);
                    ImageView imageViewDialog = dialog.findViewById(R.id.imageView_dialogPic);
                    TextView textViewDialog = dialog.findViewById(R.id.textViewDialog);
                    ImageView imageViewTwitter = dialog.findViewById(R.id.imageView_dialogTwitter);
                    final ImageButton imageButton = dialog.findViewById(R.id.imageButton_dialogBookmark);
                    imageButton.setTag("yes");
                    Drawable bookmarked = ResourcesCompat.getDrawable(mContext.getResources(), R.drawable.ic_bookmark_black_clicked_24dp, null);
                    imageButton.setBackground(bookmarked);
                    Picasso.with(v.getContext()).load(clickedItem.getImageUrl()).fit().centerInside().into(imageViewDialog);
                    textViewDialog.setText(clickedItem.getSummary());
                    imageViewTwitter.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/intent/tweet?text=Check out this Link: "
                                    + clickedItem.getmWebUrl() + "&hashtags=CSCI571NEWS"));
//                            startActivity(intent);
                            mContext.startActivity(intent);
                        }
                    });

                    imageButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mSharedPreference.removeFavorite(mContext, (NewsItem) mNewsCardsList.get(getAdapterPosition()));
                            Toast.makeText(mContext, "\"" + ((NewsItem) mNewsCardsList.get(getAdapterPosition())).getSummary()
                                    +"\" was removed from Bookmarks", Toast.LENGTH_SHORT).show();
                            mNewsCardsList.remove(getAdapterPosition());
                            notifyDataSetChanged();
                            fragment.checkNoBookmark();
                            dialog.cancel();
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getFormattedTime (String oTime) throws ParseException {
        Instant instant = Instant.parse(oTime);
        ZoneId z = ZoneId.of( "America/Los_Angeles" );
        ZonedDateTime zdt = instant.atZone(z);
        Date newsTime = Date.from(zdt.toInstant());
        String str = new SimpleDateFormat("dd MMM").format(newsTime);
        return str;
    }

    public boolean checkBookmarkItem(NewsItem newsItem){
        boolean check = false;
        List<NewsItem> BookmarkedItemsInSharedPreference = mSharedPreference.getFavorites(mContext);
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

    public void notifyAdapterDataSetChanged() {
        List<NewsItem> newList = mSharedPreference.getFavorites(mContext);
        if (newList != null && newList.size() != mNewsCardsList.size()){
            mNewsCardsList = (ArrayList) newList;
            notifyDataSetChanged();
        }
    }
}
