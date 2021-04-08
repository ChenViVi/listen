package com.yellowzero.listen.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.allen.library.RxHttpUtils;
import com.allen.library.bean.BaseData;
import com.allen.library.interceptor.Transformer;
import com.google.android.material.tabs.TabLayout;
import com.yellowzero.listen.R;
import com.yellowzero.listen.adapter.TabViewPagerAdapter;
import com.yellowzero.listen.model.VideoTag;
import com.yellowzero.listen.observer.DataObserver;
import com.yellowzero.listen.service.VideoService;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

public class BilibiliFragment extends Fragment {

    private TabViewPagerAdapter adapterTab;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bilibili,null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TabLayout tabLayout = view.findViewById(R.id.tabLayout);
        ViewPager viewPager = view.findViewById(R.id.viewPager);
        adapterTab = new TabViewPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(adapterTab);
        tabLayout.setupWithViewPager(viewPager);
        adapterTab.addFragment(new BilibiliListMainFragment(), getString(R.string.tv_bilibili_main));
        RxHttpUtils.createApi(VideoService.class)
                .tags()
                .compose(Transformer.<BaseData<List<VideoTag>>>switchSchedulers())
                .subscribe(new DataObserver<List<VideoTag>>(this) {

                    @Override
                    protected void onSuccess(List<VideoTag> tags) {
                        for (VideoTag tag : tags)
                            adapterTab.addFragment(BilibiliListFavFragment.createInstance(tag.getBizId()), tag.getName());
                        adapterTab.notifyDataSetChanged();
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        adapterTab.notifyDataSetChanged();
    }
}
