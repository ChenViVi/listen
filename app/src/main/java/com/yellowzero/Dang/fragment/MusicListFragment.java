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
import com.yellowzero.Dang.R;
import com.yellowzero.Dang.adapter.MusicAdapter;
import com.yellowzero.Dang.model.Music;
import com.yellowzero.Dang.model.MusicTag;
import com.yellowzero.Dang.service.MusicService;

import java.util.ArrayList;
import java.util.List;

public class MusicListFragment extends Fragment {

    private static final String KEY_TAG_ID = "tagId";
    private int tagId;
    private RecyclerView rvList;
    private SwipeRefreshLayout refreshLayout;
    private List<Music> itemList = new ArrayList<>();
    private MusicAdapter adapter;

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
