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
import com.allen.library.bean.BaseData;
import com.allen.library.interceptor.Transformer;
import com.yellowzero.listen.R;
import com.yellowzero.listen.activity.SettingActivity;
import com.yellowzero.listen.adapter.ScheduleAdapter;
import com.yellowzero.listen.model.AppInfo;
import com.yellowzero.listen.model.Schedule;
import com.yellowzero.listen.observer.DataObserver;
import com.yellowzero.listen.service.AppService;
import com.yellowzero.listen.service.ScheduleService;
import com.yellowzero.listen.view.UpdateDialog;

import java.util.ArrayList;
import java.util.List;

public class ScheduleFragment extends Fragment {

    private RecyclerView rvList;
    private SwipeRefreshLayout refreshLayout;
    private List<Schedule> itemList = new ArrayList<>();
    private ScheduleAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_schedule,null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        rvList = view.findViewById(R.id.rvList);
        refreshLayout = view.findViewById(R.id.refreshLayout);
        rvList.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ScheduleAdapter(getContext(), itemList);
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

    private void loadList() {
        RxHttpUtils.createApi(ScheduleService.class)
                .list()
                .compose(Transformer.<BaseData<List<Schedule>>>switchSchedulers())
                .subscribe(new DataObserver<List<Schedule>>(this) {

                    @Override
                    protected void onError(String errorMsg) {
                        super.onError(errorMsg);
                        refreshLayout.setRefreshing(false);
                    }

                    @Override
                    protected void onSuccess(List<Schedule> data) {
                        itemList.clear();
                        if (data != null)
                            itemList.addAll(data);
                        adapter.notifyDataSetChanged();
                        refreshLayout.setRefreshing(false);
                    }
                });
    }
}
