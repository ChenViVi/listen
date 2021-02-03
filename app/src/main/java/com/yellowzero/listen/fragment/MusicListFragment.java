package com.yellowzero.listen.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.allen.library.RxHttpUtils;
import com.allen.library.bean.BaseData;
import com.allen.library.interceptor.Transformer;
import com.allen.library.observer.DataObserver;
import com.allen.library.utils.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.danikula.videocache.HttpProxyCacheServer;
import com.yellowzero.listen.App;
import com.yellowzero.listen.AppData;
import com.yellowzero.listen.R;
import com.yellowzero.listen.adapter.MusicAdapter;
import com.yellowzero.listen.model.Music;
import com.yellowzero.listen.player.DefaultPlayerManager;
import com.yellowzero.listen.player.bean.DefaultAlbum;
import com.yellowzero.listen.service.MusicService;
import com.yellowzero.listen.util.NetworkChangeReceiver;
import com.yellowzero.listen.util.NetworkUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MusicListFragment extends Fragment {

    private boolean isFirstResume = true;
    private static final String KEY_TAG_ID = "tagId";
    private static final String FORMAT_MUSIC_ID = "%d_%d";
    private int tagId;
    private int selectMusicId = -1;
    private SwipeRefreshLayout refreshLayout;
    private List<Music> itemList = new ArrayList<>();
    private DefaultAlbum album = new DefaultAlbum();
    private HttpProxyCacheServer proxy;
    private MusicAdapter adapter;
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
                if (itemList.get(position).isAvailable()) {
                    setPlayMusic(position);
                    adapter.notifyDataSetChanged();
                } else if (NetworkUtil.getConnectedState(getContext()) == NetworkUtil.STATE_MOBILE && !AppData.ENABLE_MUSIC_MOBILE){
                    new AlertDialog.Builder(getContext())
                            .setTitle(android.R.string.dialog_alert_title)
                            .setMessage(R.string.tt_enable_music_mobile)
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    AppData.ENABLE_MUSIC_MOBILE = true;
                                    AppData.saveData(getContext());
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
        Bundle bundle = getArguments();
        if (bundle != null)
            tagId = bundle.getInt(KEY_TAG_ID);
        refreshLayout.setColorSchemeResources(R.color.colorPrimary);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (NetworkUtil.getConnectedState(getContext()) != NetworkUtil.STATE_OFFLINE)
                    loadList();
                else
                    refreshLayout.setRefreshing(false);
            }
        });
        DefaultPlayerManager.getInstance().getChangeMusicLiveData().observe(this, changeMusic -> {
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
        DefaultPlayerManager.getInstance().getPauseLiveData().observe(this, isPaused -> {
            if (isPaused) {
                selectMusicId = -1;
                for (Music music : itemList)
                    music.setSelected(false);
                adapter.notifyDataSetChanged();
            }
        });
        proxy = App.getProxy(getContext());
        App app = ((App)getActivity().getApplication());
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

    private void setMusicsAvailable() {
        int state = NetworkUtil.getConnectedState(getContext());
        for (Music music : itemList) {
            if (AppData.ENABLE_MUSIC_MOBILE)
                music.setAvailable(state != NetworkUtil.STATE_OFFLINE || music.isCached());
            else
                music.setAvailable(state == NetworkUtil.STATE_WIFI || music.isCached());
        }
    }

    private void setPlayMusic(int position) {
        selectMusicId = itemList.get(position).getId();
        DefaultPlayerManager.getInstance().loadAlbum(album);
        DefaultPlayerManager.getInstance().playAudio(position);
        for (Music music : itemList)
            music.setSelected(false);
        itemList.get(position).setSelected(true);
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
                        if (data != null)
                            itemList.addAll(data);
                        List<DefaultAlbum.DefaultMusic> musics = new ArrayList<>();
                        for (int i = 0; i < itemList.size(); i++) {
                            Music musicData = itemList.get(i);
                            musicData.setNumber(i + 1);
                            musicData.setCached(proxy.isCached(musicData.getUrl()));
                            setMusicsAvailable();
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
