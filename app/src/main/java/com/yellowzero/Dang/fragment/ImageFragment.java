package com.yellowzero.Dang.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.yellowzero.Dang.R;
import com.yellowzero.Dang.TestFragment;
import com.yellowzero.Dang.adapter.TabViewPagerAdapter;

public class ImageFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_image,null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TabLayout tabLayout = view.findViewById(R.id.tabLayout);
        ViewPager viewPager = view.findViewById(R.id.viewPager);
        TabViewPagerAdapter adapterTab = new TabViewPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(adapterTab);
        tabLayout.setupWithViewPager(viewPager);
        adapterTab.addFragment(new TestFragment(), "3131");
        adapterTab.addFragment(new TestFragment(), "r23sdfsd");
        adapterTab.addFragment(new TestFragment(), "1sfwef");
        adapterTab.addFragment(new TestFragment(), "sdfasd");
        adapterTab.addFragment(new TestFragment(), "adaf");
        adapterTab.notifyDataSetChanged();
    }
}
