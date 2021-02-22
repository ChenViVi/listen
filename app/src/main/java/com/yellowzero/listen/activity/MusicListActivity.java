package com.yellowzero.listen.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.allen.library.RxHttpUtils;
import com.allen.library.bean.BaseData;
import com.allen.library.interceptor.Transformer;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.danikula.videocache.HttpProxyCacheServer;
import com.yellowzero.listen.App;
import com.yellowzero.listen.AppData;
import com.yellowzero.listen.R;
import com.yellowzero.listen.adapter.MusicAdapter;
import com.yellowzero.listen.model.Music;
import com.yellowzero.listen.model.MusicTag;
import com.yellowzero.listen.observer.DataObserver;
import com.yellowzero.listen.player.DefaultPlayerManager;
import com.yellowzero.listen.player.bean.DefaultAlbum;
import com.yellowzero.listen.service.MusicService;
import com.yellowzero.listen.util.NetworkChangeReceiver;
import com.yellowzero.listen.util.NetworkUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MusicListActivity extends AppCompatActivity {

    private boolean isFirstResume = true;
    private static final String KEY_TAG = "tag";
    private static final String FORMAT_MUSIC_ID = "%d_%d";
    private MusicTag tag;
    private SwipeRefreshLayout refreshLayout;
    private List<Music> itemList = new ArrayList<>();
    private DefaultAlbum album = new DefaultAlbum();
    private HttpProxyCacheServer proxy;
    private MusicAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_list);
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
        rvList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MusicAdapter(this, itemList);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                if (itemList.get(position).isAvailable()) {
                    setPlayMusic(position);
                    adapter.notifyDataSetChanged();
                } else if (NetworkUtil.getConnectedState(MusicListActivity.this) == NetworkUtil.STATE_MOBILE && !AppData.ENABLE_MUSIC_MOBILE) {
                    new AlertDialog.Builder(MusicListActivity.this)
                            .setTitle(android.R.string.dialog_alert_title)
                            .setMessage(R.string.tt_enable_music_mobile)
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    AppData.ENABLE_MUSIC_MOBILE = true;
                                    AppData.saveData(MusicListActivity.this);
                                    setMusicsAvailable();
                                    setPlayMusic(position);
                                    adapter.notifyDataSetChanged();
                                }
                            })
                            .setNegativeButton(android.R.string.cancel, null)
                            .setCancelable(true)
                            .create()
                            .show();
                }
            }
        });
        rvList.setAdapter(adapter);
        refreshLayout.setColorSchemeResources(R.color.colorPrimary);
        refreshLayout.setRefreshing(true);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (NetworkUtil.getConnectedState(MusicListActivity.this) != NetworkUtil.STATE_OFFLINE)
                    loadList();
                else
                    refreshLayout.setRefreshing(false);
            }
        });
        DefaultPlayerManager.getInstance().getChangeMusicLiveData().observe(this, changeMusic -> {
            for (int i = 0; i < itemList.size(); i++) {
                Music music = itemList.get(i);
                music.setSelected(String.format(Locale.getDefault(), FORMAT_MUSIC_ID, tag.getId(), music.getId()).equals(changeMusic.getMusicId()));
            }
            adapter.notifyDataSetChanged();
        });
        proxy = App.getProxy(MusicListActivity.this);
        App app = ((App)getApplication());
        app.addNetworkListener(new NetworkChangeReceiver.NetworkListener() {
            @Override
            public void onChangeState(int state) {
                for (Music music : itemList) {
                    music.setCached(proxy.isCached(music.getUrl()));
                    setMusicsAvailable();
                }
                adapter.notifyDataSetChanged();
            }
        });
        loadList();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isFirstResume) {
            isFirstResume = false;
            return;
        }
        setMusicsAvailable();
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setMusicsAvailable() {
        int state = NetworkUtil.getConnectedState(this);
        for (Music music : itemList) {
            if (AppData.ENABLE_MUSIC_MOBILE)
                music.setAvailable(state != NetworkUtil.STATE_OFFLINE || music.isCached());
            else
                music.setAvailable(state == NetworkUtil.STATE_WIFI || music.isCached());
        }
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

    public void loadList() {
        RxHttpUtils.createApi(MusicService.class)
                .list(tag.getId())
                .compose(Transformer.<BaseData<List<Music>>>switchSchedulers())
                .subscribe(new DataObserver<List<Music>>() {
                    @Override
                    protected void onSuccess(List<Music> data) {
                        itemList.clear();
                        if (data != null)
                            itemList.addAll(data);
                        List<DefaultAlbum.DefaultMusic> musics = new ArrayList<>();
                        for (int i = 0; i < itemList.size(); i++) {
                            Music musicData = itemList.get(i);
                            musicData.setNumber(i + 1);
                            musicData.setCached(proxy.isCached(musicData.getUrl()));
                            setMusicsAvailable();
                            DefaultAlbum.DefaultMusic music = new DefaultAlbum.DefaultMusic();
                            music.setMusicId(String.format(Locale.getDefault(), FORMAT_MUSIC_ID, tag.getId(), musicData.getId()));
                            music.setTitle(musicData.getName());
                            music.setUrl(musicData.getUrl());
                            music.setCoverImg(musicData.getCover());
                            musics.add(music);
                            if (DefaultPlayerManager.getInstance().isPlaying() &&
                                    music.getMusicId().equals(DefaultPlayerManager.getInstance().getCurrentPlayingMusic().getMusicId())) {
                                musicData.setSelected(true);
                            }
                        }
                        album.setMusics(musics);
                        adapter.notifyDataSetChanged();
                        refreshLayout.setRefreshing(false);
                    }

                    @Override
                    protected void onError(String errorMsg) {
                        super.onError(errorMsg);
                        refreshLayout.setRefreshing(false);
                    }
                });
    }

    public static void start(Context context, MusicTag tag) {
        Intent intent = new Intent(context, MusicListActivity.class);
        intent.putExtra(KEY_TAG, tag);
        context.startActivity(intent);
    }
}