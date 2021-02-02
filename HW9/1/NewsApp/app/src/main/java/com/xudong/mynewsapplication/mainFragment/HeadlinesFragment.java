package com.xudong.mynewsapplication.mainFragment;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.xudong.mynewsapplication.R;
import com.xudong.mynewsapplication.headlinePager.SectionsPagerAdapter;


public class HeadlinesFragment extends Fragment {
    private HeadlinesViewModel mViewModel;
    private static final String TAG = "hello";
    private ViewPager viewPager;
    private TabLayout tabs;

    public static HeadlinesFragment newInstance() {
        return new HeadlinesFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.headlines_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(HeadlinesViewModel.class);
        // TODO: Use the ViewModel
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getContext(), getActivity().getSupportFragmentManager());
        tabs = getView().findViewById(R.id.tabs);
        viewPager = getView().findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        tabs.setupWithViewPager(viewPager);


//        Log.e(TAG,"Headlines oac: ");
    }

    @Override
    public void onStart() {
        super.onStart();
//        Log.e(TAG,"Headlines start: ");
    }

    @Override
    public void onResume() {
        super.onResume();
//        Log.e(TAG,"Headlines resume: ");
    }

    @Override
    public void onPause() {
        super.onPause();
//        Log.e(TAG,"Headlines pause: ");
    }

    @Override
    public void onStop() {
        super.onStop();
//        Log.e(TAG,"Headlines stop: ");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        Log.e(TAG,"Headlines destroy: ");
    }
}
