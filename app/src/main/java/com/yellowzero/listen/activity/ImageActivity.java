package com.yellowzero.listen.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.allen.library.RxHttpUtils;
import com.allen.library.bean.BaseData;
import com.allen.library.interceptor.Transformer;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.listener.OnLoadMoreListener;
import com.yellowzero.listen.R;
import com.yellowzero.listen.adapter.ImageAdapter;
import com.yellowzero.listen.model.Image;
import com.yellowzero.listen.model.ImageTag;
import com.yellowzero.listen.observer.DataObserver;
import com.yellowzero.listen.service.ImageService;

import java.util.ArrayList;
import java.util.List;

public class ImageActivity extends AppCompatActivity {

    private static final String KEY_TAG = "tag";

    private ImageTag tag;
    private static final int PAGE_SIZE = 20;
    private int page = 0;
    private List<Image> itemList = new ArrayList<>();
    private ImageAdapter adapter;
    private SwipeRefreshLayout refreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        tag = (ImageTag) getIntent().getSerializableExtra(KEY_TAG);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(tag.getName());
        RecyclerView rvList = findViewById(R.id.rvList);
        refreshLayout = findViewById(R.id.refreshLayout);
        rvList.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        adapter = new ImageAdapter(this, itemList);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                ImagedDetailActivity.start(ImageActivity.this, itemList.get(position));
            }
        });
        adapter.getLoadMoreModule().setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                page++;
                loadList();
            }
        });
        rvList.setAdapter(adapter);
        refreshLayout.setColorSchemeResources(R.color.colorPrimary);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 0;
                loadList();
            }
        });
        loadList();
    }

    public static void start(Context context, @NonNull ImageTag tag) {
        Intent intent = new Intent(context, ImageActivity.class);
        intent.putExtra(KEY_TAG, tag);
        context.startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadList() {
        RxHttpUtils.createApi(ImageService.class)
                .list(tag.getId(), page, PAGE_SIZE)
                .compose(Transformer.<BaseData<List<Image>>>switchSchedulers())
                .subscribe(new DataObserver<List<Image>>() {

                    @Override
                    protected void onSuccess(List<Image> data) {
                        if (page == 0) {
                            itemList.clear();
                            refreshLayout.setRefreshing(false);
                        }
                        if (data != null)
                            itemList.addAll(data);
                        adapter.notifyDataSetChanged();
                        if (adapter.getLoadMoreModule().isLoading())
                            adapter.getLoadMoreModule().loadMoreComplete();
                        adapter.getLoadMoreModule().setEnableLoadMore(data != null && data.size() != 0);
                    }
                });
    }
}