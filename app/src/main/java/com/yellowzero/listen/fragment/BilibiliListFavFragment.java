package com.yellowzero.listen.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.allen.library.RxHttpUtils;
import com.allen.library.interceptor.Transformer;
import com.allen.library.observer.StringObserver;
import com.allen.library.utils.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yellowzero.listen.R;
import com.yellowzero.listen.adapter.BilibiliVideoAdapter;
import com.yellowzero.listen.model.BilibiliVideo;
import com.yellowzero.listen.model.BilibiliFav;
import com.yellowzero.listen.service.BilibiliService;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BilibiliListFavFragment extends Fragment {

    private static final String KEY_TAG_ID = "bizId";

    private long bizId;
    private RecyclerView rvList;
    private SwipeRefreshLayout refreshLayout;
    private List<BilibiliVideo> itemList = new ArrayList<>();
    private BilibiliVideoAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bilibili_list_fav,null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null)
            bizId = bundle.getLong(KEY_TAG_ID);
        rvList = view.findViewById(R.id.rvList);
        refreshLayout = view.findViewById(R.id.refreshLayout);
        rvList.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new BilibiliVideoAdapter(getContext(), itemList);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                new CustomTabsIntent.Builder()
                        .build()
                        .launchUrl(getContext(), Uri.parse(itemList.get(position).getUrl()));
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

    public static BilibiliListFavFragment createInstance(long bizId) {
        BilibiliListFavFragment fragment = new BilibiliListFavFragment();
        Bundle args = new Bundle();
        args.putLong(KEY_TAG_ID, bizId);
        fragment.setArguments(args);
        return fragment;
    }

    public void loadList() {
        RxHttpUtils.createApi("bilibili","https://api.bilibili.com/", BilibiliService.class)
                .list(bizId,3,1000)
                .compose(Transformer.<String>switchSchedulers())
                .subscribe(new StringObserver() {

                    @Override
                    protected void onError(String errorMsg) {
                        ToastUtils.showToast("请求失败");
                        refreshLayout.setRefreshing(false);
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
                            JSONArray vlist = dataJson.getJSONObject("data").getJSONArray("mediaList");
                            Gson gson = new Gson();
                            List<BilibiliFav> videos = gson.fromJson(vlist.toString(), new TypeToken<List<BilibiliFav>>() {}.getType());
                            itemList.clear();
                            refreshLayout.setRefreshing(false);
                            if (videos != null)
                                for (BilibiliFav video : videos)
                                    itemList.add(new BilibiliVideo(video));
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
