package com.yellowzero.listen.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.allen.library.RxHttpUtils;
import com.allen.library.bean.BaseData;
import com.allen.library.interceptor.Transformer;
import com.yellowzero.listen.AppData;
import com.yellowzero.listen.R;
import com.yellowzero.listen.model.AppInfo;
import com.yellowzero.listen.observer.DataObserver;
import com.yellowzero.listen.service.AppService;
import com.yellowzero.listen.view.DownloadDialog;

import java.util.List;

public class SettingActivity extends AppCompatActivity {
    private int appCode = -1;
    private CheckBox checkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        checkBox = findViewById(R.id.cbEnableMusicMobile);
        TextView tvVersion = findViewById(R.id.tvVersion);
        TextView tvNewVersion = findViewById(R.id.tvNewVersion);
        View llVersion = findViewById(R.id.llVersion);
        checkBox.setChecked(AppData.ENABLE_MUSIC_MOBILE);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                enableMusicMobile(b);
            }
        });
        try {
            PackageManager manager = getPackageManager();
            PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
            appCode = info.versionCode;
            String name = info.versionName;
            tvVersion.setText(name);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            tvVersion.setText(R.string.tv_version_error);
        }
        if (appCode != -1)
            RxHttpUtils.createApi(AppService.class)
                    .list()
                    .compose(Transformer.<BaseData<List<AppInfo>>>switchSchedulers())
                    .subscribe(new DataObserver<List<AppInfo>>() {

                        @Override
                        protected void onSuccess(List<AppInfo> data) {
                            if (data != null && data.size() > 0) {
                                AppInfo appInfo = data.get(0);
                                if (appInfo.getCode() > appCode) {
                                    tvNewVersion.setSelected(true);
                                    tvNewVersion.setText(R.string.tv_version_has_new);
                                    llVersion.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            new DownloadDialog(SettingActivity.this, appInfo).show();
                                        }
                                    });
                                } else {
                                    tvNewVersion.setSelected(false);
                                    tvNewVersion.setText(R.string.tv_version_is_new);
                                }
                            }
                        }
                    });
    }

    @Override
    protected void onDestroy() {
        RxHttpUtils.cancel("download");
        super.onDestroy();
    }

    public void onClickAbout(View view) {
        WebViewActivity.start(this, getString(R.string.tt_about), "http://chenvivi.gitee.io/yellowzero/index.html", false);
    }

    public void onClickMusicMobile(View view) {
        enableMusicMobile(checkBox.isChecked());
    }

    private void enableMusicMobile(boolean enable) {
        AppData.ENABLE_MUSIC_MOBILE = enable;
        AppData.saveData(SettingActivity.this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}