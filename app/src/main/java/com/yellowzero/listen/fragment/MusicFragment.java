package com.yellowzero.listen.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.allen.library.RxHttpUtils;
import com.allen.library.bean.BaseData;
import com.allen.library.interceptor.Transformer;
import com.allen.library.observer.DataObserver;
import com.allen.library.utils.ToastUtils;
import com.google.android.material.tabs.TabLayout;
import com.yellowzero.listen.R;
import com.yellowzero.listen.adapter.TabViewPagerAdapter;
import com.yellowzero.listen.model.MusicTag;
import com.yellowzero.listen.service.MusicService;

import java.util.List;

public class MusicFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_music,null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TabLayout tabLayout = view.findViewById(R.id.tabLayout);
        ViewPager viewPager = view.findViewById(R.id.viewPager);
        TabViewPagerAdapter adapterTab = new TabViewPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(adapterTab);
        tabLayout.setupWithViewPager(viewPager);

        RxHttpUtils.createApi(MusicService.class)
                .tags()
                .compose(Transformer.<BaseData<List<MusicTag>>>switchSchedulers())
                .subscribe(new DataObserver<List<MusicTag>>() {
                    @Override
                    protected void onError(String errorMsg) {
                        ToastUtils.showToast(getResources().getString(R.string.ts_http_error));
                    }

                    @Override
                    protected void onSuccess(List<MusicTag> tags) {
                        for (MusicTag tag : tags)
                            adapterTab.addFragment(MusicListFragment.createInstance(tag.getId()), tag.getName());
                        adapterTab.notifyDataSetChanged();
                    }
                });
    }
}
