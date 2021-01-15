package com.yellowzero.Dang.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.allen.library.RxHttpUtils;
import com.allen.library.bean.BaseData;
import com.allen.library.interceptor.Transformer;
import com.allen.library.observer.DataObserver;
import com.allen.library.utils.ToastUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.kunminx.player.bean.DefaultAlbum;
import com.yellowzero.Dang.R;
import com.yellowzero.Dang.activity.MainActivity;
import com.yellowzero.Dang.adapter.MusicAdapter;
import com.yellowzero.Dang.model.Music;
import com.yellowzero.Dang.service.MusicService;
import com.yellowzero.Dang.util.PlayerManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MusicListFragment extends Fragment {

    private static final String KEY_TAG_ID = "tagId";
    private static final String FORMAT_MUSIC_ID = "%d_%d";
    private int tagId;
    private int selectMusicId = -1;
    private SwipeRefreshLayout refreshLayout;
    private List<Music> itemList = new ArrayList<>();
    private MusicAdapter adapter;
    private DefaultAlbum album = new DefaultAlbum();
    private RecyclerView rvList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_music_list,null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvList = view.findViewById(R.id.rvList);
        refreshLayout = view.findViewById(R.id.refreshLayout);
        rvList.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MusicAdapter(getContext(), itemList);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                selectMusicId = itemList.get(position).getId();
                PlayerManager.getInstance().loadAlbum(album);
                PlayerManager.getInstance().playAudio(position);
                for (Music music : itemList)
                    music.setSelected(false);
                itemList.get(position).setSelected(true);
                adapter.notifyDataSetChanged();
            }
        });
        rvList.setAdapter(adapter);
        Bundle bundle = getArguments();
        if (bundle != null)
            tagId = bundle.getInt(KEY_TAG_ID);
        refreshLayout.setColorSchemeResources(R.color.colorPrimary);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadList();
            }
        });
        loadList();
        PlayerManager.getInstance().getChangeMusicLiveData().observe(this, changeMusic -> {
            int position = -1;
            for (int i = 0; i < itemList.size(); i++) {
                Music music = itemList.get(i);
                if (String.format(Locale.getDefault(), FORMAT_MUSIC_ID, tagId, music.getId()).equals(changeMusic.getMusicId())) {
                    position = music.getId();
                    music.setSelected(true);
                } else {
                    music.setSelected(false);
                }
            }
            selectMusicId = position;
            adapter.notifyDataSetChanged();
        });
    }

    public void loadList() {
        RxHttpUtils.createApi(MusicService.class)
                .list(tagId)
                .compose(Transformer.<BaseData<List<Music>>>switchSchedulers())
                .subscribe(new DataObserver<List<Music>>() {
                    @Override
                    protected void onError(String errorMsg) {
                        ToastUtils.showToast(getResources().getString(R.string.ts_http_error));
                    }

                    @Override
                    protected void onSuccess(List<Music> data) {
                        itemList.clear();
                        itemList.addAll(data);
                        List<DefaultAlbum.DefaultMusic> musics = new ArrayList<>();
                        for (Music musicData : itemList) {
                            DefaultAlbum.DefaultMusic music = new DefaultAlbum.DefaultMusic();
                            music.setMusicId(String.format(Locale.getDefault(), FORMAT_MUSIC_ID, tagId, musicData.getId()));
                            music.setTitle(musicData.getName());
                            music.setUrl(musicData.getUrl());
                            music.setCoverImg(musicData.getCover());
                            musics.add(music);
                            if (musicData.getId() == selectMusicId && selectMusicId != -1) {
                                musicData.setSelected(true);
                            }
                        }
                        album.setMusics(musics);
                        adapter.notifyDataSetChanged();
                        refreshLayout.setRefreshing(false);
                    }
                });
    }

    public static MusicListFragment createInstance(int tagId) {
        MusicListFragment fragment = new MusicListFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_TAG_ID, tagId);
        fragment.setArguments(args);
        return fragment;
    }
}
