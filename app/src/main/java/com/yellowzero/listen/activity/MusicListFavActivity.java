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
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.danikula.videocache.HttpProxyCacheServer;
import com.yellowzero.listen.App;
import com.yellowzero.listen.AppData;
import com.yellowzero.listen.R;
import com.yellowzero.listen.adapter.MusicAdapter;
import com.yellowzero.listen.model.MusicTag;
import com.yellowzero.listen.model.Music;
import com.yellowzero.listen.model.entity.MusicEntity;
import com.yellowzero.listen.model.entity.MusicEntityDao;
import com.yellowzero.listen.player.DefaultPlayerManager;
import com.yellowzero.listen.player.bean.DefaultAlbum;
import com.yellowzero.listen.player.contract.IPlayController;
import com.yellowzero.listen.util.NetworkChangeReceiver;
import com.yellowzero.listen.util.NetworkUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MusicListFavActivity extends AppCompatActivity {

    private static final String KEY_TAG = "tag";
    private MusicTag tag;
    private SwipeRefreshLayout refreshLayout;
    private List<Music> itemList = new ArrayList<>();
    private DefaultAlbum album = new DefaultAlbum();
    private HttpProxyCacheServer proxy;
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
        proxy = App.getProxy(this);
        album.setAlbumId(String.valueOf(tag.getId()));
        album.setTitle(tag.getName());
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
        rvList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MusicAdapter(this, itemList, musicEntityDao);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                if (refreshLayout.isRefreshing() || position >= album.getMusics().size())
                    return;
                if (itemList.get(position).isAvailable()) {
                    setPlayMusic(position);
                    adapter.notifyDataSetChanged();
                } else if (NetworkUtil.getConnectedState(MusicListFavActivity.this) == NetworkUtil.STATE_MOBILE && !AppData.ENABLE_MUSIC_MOBILE) {
                    new AlertDialog.Builder(MusicListFavActivity.this)
                            .setTitle(android.R.string.dialog_alert_title)
                            .setMessage(R.string.tt_enable_music_mobile)
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    AppData.ENABLE_MUSIC_MOBILE = true;
                                    AppData.saveData(MusicListFavActivity.this);
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
                if (NetworkUtil.getConnectedState(MusicListFavActivity.this) != NetworkUtil.STATE_OFFLINE)
                    loadList();
                else
                    refreshLayout.setRefreshing(false);
            }
        });
        proxy = App.getProxy(MusicListFavActivity.this);
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
        DefaultPlayerManager.getInstance().getChangeMusicLiveData().observe(this, changeMusic -> {
            for (int i = 0; i < itemList.size(); i++) {
                Music music = itemList.get(i);
                String musicId = String.format(Locale.getDefault(), AppData.FORMAT_MUSIC_ID,
                        tag.getId(), music.getUrl());
                music.setSelected(musicId.equals(changeMusic.getMusicId()));
            }
            adapter.notifyDataSetChanged();
            Glide.with(MusicListFavActivity.this)
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
                    for (int i = 0; i < itemList.size(); i++)
                        itemList.get(i).setSelected(false);
                    adapter.notifyDataSetChanged();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onPlay(View view) {
        DefaultPlayerManager.getInstance().togglePlay();
    }

    public void onNext(View view) {
        DefaultPlayerManager.getInstance().playNext();
    }

    public void onClickPlayDetail(View view) {
        startActivity(new Intent(MusicListFavActivity.this, MusicPlayActivity.class));
    }

    private void setMusicsAvailable() {
        int state = NetworkUtil.getConnectedState(this);
        for (Music music : itemList) {
            if (music.getUrl().contains("http"))
                if (AppData.ENABLE_MUSIC_MOBILE)
                    music.setAvailable(state != NetworkUtil.STATE_OFFLINE || music.isCached());
                else
                    music.setAvailable(state == NetworkUtil.STATE_WIFI || music.isCached());
            else
                music.setAvailable(true);
        }
    }

    private void setPlayMusic(int position) {
        DefaultPlayerManager.getInstance().loadAlbum(album);
        DefaultPlayerManager.getInstance().playAudio(position);
        for (Music music : itemList)
            music.setSelected(false);
        itemList.get(position).setSelected(true);
    }

    public void loadList() {
        itemList.clear();
        List<DefaultAlbum.DefaultMusic> musics = new ArrayList<>();
        List<MusicEntity> musicEntities = musicEntityDao.queryBuilder().orderDesc(MusicEntityDao.Properties.Id).list();
        for (int i = 0; i < musicEntities.size(); i++) {
            MusicEntity musicEntity = musicEntities.get(i);
            Music musicData = new Music(musicEntity);
            musicData.setNumber(i + 1);
            musicData.setCached(proxy.isCached(musicEntity.getUrl()));
            musicData.setFav(true);
            itemList.add(musicData);
            DefaultAlbum.DefaultMusic music = new DefaultAlbum.DefaultMusic();
            music.setMusicId(String.format(Locale.getDefault(), AppData.FORMAT_MUSIC_ID,
                    tag.getId(), musicData.getUrl()));
            music.setName(musicData.getName());
            music.setUrl(musicData.getUrl());
            music.setCover(musicData.getCover());
            music.setLink(musicData.getLink());
            musics.add(music);
            if (DefaultPlayerManager.getInstance().isPlaying() &&
                    music.getMusicId().equals(DefaultPlayerManager.getInstance().getCurrentPlayingMusic().getMusicId())) {
                musicData.setSelected(true);
            }
        }
        setMusicsAvailable();
        album.setMusics(musics);
        adapter.notifyDataSetChanged();
        refreshLayout.setRefreshing(false);
    }

    public static void start(Context context, MusicTag tag) {
        Intent intent = new Intent(context, MusicListFavActivity.class);
        intent.putExtra(KEY_TAG, tag);
        context.startActivity(intent);
    }
}