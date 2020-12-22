package com.yellowzero.Dang;

import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.fragment.app.Fragment;

import android.net.Uri;
import android.os.Bundle;

import com.yellowzero.Dang.fragment.BilibiliFragment;
import com.yellowzero.Dang.fragment.ImageFragment;
import com.yellowzero.Dang.fragment.MusicFragment;
import com.yellowzero.Dang.view.AndTabManager;
import com.yellowzero.Dang.view.FragmentTabCheckListener;
import com.yellowzero.Dang.view.Tab;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar(findViewById(R.id.toolbar));
        AndTabManager andTabManager = new AndTabManager(this, findViewById(R.id.llTab));
        andTabManager.addTab(new Tab().setText(getResources().getText(R.string.tv_music).toString())
                .setNormalColor(getResources().getColor(R.color.tabGrey))
                .setSelectColor(getResources().getColor(R.color.colorPrimary))
                .setIconNormalResId(R.drawable.tab_music_inactive)
                .setIconPressedResId(R.drawable.tab_music_active));
        andTabManager.addTab(new Tab().setText(getResources().getText(R.string.tv_bilibili).toString())
                .setNormalColor(getResources().getColor(R.color.tabGrey))
                .setSelectColor(getResources().getColor(R.color.colorPrimary))
                .setIconNormalResId(R.drawable.tab_bilibili_inactive)
                .setIconPressedResId(R.drawable.tab_bilibili_active));
        andTabManager.addTab(new Tab().setText(getResources().getText(R.string.tv_image).toString())
                .setNormalColor(getResources().getColor(R.color.tabGrey))
                .setSelectColor(getResources().getColor(R.color.colorPrimary))
                .setIconNormalResId(R.drawable.tab_image_inactive)
                .setIconPressedResId(R.drawable.tab_image_active));
        andTabManager.setOnTabCheckListener(new FragmentTabCheckListener(
                getSupportFragmentManager(),
                R.id.llFragment,
                new Fragment[] {
                        new MusicFragment(),
                        new BilibiliFragment(),
                        new ImageFragment()
                }));
        andTabManager.setCurrentItem(0);
        /*new CustomTabsIntent.Builder()
                .build()
                .launchUrl(this, Uri.parse("https://www.bilibili.com/video/BV1Xi4y1V7rp"));*/
    }
}