package com.yellowzero.Dang.fragment;

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
import com.allen.library.observer.StringObserver;
import com.allen.library.utils.ToastUtils;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yellowzero.Dang.R;
import com.yellowzero.Dang.TestFragment;
import com.yellowzero.Dang.adapter.TabViewPagerAdapter;
import com.yellowzero.Dang.model.BilibiliVideo;
import com.yellowzero.Dang.model.MusicTag;
import com.yellowzero.Dang.service.BilibiliService;
import com.yellowzero.Dang.service.MusicService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
