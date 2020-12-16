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

import com.allen.library.RxHttpUtils;
import com.allen.library.interceptor.Transformer;
import com.allen.library.observer.StringObserver;
import com.allen.library.utils.ToastUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yellowzero.Dang.R;
import com.yellowzero.Dang.adapter.BilibiliVideoAdapter;
import com.yellowzero.Dang.model.BilibiliVideo;
import com.yellowzero.Dang.service.BilibiliService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BilibiliFragment extends Fragment {

    private RecyclerView rvList;
    private List<BilibiliVideo> videoList = new ArrayList<>();
    private BilibiliVideoAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bilibili,null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvList = view.findViewById(R.id.rvList);
        rvList.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new BilibiliVideoAdapter(getContext(), videoList);
        rvList.setAdapter(adapter);
        RxHttpUtils.createApi("bilibili","https://api.bilibili.com/", BilibiliService.class)
                .getVideos("345630501", 16, 1,
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
                            JSONObject dataJson= new JSONObject(data);
                            int code = dataJson.getInt("code");
                            if (code != 0) {
                                ToastUtils.showToast("请求失败");
                                return;
                            }
                            JSONArray vlist = dataJson.getJSONObject("data").getJSONObject("list").getJSONArray("vlist");
                            Gson gson = new Gson();
                            List<BilibiliVideo> videos = gson.fromJson(vlist.toString(), new TypeToken<List<BilibiliVideo>>() {}.getType());
                            videoList.addAll(videos);
                            adapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
}
