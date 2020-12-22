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

import com.yellowzero.Dang.R;
import com.yellowzero.Dang.adapter.MusicAdapter;
import com.yellowzero.Dang.model.Music;

import java.util.ArrayList;
import java.util.List;

public class MusicListFragment extends Fragment {
    private RecyclerView rvList;
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
        rvList.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MusicAdapter(getContext(), itemList);
        rvList.setAdapter(adapter);

        itemList.add(new Music(1, 1, "我们都不应该孤单", "https://www.bilibili.com/video/BV1Xi4y1V7rp"));
        itemList.add(new Music(2, 2, "我们都不应该孤单", "https://www.bilibili.com/video/BV1Xi4y1V7rp"));
        itemList.add(new Music(3, 3, "我们都不应该孤单", "https://www.bilibili.com/video/BV1Xi4y1V7rp"));
        itemList.add(new Music(4, 4, "我们都不应该孤单", "https://www.bilibili.com/video/BV1Xi4y1V7rp"));
        itemList.add(new Music(5, 5, "我们都不应该孤单", "https://www.bilibili.com/video/BV1Xi4y1V7rp"));
        itemList.add(new Music(6, 6, "我们都不应该孤单", "https://www.bilibili.com/video/BV1Xi4y1V7rp"));
        itemList.add(new Music(7, 7, "我们都不应该孤单", "https://www.bilibili.com/video/BV1Xi4y1V7rp"));
        itemList.add(new Music(8, 8, "我们都不应该孤单", "https://www.bilibili.com/video/BV1Xi4y1V7rp"));
        adapter.notifyDataSetChanged();
    }
}
