package com.yellowzero.listen.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.yellowzero.listen.R;
import com.yellowzero.listen.player.DefaultPlayerManager;
import com.yellowzero.listen.player.PlayingInfoManager;


public class MusicPlayActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageView ivCover;
    private AppCompatSeekBar sbProgress;
    private ImageView ivPlay;
    private ImageView ivMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_play);
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

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                DefaultPlayerManager.getInstance().setSeek(seekBar.getProgress());
            }
        });
        DefaultPlayerManager.getInstance().getChangeMusicLiveData().observe(this, changeMusic -> {
            setTitle(changeMusic.getTitle());
            Glide.with(MusicPlayActivity.this).load(changeMusic.getImg()).transform(new CircleCrop()).into(ivCover);
        });
        DefaultPlayerManager.getInstance().getPlayingMusicLiveData().observe(this, playingMusic -> {
            sbProgress.setMax(playingMusic.getDuration());
            sbProgress.setProgress(playingMusic.getPlayerPosition());
        });
        DefaultPlayerManager.getInstance().getPauseLiveData().observe(this, isPlaying -> {
            if (isPlaying)
                ivPlay.setImageResource(R.drawable.ic_play);
            else
                ivPlay.setImageResource(R.drawable.ic_play_stop);
        });
        setModeImageView(DefaultPlayerManager.getInstance().getRepeatMode());

        DefaultPlayerManager.getInstance().getPlayModeLiveData().observe(this, this::setModeImageView);
    }

    private void setModeImageView(int mode) {
        if (mode == PlayingInfoManager.MODE_LIST_CYCLE) {
            ivMode.setImageResource(R.drawable.ic_mode_loop);
        } else if (mode == PlayingInfoManager.MODE_SINGLE_CYCLE) {
            ivMode.setImageResource(R.drawable.ic_mode_single);
        } else if (mode == PlayingInfoManager.MODE_RANDOM) {
            ivMode.setImageResource(R.drawable.ic_mode_random);
        }
    }

    public void onClickLast(View view) {
        DefaultPlayerManager.getInstance().playPrevious();
    }

    public void onClickPlay(View view) {
        DefaultPlayerManager.getInstance().togglePlay();
    }

    public void onClickNext(View view) {
        DefaultPlayerManager.getInstance().playNext();
    }

    public void onClickMode(View view) {
        DefaultPlayerManager.getInstance().changeMode();
    }
}