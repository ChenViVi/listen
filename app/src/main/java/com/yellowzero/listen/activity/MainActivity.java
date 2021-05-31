package com.yellowzero.listen.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Gravity;
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
import com.yellowzero.listen.fragment.ScheduleFragment;
import com.yellowzero.listen.model.AppInfo;
import com.yellowzero.listen.observer.DataObserver;
import com.yellowzero.listen.player.DefaultPlayerManager;
import com.yellowzero.listen.player.contract.IPlayController;
import com.yellowzero.listen.service.AppService;
import com.yellowzero.listen.view.UpdateDialog;
import com.yellowzero.listen.view.Tab;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private List<Tab> tabs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar(findViewById(R.id.toolbar));
        ImageView ivCover = findViewById(R.id.ivCover);
        TextView tvName = findViewById(R.id.tvName);
        ImageView ivPlay = findViewById(R.id.ivPlay);
        View llMusic = findViewById(R.id.llMusic);
        View llTabImage = findViewById(R.id.llTabImage);
        ImageView ivTabImage = findViewById(R.id.ivTabImage);
        ImageView ivTabMusic = findViewById(R.id.ivTabMusic);
        tabs.add(new Tab().setText(getResources().getText(R.string.tv_image).toString())
                .setNormalColor(getResources().getColor(R.color.tabGrey))
                .setSelectColor(getResources().getColor(R.color.colorPrimary))
                .setIconNormalResId(R.drawable.tab_image_inactive)
                .setIconPressedResId(R.drawable.tab_image_active)
                .setView(llTabImage)
                .setTextView(findViewById(R.id.tvTabImage))
                .setImageView(ivTabImage)
                .setFragment(new ImageFragment()));
        tabs.add(new Tab().setText(getResources().getText(R.string.tv_music).toString())
                .setNormalColor(getResources().getColor(R.color.tabGrey))
                .setSelectColor(getResources().getColor(R.color.colorPrimary))
                .setIconNormalResId(R.drawable.tab_music_inactive)
                .setIconPressedResId(R.drawable.tab_music_active)
                .setView(findViewById(R.id.llTabMusic))
                .setTextView(findViewById(R.id.tvTabMusic))
                .setImageView(ivTabMusic)
                .setFragment(new MusicTagFragment()));
        tabs.add(new Tab().setText(getResources().getText(R.string.tv_video).toString())
                .setNormalColor(getResources().getColor(R.color.tabGrey))
                .setSelectColor(getResources().getColor(R.color.colorPrimary))
                .setIconNormalResId(R.drawable.tab_video_inactive)
                .setIconPressedResId(R.drawable.tab_video_active)
                .setView(findViewById(R.id.llTabVideo))
                .setTextView(findViewById(R.id.tvTabVideo))
                .setImageView(findViewById(R.id.ivTabVideo))
                .setFragment(new BilibiliFragment()));
        tabs.add(new Tab().setText(getResources().getText(R.string.tv_schedule).toString())
                .setNormalColor(getResources().getColor(R.color.tabGrey))
                .setSelectColor(getResources().getColor(R.color.colorPrimary))
                .setIconNormalResId(R.drawable.tab_schedule_inactive)
                .setIconPressedResId(R.drawable.tab_schedule_active)
                .setView(findViewById(R.id.llTabSchedule))
                .setTextView(findViewById(R.id.tvTabSchedule))
                .setImageView(findViewById(R.id.ivTabSchedule))
                .setFragment(new ScheduleFragment()));
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        for (int i = 0; i < tabs.size(); i++) {
            Tab tab = tabs.get(i);
            View llTab = tab.getView();
            llTab.setTag(i);
            llTab.setOnClickListener(this);
            transaction.add(R.id.llFragment, tab.getFragment());
        }
        transaction.show(tabs.get(0).getFragment());
        for (int i = 1; i < tabs.size(); i++)
            transaction.hide(tabs.get(i).getFragment());
        transaction.commit();
        DefaultPlayerManager.getInstance().getChangeMusicLiveData().observe(this, changeMusic -> {
            Glide.with(MainActivity.this)
                    .load(changeMusic.getImg())
                    .placeholder(R.drawable.ic_holder_circle)
                    .error(R.drawable.ic_holder_circle)
                    .transform(new CircleCrop())
                    .into(ivCover);
            tvName.setText(changeMusic.getName());
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
                .subscribe(new DataObserver<List<AppInfo>>(this) {

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

    @Override
    public void onClick(View v) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        for (Tab tab : tabs) {
            if (v == tab.getView()) {
                tab.getImageView().setImageResource(tab.getIconPressedResId());
                tab.getTextView().setTextColor(tab.getSelectColor());
                transaction.show(tab.getFragment());
            } else {
                tab.getImageView().setImageResource(tab.getIconNormalResId());
                tab.getTextView().setTextColor(tab.getNormalColor());
                transaction.hide(tab.getFragment());
            }
        }
        transaction.commit();
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
}