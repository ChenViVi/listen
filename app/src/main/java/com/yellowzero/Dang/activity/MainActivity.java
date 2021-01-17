package com.yellowzero.Dang.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.allen.library.RxHttpUtils;
import com.allen.library.download.DownloadObserver;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.yellowzero.Dang.R;
import com.yellowzero.Dang.fragment.BilibiliFragment;
import com.yellowzero.Dang.fragment.ImageFragment;
import com.yellowzero.Dang.fragment.MusicFragment;
import com.yellowzero.Dang.util.PlayerManager;
import com.yellowzero.Dang.view.AndTabManager;
import com.yellowzero.Dang.view.FragmentTabCheckListener;
import com.yellowzero.Dang.view.Tab;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ImageView ivCover;
    private TextView tvName;
    private ImageView ivPlay;
    private View llMusic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar(findViewById(R.id.toolbar));
        ivCover = findViewById(R.id.ivCover);
        tvName = findViewById(R.id.tvName);
        ivPlay = findViewById(R.id.ivPlay);
        llMusic = findViewById(R.id.llMusic);
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
        PlayerManager.getInstance().getPauseLiveData().observe(this, isPaused -> {
            if (isPaused) {
                ivPlay.setImageResource(R.drawable.ic_play);
                llMusic.setVisibility(View.GONE);
            }
            else {
                ivPlay.setImageResource(R.drawable.ic_play_stop);
                llMusic.setVisibility(View.VISIBLE);
            }
        });
/*        XXPermissions.with(this)
                .permission(Permission.MANAGE_EXTERNAL_STORAGE)
                .request(new OnPermissionCallback() {

                    @Override
                    public void onGranted(List<String> permissions, boolean all) {

                    }

                    @Override
                    public void onDenied(List<String> permissions, boolean never) {

                    }
                });*/
        /*new CustomTabsIntent.Builder()
                .build()
                .launchUrl(this, Uri.parse("https://www.bilibili.com/video/BV1Xi4y1V7rp"));*/
        /*XXPermissions.with(this)
                .permission(Permission.MANAGE_EXTERNAL_STORAGE)
                .request(new OnPermissionCallback() {

                    @Override
                    public void onGranted(List<String> permissions, boolean all) {

                    }

                    @Override
                    public void onDenied(List<String> permissions, boolean never) {

                    }
                });*/
    }

    public void onPlay(View view) {
        PlayerManager.getInstance().togglePlay();
    }

    public void onNext(View view) {
        PlayerManager.getInstance().playNext();
    }

/*    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_setting);
        item.setIcon(R.drawable.ic_pause);
        return super.onPrepareOptionsMenu(menu);
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if (item.getItemId() == R.id.action_setting) {
            startActivity(new Intent(MainActivity.this, SettingActivity.class));
            return true;
        }
        else
            return super.onOptionsItemSelected(item);
    }
}