package com.yellowzero.listen.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.allen.library.RxHttpUtils;
import com.allen.library.bean.BaseData;
import com.allen.library.interceptor.Transformer;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.yellowzero.listen.R;
import com.yellowzero.listen.activity.MusicListActivity;
import com.yellowzero.listen.adapter.MusicTagAdapter;
import com.yellowzero.listen.model.MusicTag;
import com.yellowzero.listen.observer.DataObserver;
import com.yellowzero.listen.service.MusicService;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class MusicTagFragment extends Fragment {

    private RecyclerView rvList;
    private SwipeRefreshLayout refreshLayout;
    private List<MusicTag> itemList = new ArrayList<>();
    private MusicTagAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_music_tag,null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvList = view.findViewById(R.id.rvList);
        refreshLayout = view.findViewById(R.id.refreshLayout);
        //rvList.setLayoutManager(new LinearLayoutManager(getContext()));
        rvList.setLayoutManager(new GridLayoutManager(getContext(), 2));
        adapter = new MusicTagAdapter(getContext(), itemList);
        adapter.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                MusicListActivity.start(getContext(), itemList.get(position));
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

    public void loadList() {
        RxHttpUtils.createApi(MusicService.class)
                .tags()
                .compose(Transformer.<BaseData<List<MusicTag>>>switchSchedulers())
                .subscribe(new DataObserver<List<MusicTag>>() {

                    @Override
                    protected void onSuccess(List<MusicTag> data) {
                        itemList.clear();
                        refreshLayout.setRefreshing(false);
                        if (data != null)
                            itemList.addAll(data);
                        adapter.notifyDataSetChanged();
                        if (adapter.getLoadMoreModule().isLoading())
                            adapter.getLoadMoreModule().loadMoreComplete();
                        adapter.getLoadMoreModule().setEnableLoadMore(data != null && data.size() != 0);
                    }
                });
    }
}
