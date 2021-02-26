package com.yellowzero.listen.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.allen.library.RxHttpUtils;
import com.allen.library.bean.BaseData;
import com.allen.library.interceptor.Transformer;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.yellowzero.listen.AppData;
import com.yellowzero.listen.R;
import com.yellowzero.listen.fragment.BilibiliFragment;
import com.yellowzero.listen.fragment.ImageFragment;
import com.yellowzero.listen.fragment.MusicTagFragment;
import com.yellowzero.listen.model.AppInfo;
import com.yellowzero.listen.observer.DataObserver;
import com.yellowzero.listen.player.DefaultPlayerManager;
import com.yellowzero.listen.player.contract.IPlayController;
import com.yellowzero.listen.service.AppService;
import com.yellowzero.listen.view.AndTabManager;
import com.yellowzero.listen.view.UpdateDialog;
import com.yellowzero.listen.view.FragmentTabCheckListener;
import com.yellowzero.listen.view.Tab;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar(findViewById(R.id.toolbar));
        ImageView ivCover = findViewById(R.id.ivCover);
        TextView tvName = findViewById(R.id.tvName);
        ImageView ivPlay = findViewById(R.id.ivPlay);
        View llMusic = findViewById(R.id.llMusic);
        AndTabManager andTabManager = new AndTabManager(this, findViewById(R.id.llTab));
        andTabManager.addTab(new Tab().setText(getResources().getText(R.string.tv_image).toString())
                .setNormalColor(getResources().getColor(R.color.tabGrey))
                .setSelectColor(getResources().getColor(R.color.colorPrimary))
                .setIconNormalResId(R.drawable.tab_image_inactive)
                .setIconPressedResId(R.drawable.tab_image_active));
        andTabManager.addTab(new Tab().setText(getResources().getText(R.string.tv_music).toString())
                .setNormalColor(getResources().getColor(R.color.tabGrey))
                .setSelectColor(getResources().getColor(R.color.colorPrimary))
                .setIconNormalResId(R.drawable.tab_music_inactive)
                .setIconPressedResId(R.drawable.tab_music_active));
        andTabManager.addTab(new Tab().setText(getResources().getText(R.string.tv_video).toString())
                .setNormalColor(getResources().getColor(R.color.tabGrey))
                .setSelectColor(getResources().getColor(R.color.colorPrimary))
                .setIconNormalResId(R.drawable.tab_video_inactive)
                .setIconPressedResId(R.drawable.tab_video_active));
        andTabManager.setOnTabCheckListener(new FragmentTabCheckListener(
                getSupportFragmentManager(),
                R.id.llFragment,
                new Fragment[] {
                        new ImageFragment(),
                        new MusicTagFragment(),
                        new BilibiliFragment()
                }));
        andTabManager.setCurrentItem(0);
        DefaultPlayerManager.getInstance().getChangeMusicLiveData().observe(this, changeMusic -> {
            Glide.with(MainActivity.this)
                    .load(changeMusic.getImg())
                    .placeholder(R.drawable.ic_holder_circle)
                    .error(R.drawable.ic_holder_circle)
                    .transform(new CircleCrop())
                    .into(ivCover);
            tvName.setText(changeMusic.getTitle());
        });
        DefaultPlayerManager.getInstance().getStateLiveData().observe(this, state -> {
            switch (state) {
                case IPlayController.STATE_STOP:
                    llMusic.setVisibility(View.GONE);
                    break;
                case IPlayController.STATE_PAUSE:
                    ivPlay.setImageResource(R.drawable.ic_play);
                    llMusic.setVisibility(View.VISIBLE);
                    break;
                case IPlayController.STATE_PLAY:
                    ivPlay.setImageResource(R.drawable.ic_play_pause);
                    llMusic.setVisibility(View.VISIBLE);
                    break;
            }
        });
        RxHttpUtils.createApi(AppService.class)
                .list()
                .compose(Transformer.<BaseData<List<AppInfo>>>switchSchedulers())
                .subscribe(new DataObserver<List<AppInfo>>() {

                    @Override
                    protected void onSuccess(List<AppInfo> data) {
                        if (data != null && data.size() > 0) {
                            AppInfo appInfo = data.get(0);
                            try {
                                PackageManager manager = getPackageManager();
                                PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
                                if (appInfo.getCode() > info.versionCode && appInfo.getCode() > AppData.LAST_UPDATE_CODE) {
                                    new UpdateDialog(MainActivity.this, appInfo){
                                        @Override
                                        public void onCancel() {
                                            super.onCancel();
                                            AppData.LAST_UPDATE_CODE = appInfo.getCode();
                                            AppData.saveData(MainActivity.this);
                                        }
                                    }.show();
                                }
                            }   catch (PackageManager.NameNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    protected void onError(String errorMsg) {

                    }
                });
    }

    public void onPlay(View view) {
        DefaultPlayerManager.getInstance().togglePlay();
    }

    public void onNext(View view) {
        DefaultPlayerManager.getInstance().playNext();
    }

    public void onClickPlayDetail(View view) {
        startActivity(new Intent(MainActivity.this, MusicPlayActivity.class));
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
        if (item.getItemId() == R.id.action_setting) {
            startActivity(new Intent(MainActivity.this, SettingActivity.class));
            return true;
        }
        else
            return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppData.saveData(this);
    }
}