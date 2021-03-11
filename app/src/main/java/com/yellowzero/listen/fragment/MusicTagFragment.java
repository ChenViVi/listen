package com.yellowzero.listen.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.allen.library.RxHttpUtils;
import com.allen.library.bean.BaseData;
import com.allen.library.interceptor.Transformer;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.yellowzero.listen.App;
import com.yellowzero.listen.AppData;
import com.yellowzero.listen.R;
import com.yellowzero.listen.activity.MusicListActivity;
import com.yellowzero.listen.activity.MusicListFavActivity;
import com.yellowzero.listen.activity.MusicListLocalActivity;
import com.yellowzero.listen.adapter.MusicTagAdapter;
import com.yellowzero.listen.model.MusicTag;
import com.yellowzero.listen.model.entity.MusicEntityDao;
import com.yellowzero.listen.observer.DataObserver;
import com.yellowzero.listen.player.DefaultPlayerManager;
import com.yellowzero.listen.player.bean.DefaultAlbum;
import com.yellowzero.listen.service.MusicService;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class MusicTagFragment extends Fragment implements OnPermissionCallback {

    private RecyclerView rvList;
    private SwipeRefreshLayout refreshLayout;
    private List<MusicTag> itemList = new ArrayList<>();
    private MusicTagAdapter adapter;
    private MusicTag tag;
    private MusicEntityDao musicEntityDao;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_music_tag,null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        musicEntityDao = ((App) getActivity().getApplication()).getDaoSession().getMusicEntityDao();
        rvList = view.findViewById(R.id.rvList);
        refreshLayout = view.findViewById(R.id.refreshLayout);
        rvList.setLayoutManager(new GridLayoutManager(getContext(), 2));
        adapter = new MusicTagAdapter(getContext(), itemList);
        adapter.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                tag = itemList.get(position);
                if (tag.getId() == MusicTag.ID_LOCAL)
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R)
                        XXPermissions.with(getContext())
                                .permission(Permission.MANAGE_EXTERNAL_STORAGE)
                                .request(MusicTagFragment.this);
                    else
                        XXPermissions.with(getContext())
                                .permission(Permission.READ_EXTERNAL_STORAGE)
                                .request(MusicTagFragment.this);
                else if (tag.getId() == MusicTag.ID_FAV)
                    MusicListFavActivity.start(getContext(), tag);
                else
                    MusicListActivity.start(getContext(), tag);
            }
        });
        rvList.setAdapter(adapter);
        refreshLayout.setColorSchemeResources(R.color.colorPrimary);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadList();
            }
        });
        loadList();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (itemList.size() > 0) {
            setSelectTag();
            if (itemList.size() > 1) {
                itemList.get(0).setCount(musicEntityDao.count());
                itemList.get(1).setCount(AppData.MUSIC_LOCAL_COUNT);
            }
            adapter.notifyDataSetChanged();
        }
    }

    private void setSelectTag() {
        DefaultAlbum album = DefaultPlayerManager.getInstance().getAlbum();
        for (MusicTag tag : itemList)
            if (!DefaultPlayerManager.getInstance().isStop()
                    && album!= null){
                int albumId = Integer.parseInt(album.getAlbumId());
                if (albumId == MusicTag.ID_LOCAL_ARTIST || albumId == MusicTag.ID_LOCAL_OTHER)
                    albumId = MusicTag.ID_LOCAL;
                tag.setSelected(tag.getId() == albumId);
            } else
                tag.setSelected(false);
    }

    public void loadList() {
        RxHttpUtils.createApi(MusicService.class)
                .tags()
                .compose(Transformer.<BaseData<List<MusicTag>>>switchSchedulers())
                .subscribe(new DataObserver<List<MusicTag>>() {

                    @Override
                    protected void onError(String errorMsg) {
                        super.onError(errorMsg);
                        refreshLayout.setRefreshing(false);
                    }

                    @Override
                    protected void onSuccess(List<MusicTag> data) {
                        Context context = getContext();
                        if (context == null)
                            return;
                        itemList.clear();
                        MusicTag favTag = new MusicTag();
                        favTag.setName(context.getString(R.string.tv_music_fav));
                        favTag.setId(MusicTag.ID_FAV);
                        favTag.setCoverRes(R.drawable.ic_music_fav);
                        favTag.setCount(musicEntityDao.count());
                        itemList.add(favTag);
                        MusicTag localTag = new MusicTag();
                        localTag.setName(context.getString(R.string.tv_music_local));
                        localTag.setId(MusicTag.ID_LOCAL);
                        localTag.setCoverRes(R.drawable.ic_music_local);
                        localTag.setCount(AppData.MUSIC_LOCAL_COUNT);
                        itemList.add(localTag);
                        refreshLayout.setRefreshing(false);
                        if (data != null)
                            itemList.addAll(data);
                        setSelectTag();
                        adapter.notifyDataSetChanged();
                        if (adapter.getLoadMoreModule().isLoading())
                            adapter.getLoadMoreModule().loadMoreComplete();
                        adapter.getLoadMoreModule().setEnableLoadMore(data != null && data.size() != 0);
                    }
                });
    }

    @Override
    public void onGranted(List<String> permissions, boolean all) {
        MusicListLocalActivity.start(getContext(), tag);
    }

    @Override
    public void onDenied(List<String> permissions, boolean never) {
        Toast.makeText(getContext(), R.string.ts_permission_storage, Toast.LENGTH_SHORT).show();
    }
}
