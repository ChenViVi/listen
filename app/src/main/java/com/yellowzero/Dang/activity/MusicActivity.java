package com.yellowzero.Dang.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.kunminx.player.PlayingInfoManager;
import com.yellowzero.Dang.R;
import com.yellowzero.Dang.util.PlayerManager;

public class MusicActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageView ivCover;
    private AppCompatSeekBar sbProgress;
    private ImageView ivPlay;
    private ImageView ivMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
        toolbar = findViewById(R.id.toolbar);
        ivCover = findViewById(R.id.ivCover);
        sbProgress = findViewById(R.id.sbProgress);
        ivPlay = findViewById(R.id.ivPlay);
        ivMode = findViewById(R.id.ivMode);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sbProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                //PlayerManager.getInstance().setSeek(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                PlayerManager.getInstance().setSeek(seekBar.getProgress());
            }
        });
        PlayerManager.getInstance().getChangeMusicLiveData().observe(this, changeMusic -> {
            setTitle(changeMusic.getTitle());
            Glide.with(MusicActivity.this).load(changeMusic.getImg()).transform(new CircleCrop()).into(ivCover);
        });
        PlayerManager.getInstance().getPlayingMusicLiveData().observe(this, playingMusic -> {
            sbProgress.setMax(playingMusic.getDuration());
            sbProgress.setProgress(playingMusic.getPlayerPosition());
        });
        PlayerManager.getInstance().getPauseLiveData().observe(this, isPlaying -> {
            if (isPlaying)
                ivPlay.setImageResource(R.drawable.ic_play);
            else
                ivPlay.setImageResource(R.drawable.ic_play_stop);
        });
        setModeImageView(PlayerManager.getInstance().getRepeatMode());
        PlayerManager.getInstance().getPlayModeLiveData().observe(this, this::setModeImageView);

    }

    private void setModeImageView(Enum mode) {
        if (mode == PlayingInfoManager.RepeatMode.LIST_LOOP) {
            ivMode.setImageResource(R.drawable.ic_mode_loop);
        } else if (mode == PlayingInfoManager.RepeatMode.ONE_LOOP) {
            ivMode.setImageResource(R.drawable.ic_mode_single);
        } else if (mode == PlayingInfoManager.RepeatMode.RANDOM) {
            ivMode.setImageResource(R.drawable.ic_mode_random);
        }
    }

    public void onClickLast(View view) {
        PlayerManager.getInstance().playPrevious();
    }

    public void onClickPlay(View view) {
        PlayerManager.getInstance().togglePlay();
    }

    public void onClickNext(View view) {
        PlayerManager.getInstance().playNext();
    }

    public void onClickMode(View view) {
        PlayerManager.getInstance().changeMode();
    }
}