package com.yellowzero.listen.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.yellowzero.listen.R;
import com.yellowzero.listen.player.DefaultPlayerManager;
import com.yellowzero.listen.player.PlayingInfoManager;
import com.yellowzero.listen.player.contract.IPlayController;


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
        DefaultPlayerManager.getInstance().getStateLiveData().observe(this, state -> {
            switch (state) {
                case IPlayController.STATE_STOP:
                case IPlayController.STATE_PAUSE:
                    ivPlay.setImageResource(R.drawable.ic_play);
                    break;
                case IPlayController.STATE_PLAY:
                    ivPlay.setImageResource(R.drawable.ic_play_pause);
                    break;
            }
        });
        setModeImageView(DefaultPlayerManager.getInstance().getRepeatMode());
        DefaultPlayerManager.getInstance().getPlayModeLiveData().observe(this, this::setModeImageView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_music_play, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (item.getItemId() == R.id.action_close) {
            DefaultPlayerManager.getInstance().clear();
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setModeImageView(int mode) {
        if (mode == IPlayController.MODE_LIST_CYCLE) {
            ivMode.setImageResource(R.drawable.ic_mode_loop);
        } else if (mode == IPlayController.MODE_SINGLE_CYCLE) {
            ivMode.setImageResource(R.drawable.ic_mode_single);
        } else if (mode == IPlayController.MODE_RANDOM) {
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