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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.jaeger.library.StatusBarUtil;
import com.yellowzero.listen.App;
import com.yellowzero.listen.R;
import com.yellowzero.listen.model.MusicTag;
import com.yellowzero.listen.model.entity.MusicEntity;
import com.yellowzero.listen.model.entity.MusicEntityDao;
import com.yellowzero.listen.player.DefaultPlayerManager;
import com.yellowzero.listen.player.bean.DefaultAlbum;
import com.yellowzero.listen.player.contract.IPlayController;

import java.util.List;

public class MusicPlayActivity extends AppCompatActivity implements OnPermissionCallback {

    private Toolbar toolbar;
    private ImageView ivCover;
    private AppCompatSeekBar sbProgress;
    private ImageView ivPlay;
    private ImageView ivMode;
    private ImageView ivFav;

    private MusicEntityDao musicEntityDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_play);
        musicEntityDao = ((App) getApplication()).getDaoSession().getMusicEntityDao();
        toolbar = findViewById(R.id.toolbar);
        ivCover = findViewById(R.id.ivCover);
        sbProgress = findViewById(R.id.sbProgress);
        ivPlay = findViewById(R.id.ivPlay);
        ivMode = findViewById(R.id.ivMode);
        ivFav = findViewById(R.id.ivFav);
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
            setTitle(changeMusic.getName());
            Glide.with(MusicPlayActivity.this)
                    .load(changeMusic.getImg())
                    .placeholder(R.drawable.ic_holder_circle)
                    .error(R.drawable.ic_holder_circle)
                    .transform(new CircleCrop())
                    .into(ivCover);
            List<MusicEntity> musicEntities = musicEntityDao.queryBuilder().where(MusicEntityDao.Properties.Url.eq(changeMusic.getUrl())).list();
            ivFav.setImageResource(musicEntities.size() > 0 ? R.drawable.ic_fav_enable : R.drawable.ic_fav);
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
        StatusBarUtil.setColor(this, getResources().getColor(R.color.bgMusicPlayStart));
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

    public void onClickPlayList(View view) {
        DefaultAlbum album = DefaultPlayerManager.getInstance().getAlbum();
        if (album == null)
            return;
        int albumId = Integer.parseInt(album.getAlbumId());
        if (albumId == MusicTag.ID_LOCAL_ARTIST || albumId == MusicTag.ID_LOCAL_OTHER)
            albumId = MusicTag.ID_LOCAL;
        if (albumId == MusicTag.ID_LOCAL)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R)
                XXPermissions.with(this)
                        .permission(Permission.MANAGE_EXTERNAL_STORAGE)
                        .request(this);
            else
                XXPermissions.with(this)
                        .permission(Permission.READ_EXTERNAL_STORAGE)
                        .request(this);
        else if (albumId == MusicTag.ID_FAV)
            MusicListFavActivity.start(this, album.getTag());
        else
            MusicListActivity.start(this, album.getTag());
    }

    public void onClickFav(View view) {
        DefaultAlbum.DefaultMusic music = DefaultPlayerManager.getInstance().getCurrentPlayingMusic();
        List<MusicEntity> musicEntities = musicEntityDao.queryBuilder().where(MusicEntityDao.Properties.Url.eq(music.getUrl())).list();
        boolean isFav = musicEntities.size() > 0;
        if (isFav)
            musicEntityDao.delete(musicEntities.get(0));
        else
            musicEntityDao.insert(music.toEntity());
        ivFav.setImageResource(!isFav ? R.drawable.ic_fav_enable : R.drawable.ic_fav);
    }

    @Override
    public void onGranted(List<String> permissions, boolean all) {
        MusicListLocalActivity.start(this, DefaultPlayerManager.getInstance().getAlbum().getTag());
    }

    @Override
    public void onDenied(List<String> permissions, boolean never) {
        Toast.makeText(this, R.string.ts_permission_storage, Toast.LENGTH_SHORT).show();
    }
}