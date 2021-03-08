package com.yellowzero.listen.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.yellowzero.listen.AppData;
import com.yellowzero.listen.R;
import com.yellowzero.listen.adapter.TabViewPagerAdapter;
import com.yellowzero.listen.fragment.TestFragment;
import com.yellowzero.listen.model.MusicTag;

public class TestActivity extends AppCompatActivity {

    private static final String KEY_TAG = "tag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        MusicTag tag = (MusicTag) getIntent().getSerializableExtra(KEY_TAG);
        if (tag == null)
            return;
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(tag.getName());
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager viewPager = findViewById(R.id.viewPager);
        TabViewPagerAdapter adapterTab = new TabViewPagerAdapter(getSupportFragmentManager());
        adapterTab.addFragment(TestFragment.createInstance(true), getString(R.string.yellow_zero));
        adapterTab.addFragment(TestFragment.createInstance(false), getString(R.string.tv_other));
        viewPager.setAdapter(adapterTab);
        tabLayout.setupWithViewPager(viewPager);
    }

    public static void start(Context context, MusicTag tag) {
        Intent intent = new Intent(context, TestActivity.class);
        intent.putExtra(KEY_TAG, tag);
        context.startActivity(intent);
    }
}