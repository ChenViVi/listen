package com.yellowzero.listen.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yellowzero.listen.R;
import com.yellowzero.listen.adapter.BilibiliVideoAdapter;
import com.yellowzero.listen.model.BilibiliVideo;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class BilibiliListFavFragment extends Fragment {

    private static final String KEY_TAG_ID = "tagId";

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
        rvList = view.findViewById(R.id.rvList);
        refreshLayout = view.findViewById(R.id.refreshLayout);
        rvList.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new BilibiliVideoAdapter(getContext(), itemList);
        rvList.setAdapter(adapter);
    }

    public static BilibiliListFavFragment createInstance(long tagId) {
        BilibiliListFavFragment fragment = new BilibiliListFavFragment();
        Bundle args = new Bundle();
        args.putLong(KEY_TAG_ID, tagId);
        fragment.setArguments(args);
        return fragment;
    }
}
