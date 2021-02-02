package com.xudong.mynewsapplication.mainFragment;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xudong.mynewsapplication.R;
import com.xudong.mynewsapplication.bookmark.BookmarkAdapter;
import com.xudong.mynewsapplication.bookmark.mySharedPreference;
import com.xudong.mynewsapplication.card.NewsItem;

import java.util.ArrayList;
import java.util.List;

import static android.widget.GridLayout.VERTICAL;

public class

BookmarkFragment extends Fragment {

    private BookmarkViewModel mViewModel;
    private RecyclerView recyclerView;
    private TextView noBookmark;
    private List<NewsItem> markedList = new ArrayList<>();
    private BookmarkAdapter bookmarkAdapter;
    private mySharedPreference mSharedPreference;
    private boolean hasCreated = false;

    private static final String TAG = "hello";

    public static BookmarkFragment newInstance() {
        return new BookmarkFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bookmark_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(BookmarkViewModel.class);
        // TODO: Use the ViewModel
        mSharedPreference = new mySharedPreference();
        noBookmark = getView().findViewById(R.id.textView_noBookmark);
        recyclerView = getView().findViewById(R.id.recyclerView_bookmark);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        getPreference();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (hasCreated){
//            Log.e(TAG,"在这刷新");
            bookmarkAdapter.notifyAdapterDataSetChanged();
            checkNoBookmark();
        }
    }

    public void getPreference(){
        markedList = mSharedPreference.getFavorites(getContext());
        bookmarkAdapter = new BookmarkAdapter(getContext(), markedList, BookmarkFragment.this);
        if (markedList != null){
            recyclerView.setAdapter(bookmarkAdapter);
        }
        hasCreated = true;
        checkNoBookmark();
    }

    public void checkNoBookmark(){
        markedList = mSharedPreference.getFavorites(getContext());
        if (markedList == null || markedList.size() == 0){
            noBookmark.setVisibility(View.VISIBLE);
        } else
            noBookmark.setVisibility(View.GONE);
    }
}
