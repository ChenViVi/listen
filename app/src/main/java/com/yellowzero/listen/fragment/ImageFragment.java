package com.yellowzero.listen.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.allen.library.RxHttpUtils;
import com.allen.library.bean.BaseData;
import com.allen.library.interceptor.Transformer;
import com.allen.library.observer.DataObserver;
import com.allen.library.utils.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.listener.OnLoadMoreListener;
import com.yellowzero.listen.R;
import com.yellowzero.listen.activity.ImageActivity;
import com.yellowzero.listen.adapter.ImageAdapter;
import com.yellowzero.listen.model.Image;
import com.yellowzero.listen.model.Music;
import com.yellowzero.listen.player.bean.DefaultAlbum;
import com.yellowzero.listen.service.ImageService;
import com.yellowzero.listen.service.MusicService;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ImageFragment extends Fragment {

    private static final int PAGE_SIZE = 20;
    private int page = 0;
    private List<Image> itemList = new ArrayList<>();
    private ImageAdapter adapter;
    private RecyclerView rvList;
    private SwipeRefreshLayout refreshLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_image,null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvList = view.findViewById(R.id.rvList);
        refreshLayout = view.findViewById(R.id.refreshLayout);
        rvList.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        adapter = new ImageAdapter(getContext(), itemList);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                Image image = itemList.get(position);
                Intent intent = new Intent(getContext(), ImageActivity.class);
                intent.putExtra("image", image);
                startActivity(intent);
            }
        });
        adapter.getLoadMoreModule().setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                page++;
                loadList();
            }
        });
        rvList.setAdapter(adapter);
        refreshLayout.setColorSchemeResources(R.color.colorPrimary);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 0;
                loadList();
            }
        });
        loadList();
    }

    private void loadList() {
        RxHttpUtils.createApi(ImageService.class)
                .list(page,PAGE_SIZE)
                .compose(Transformer.<BaseData<List<Image>>>switchSchedulers())
                .subscribe(new DataObserver<List<Image>>() {
                    @Override
                    protected void onError(String errorMsg) {
                        ToastUtils.showToast(getResources().getString(R.string.ts_http_error));
                    }

                    @Override
                    protected void onSuccess(List<Image> data) {
                        Log.e("xxxxxx", "data.size()=" + data.size());
                        for (int i = 0; i < data.size(); i++) {
                            Log.e("xxxxx", "i=" + i);
                            Log.e("xxxxxx", "weiboId=" + data.get(i).getWeiboId());
                        }

                        if (page == 0) {
                            itemList.clear();
                            refreshLayout.setRefreshing(false);
                        }
                        itemList.addAll(data);
                        adapter.notifyDataSetChanged();
                        if (adapter.getLoadMoreModule().isLoading())
                            adapter.getLoadMoreModule().loadMoreComplete();
                        adapter.getLoadMoreModule().setEnableLoadMore(data.size() != 0);
                    }
                });
    }
}
