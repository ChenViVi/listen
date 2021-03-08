package com.yellowzero.listen.fragment;

import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.yellowzero.listen.App;
import com.yellowzero.listen.AppData;
import com.yellowzero.listen.R;
import com.yellowzero.listen.activity.MusicListLocalActivity;
import com.yellowzero.listen.activity.MusicPlayActivity;
import com.yellowzero.listen.adapter.MusicAdapter;
import com.yellowzero.listen.model.Music;
import com.yellowzero.listen.model.MusicTag;
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

public class TestFragment extends Fragment {
    private static final String KEY_IS_ARTIST = "isArtist";
    private final String regexSuffix = "(m4a)|(3gp)|(mp3)|(wma)|(ogg)|(wav)|(mid)|(flac)";
    private int indexNumber;
    private String coverDirPath;

    private String yellowZero;
    private MusicTag tag;
    private SwipeRefreshLayout refreshLayout;
    private List<Music> itemList = new ArrayList<>();
    List<DefaultAlbum.DefaultMusic> musics = new ArrayList<>();
    private DefaultAlbum album = new DefaultAlbum();
    private MusicEntityDao musicEntityDao;
    private MusicAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_test,null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle == null)
            return;
        album.setAlbumId(getString(bundle.getBoolean(KEY_IS_ARTIST, true)? R.string.yellow_zero : R.string.tv_other));
        musicEntityDao = ((App) getActivity().getApplication()).getDaoSession().getMusicEntityDao();
        album.setAlbumId(String.valueOf(tag.getId()));
        RecyclerView rvList = view.findViewById(R.id.rvList);
        refreshLayout = view.findViewById(R.id.refreshLayout);
        ImageView ivCover = view.findViewById(R.id.ivCover);
        TextView tvName = view.findViewById(R.id.tvName);
        ImageView ivPlay = view.findViewById(R.id.ivPlay);
        View llMusic = view.findViewById(R.id.llMusic);
        refreshLayout.setRefreshing(true);
        refreshLayout.setColorSchemeResources(R.color.colorPrimary);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadList();
            }
        });
        rvList.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MusicAdapter(getContext(), itemList, musicEntityDao);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                setPlayMusic(position);
                adapter.notifyDataSetChanged();
            }
        });
        rvList.setAdapter(adapter);
        coverDirPath = getContext().getExternalCacheDir() + File.separator + "cover" + File.separator ;
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
            Glide.with(getContext())
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
    public void onResume() {
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
        startActivity(new Intent(getContext(), MusicPlayActivity.class));
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
                loadMusic(AppData.MUSIC_MIGU);
                album.setMusics(musics);
                AppData.MUSIC_LOCAL_COUNT = musics.size();
                AppData.saveData(getContext());
                getActivity().runOnUiThread(new Runnable() {
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
                    if (file.getPath().contains(yellowZero)
                            || (!TextUtils.isEmpty(artist) && artist.contains(yellowZero))) {
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

    public static TestFragment createInstance(boolean isArtist) {
        TestFragment fragment = new TestFragment();
        Bundle args = new Bundle();
        args.putBoolean(KEY_IS_ARTIST, isArtist);
        fragment.setArguments(args);
        return fragment;
    }
}
