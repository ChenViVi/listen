package com.yellowzero.listen.fragment;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import razerdp.basepopup.BasePopupWindow;

import com.allen.library.RxHttpUtils;
import com.allen.library.bean.BaseData;
import com.allen.library.interceptor.Transformer;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.listener.OnLoadMoreListener;
import com.yellowzero.listen.R;
import com.yellowzero.listen.activity.ImagedDetailActivity;
import com.yellowzero.listen.adapter.ImageAdapter;
import com.yellowzero.listen.model.Image;
import com.yellowzero.listen.model.ImageTag;
import com.yellowzero.listen.observer.DataObserver;
import com.yellowzero.listen.service.ImageService;
import com.yellowzero.listen.view.TagCloudView;
import com.yellowzero.listen.view.TagsPopup;

import java.util.ArrayList;
import java.util.List;

public class ImageFragment extends Fragment {

    private Integer tagId = null;
    private static final int PAGE_SIZE = 20;
    private int page = 0;
    private List<Image> itemList = new ArrayList<>();
    private List<ImageTag> tagList = new ArrayList<>();
    private ImageAdapter adapter;
    private TagsPopup tagsPopup;
    private SwipeRefreshLayout refreshLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_image,null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView rvList = view.findViewById(R.id.rvList);
        refreshLayout = view.findViewById(R.id.refreshLayout);
        ImageView ivMore = view.findViewById(R.id.ivMore);
        rvList.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        adapter = new ImageAdapter(getContext(), itemList);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                ImagedDetailActivity.start(getContext(), itemList.get(position));
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
                loadTags();
            }
        });
        tagsPopup = new TagsPopup(getContext());
        tagsPopup.setOnDismissListener(new BasePopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                ivMore.setImageResource(R.drawable.ic_arrow_right);
            }
        });
        tagsPopup.setOnTagClickListener(new TagCloudView.OnTagClickListener() {
            @Override
            public void onTagClick(int position) {
                if (position < 0 || position >= tagList.size())
                    return;
                page = 0;
                tagId = tagList.get(position).getId();
                loadList();
            }
        });
        view.findViewById(R.id.llTag).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tagsPopup.isShowing()) {
                    ivMore.setImageResource(R.drawable.ic_arrow_right);
                    tagsPopup.dismiss();
                }
                else {
                    ivMore.setImageResource(R.drawable.ic_arrow_down);
                    tagsPopup.setPopupGravity(Gravity.BOTTOM).setAlignBackground(true).showPopupWindow(view);
                }
            }
        });
        loadList();
        loadTags();
    }

    private void loadList() {
        if (tagId == null || tagId != -1)
            RxHttpUtils.createApi(ImageService.class)
                    .list(tagId, page, PAGE_SIZE)
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

    private void loadTags() {
        RxHttpUtils.createApi(ImageService.class)
                .tags()
                .compose(Transformer.<BaseData<List<ImageTag>>>switchSchedulers())
                .subscribe(new DataObserver<List<ImageTag>>() {

                    @Override
                    protected void onSuccess(List<ImageTag> data) {
                        tagList.clear();
                        ImageTag tagAll = new ImageTag();
                        tagAll.setId(null);
                        tagAll.setName(getString(R.string.tv_tag_all));
                        tagList.add(tagAll);
                        ImageTag tagLike = new ImageTag();
                        tagLike.setId(-1);
                        tagLike.setName(getString(R.string.tv_tag_like));
                        tagList.add(tagLike);
                        tagList.addAll(data);
                        tagsPopup.setTags(tagList);
                    }
                });
    }
}
