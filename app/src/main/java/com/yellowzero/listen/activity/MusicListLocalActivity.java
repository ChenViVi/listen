package com.yellowzero.listen.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.yellowzero.listen.App;
import com.yellowzero.listen.AppData;
import com.yellowzero.listen.R;
import com.yellowzero.listen.adapter.MusicAdapter;
import com.yellowzero.listen.model.MusicTag;
import com.yellowzero.listen.model.Music;
import com.yellowzero.listen.model.entity.MusicEntityDao;
import com.yellowzero.listen.player.DefaultPlayerManager;
import com.yellowzero.listen.player.bean.DefaultAlbum;
import com.yellowzero.listen.player.contract.IPlayController;
import com.yellowzero.listen.util.FileUtil;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class MusicListLocalActivity extends AppCompatActivity {

    private static final String KEY_TAG = "tag";
    private static final String YELLOW_ZERO = "黄龄";
    private final String regexSuffix = "(m4a)|(3gp)|(mp3)|(wma)|(ogg)|(wav)|(mid)|(flac)";
    private int indexNumber;
    private String coverDirPath;

    private MusicTag tag;
    private SwipeRefreshLayout refreshLayout;
    private List<Music> itemList = new ArrayList<>();
    List<DefaultAlbum.DefaultMusic> musics = new ArrayList<>();
    private DefaultAlbum album = new DefaultAlbum();
    private MusicEntityDao musicEntityDao;
    private MusicAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_list);
        tag = (MusicTag) getIntent().getSerializableExtra(KEY_TAG);
        if (tag == null)
            return;
        musicEntityDao = ((App) getApplication()).getDaoSession().getMusicEntityDao();
        album.setAlbumId(String.valueOf(tag.getId()));
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(tag.getName());
        RecyclerView rvList = findViewById(R.id.rvList);
        refreshLayout = findViewById(R.id.refreshLayout);
        ImageView ivCover = findViewById(R.id.ivCover);
        TextView tvName = findViewById(R.id.tvName);
        ImageView ivPlay = findViewById(R.id.ivPlay);
        View llMusic = findViewById(R.id.llMusic);
        refreshLayout.setRefreshing(true);
        refreshLayout.setColorSchemeResources(R.color.colorPrimary);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadList();
            }
        });
        rvList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MusicAdapter(this, itemList, musicEntityDao);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                setPlayMusic(position);
                adapter.notifyDataSetChanged();
            }
        });
        rvList.setAdapter(adapter);
        coverDirPath = getExternalCacheDir() + File.separator + "cover" + File.separator ;
        File coverDir = new File(coverDirPath);
        if (!coverDir.exists())
            coverDir.mkdir();
        DefaultPlayerManager.getInstance().getChangeMusicLiveData().observe(this, changeMusic -> {
            for (int i = 0; i < itemList.size(); i++) {
                Music music = itemList.get(i);
                String musicId = String.format(Locale.getDefault(), AppData.FORMAT_MUSIC_ID,
                        tag.getId(), music.getUrl());
                music.setSelected(musicId.equals(changeMusic.getMusicId()));
            }
            adapter.notifyDataSetChanged();
            Glide.with(MusicListLocalActivity.this)
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
        loadList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (itemList.size() == 0)
            return;
        for (Music music : itemList)
            music.setFav(musicEntityDao.queryBuilder()
                    .where(MusicEntityDao.Properties.Url.eq(music.getUrl()))
                    .list().size() > 0);
        adapter.notifyDataSetChanged();
    }

    public void onPlay(View view) {
        DefaultPlayerManager.getInstance().togglePlay();
    }

    public void onNext(View view) {
        DefaultPlayerManager.getInstance().playNext();
    }

    public void onClickPlayDetail(View view) {
        startActivity(new Intent(MusicListLocalActivity.this, MusicPlayActivity.class));
    }

    private void setPlayMusic(int position) {
        if (DefaultPlayerManager.getInstance().getAlbum() == null ||
                !DefaultPlayerManager.getInstance().getAlbum().getAlbumId().equals(album.getAlbumId()))
            DefaultPlayerManager.getInstance().loadAlbum(album);
        DefaultPlayerManager.getInstance().playAudio(position);
        for (Music music : itemList)
            music.setSelected(false);
        itemList.get(position).setSelected(true);
    }

    private void loadList() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                indexNumber = 1;
                itemList.clear();
                musics.clear();
                loadMusic(AppData.MUSIC_NETEASE_PATH);
                loadMusic(AppData.MUSIC_QQ_PATH);
                loadMusic(AppData.MUSIC_KUGOU_PATH);
                loadMusic(AppData.MUSIC_MOO_PATH);
                loadMusic(AppData.QQ_FILES_1);
                loadMusic(AppData.QQ_FILES_2);
                album.setMusics(musics);
                AppData.MUSIC_LOCAL_COUNT = musics.size();
                AppData.saveData(MusicListLocalActivity.this);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                        refreshLayout.setRefreshing(false);
                    }
                });
            }
        }).start();
    }

    private void loadMusic(String dir) {
        File dirMusic = new File(dir);
        if (dirMusic.exists() && dirMusic.isDirectory()) {
            File[] files = dirMusic.listFiles(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    String path = file.getPath();
                    String suffix = FileUtil.getSuffix(path);
                    if (suffix == null)
                        return false;
                    return Pattern.matches(regexSuffix, suffix);
                }
            });
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            if (files != null)
                for (File file : files) {
                    mmr.setDataSource(file.getPath());
                    String artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                    if (file.getPath().contains(YELLOW_ZERO) || (!TextUtils.isEmpty(artist) && artist.contains(YELLOW_ZERO))) {
                        String title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                        if (TextUtils.isEmpty(title))
                            title = FileUtil.getPrefix(file.getPath());
                        String coverPath = coverDirPath + title + ".jpg";
                        File coverFile = new File(coverPath);
                        if (!coverFile.exists()) {
                            byte[] coverData = mmr.getEmbeddedPicture();
                            if (coverData != null)
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try (FileOutputStream fos = new FileOutputStream(coverFile)) {
                                            fos.write(coverData);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }).start();
                        }
                        Music musicData = new Music();
                        musicData.setNumber(indexNumber++);
                        musicData.setName(title);
                        musicData.setCover(coverPath);
                        musicData.setUrl(file.getPath());
                        musicData.setFav(musicEntityDao.queryBuilder().where(MusicEntityDao.Properties.Url.eq(musicData.getUrl())).list().size() > 0);
                        DefaultAlbum.DefaultMusic music = new DefaultAlbum.DefaultMusic();
                        music.setName(title);
                        music.setMusicId(String.format(Locale.getDefault(), AppData.FORMAT_MUSIC_ID,
                                tag.getId(), musicData.getUrl()));
                        music.setUrl(file.getPath());
                        music.setCover(coverPath);
                        if (DefaultPlayerManager.getInstance().isPlaying() &&
                                music.getMusicId().equals(DefaultPlayerManager.getInstance().getCurrentPlayingMusic().getMusicId())) {
                            musicData.setSelected(true);
                        }
                        itemList.add(musicData);
                        musics.add(music);
                    }
                }
        }
    }

    public static void start(Context context, MusicTag tag) {
        Intent intent = new Intent(context, MusicListLocalActivity.class);
        intent.putExtra(KEY_TAG, tag);
        context.startActivity(intent);
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
