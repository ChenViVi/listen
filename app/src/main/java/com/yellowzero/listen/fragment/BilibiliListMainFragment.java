package com.yellowzero.listen.fragment;

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
import com.allen.library.interceptor.Transformer;
import com.allen.library.observer.StringObserver;
import com.allen.library.utils.ToastUtils;
import com.chad.library.adapter.base.listener.OnLoadMoreListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yellowzero.listen.R;
import com.yellowzero.listen.adapter.BilibiliVideoAdapter;
import com.yellowzero.listen.model.BilibiliVideo;
import com.yellowzero.listen.service.BilibiliService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BilibiliListMainFragment extends Fragment {

    private final int PAGE_SIZE = 20;
    private int page = 1;

    private RecyclerView rvList;
    private SwipeRefreshLayout refreshLayout;
    private List<BilibiliVideo> itemList = new ArrayList<>();
    private BilibiliVideoAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bilibili_list_main,null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvList = view.findViewById(R.id.rvList);
        refreshLayout = view.findViewById(R.id.refreshLayout);
        rvList.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new BilibiliVideoAdapter(getContext(), itemList);
        rvList.setAdapter(adapter);
        adapter.getLoadMoreModule().setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                page++;
                loadList();
            }
        });
        refreshLayout.setColorSchemeResources(R.color.colorPrimary);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                loadList();
            }
        });
        loadList();
    }

    public void loadList() {
        RxHttpUtils.createApi("bilibili","https://api.bilibili.com/", BilibiliService.class)
                .getVideos("345630501", PAGE_SIZE, page,
                        "pubdate", "jsonp")
                .compose(Transformer.<String>switchSchedulers())
                .subscribe(new StringObserver() {

                    @Override
                    protected void onError(String errorMsg) {
                        ToastUtils.showToast("请求失败");
                    }

                    @Override
                    protected void onSuccess(String data) {
                        try {
                            JSONObject dataJson = new JSONObject(data);
                            int code = dataJson.getInt("code");
                            if (code != 0) {
                                ToastUtils.showToast("请求失败");
                                return;
                            }
                            JSONArray vlist = dataJson.getJSONObject("data").getJSONObject("list").getJSONArray("vlist");
                            Gson gson = new Gson();
                            List<BilibiliVideo> videos = gson.fromJson(vlist.toString(), new TypeToken<List<BilibiliVideo>>() {}.getType());
                            if (page == 1) {
                                itemList.clear();
                                refreshLayout.setRefreshing(false);
                            }
                            if (videos != null)
                                itemList.addAll(videos);
                            adapter.notifyDataSetChanged();
                            if (adapter.getLoadMoreModule().isLoading())
                                adapter.getLoadMoreModule().loadMoreComplete();
                            adapter.getLoadMoreModule().setEnableLoadMore(videos != null && videos.size() != 0);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
}
