package com.yellowzero.Dang.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.yellowzero.Dang.AppData;
import com.yellowzero.Dang.R;

public class SettingActivity extends AppCompatActivity {
    private CheckBox checkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        checkBox = findViewById(R.id.cbEnableMusicMobile);
        checkBox.setChecked(AppData.ENABLE_MUSIC_MOBILE);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                enableMusicMobile(b);
            }
        });
    }

    public void onClickMusicMobile(View view) {
        enableMusicMobile(checkBox.isChecked());
    }

    private void enableMusicMobile(boolean enable) {
        AppData.ENABLE_MUSIC_MOBILE = enable;
        AppData.saveData(SettingActivity.this);
    }
}