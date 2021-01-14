package com.yellowzero.Dang.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.yellowzero.Dang.R;
import com.yellowzero.Dang.fragment.BilibiliFragment;
import com.yellowzero.Dang.fragment.ImageFragment;
import com.yellowzero.Dang.fragment.MusicFragment;
import com.yellowzero.Dang.util.PlayerManager;
import com.yellowzero.Dang.view.AndTabManager;
import com.yellowzero.Dang.view.FragmentTabCheckListener;
import com.yellowzero.Dang.view.Tab;

public class MainActivity extends AppCompatActivity {

    private ImageView ivCover;
    private TextView tvName;
    private ImageView ivPlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar(findViewById(R.id.toolbar));
        ivCover = findViewById(R.id.ivCover);
        tvName = findViewById(R.id.tvName);
        ivPlay = findViewById(R.id.ivPlay);
        findViewById(R.id.llMusic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, MusicActivity.class));
            }
        });
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
        PlayerManager.getInstance().getChangeMusicLiveData().observe(this, changeMusic -> {
            Glide.with(MainActivity.this).load(changeMusic.getImg()).transform(new CircleCrop()).into(ivCover);
            tvName.setText(changeMusic.getTitle());
        });
        PlayerManager.getInstance().getPauseLiveData().observe(this, isPlaying -> {
            if (isPlaying)
                ivPlay.setImageResource(R.drawable.ic_play);
            else
                ivPlay.setImageResource(R.drawable.ic_play_stop);
        });
        /*new CustomTabsIntent.Builder()
                .build()
                .launchUrl(this, Uri.parse("https://www.bilibili.com/video/BV1Xi4y1V7rp"));*/
    }

    public void onPlay(View view) {
        PlayerManager.getInstance().togglePlay();
    }

    public void onNext(View view) {
        PlayerManager.getInstance().playNext();
    }
}