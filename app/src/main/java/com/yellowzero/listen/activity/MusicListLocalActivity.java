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

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.yellowzero.listen.AppData;
import com.yellowzero.listen.R;
import com.yellowzero.listen.adapter.MusicLocalAdapter;
import com.yellowzero.listen.model.MusicLocal;
import com.yellowzero.listen.model.MusicTag;
import com.yellowzero.listen.player.DefaultPlayerManager;
import com.yellowzero.listen.player.bean.DefaultAlbum;
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
    private static final String FORMAT_MUSIC_ID = "%d_%s";
    private final String regexSuffix = "(m4a)|(3gp)|(mp3)|(wma)|(ogg)|(wav)|(mid)|(flac)";
    private int indexNumber;
    private String coverDirPath;

    private MusicTag tag;
    private SwipeRefreshLayout refreshLayout;
    private List<MusicLocal> itemList = new ArrayList<>();
    List<DefaultAlbum.DefaultMusic> musics = new ArrayList<>();
    private DefaultAlbum album = new DefaultAlbum();
    private MusicLocalAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_list_local);
        tag = (MusicTag) getIntent().getSerializableExtra(KEY_TAG);
        if (tag == null)
            return;
        album.setAlbumId(String.valueOf(tag.getId()));
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(tag.getName());
        RecyclerView rvList = findViewById(R.id.rvList);
        refreshLayout = findViewById(R.id.refreshLayout);
        refreshLayout.setRefreshing(true);
        refreshLayout.setColorSchemeResources(R.color.colorPrimary);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadList();
            }
        });
        rvList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MusicLocalAdapter(this, itemList);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                setPlayMusic(position);
                adapter.notifyDataSetChanged();
            }
        });
        rvList.setAdapter(adapter);
        coverDirPath = getExternalFilesDir(null) + File.separator + "cover" + File.separator ;
        File coverDir = new File(coverDirPath);
        if (!coverDir.exists())
            coverDir.mkdir();
        loadList();
    }

    private void setPlayMusic(int position) {
        if (DefaultPlayerManager.getInstance().getAlbum() == null ||
                !DefaultPlayerManager.getInstance().getAlbum().getAlbumId().equals(album.getAlbumId()))
            DefaultPlayerManager.getInstance().loadAlbum(album);
        DefaultPlayerManager.getInstance().playAudio(position);
        for (MusicLocal music : itemList)
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
                        MusicLocal musicLocal = new MusicLocal();
                        musicLocal.setNumber(indexNumber++);
                        musicLocal.setName(title);
                        musicLocal.setCover(coverPath);
                        musicLocal.setPath(file.getPath());
                        itemList.add(musicLocal);
                        DefaultAlbum.DefaultMusic music = new DefaultAlbum.DefaultMusic();
                        music.setTitle(title);
                        music.setMusicId(String.format(Locale.getDefault(), FORMAT_MUSIC_ID, tag.getId(), music.getTitle()));
                        music.setUrl(file.getPath());
                        music.setCoverImg(coverPath);
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
