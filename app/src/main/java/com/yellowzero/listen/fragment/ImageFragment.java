package com.yellowzero.listen.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.allen.library.RxHttpUtils;
import com.allen.library.bean.BaseData;
import com.allen.library.interceptor.Transformer;
import com.allen.library.observer.DataObserver;
import com.allen.library.utils.ToastUtils;
import com.chad.library.adapter.base.listener.OnLoadMoreListener;
import com.yellowzero.listen.R;
import com.yellowzero.listen.adapter.ImageAdapter;
import com.yellowzero.listen.model.Image;
import com.yellowzero.listen.model.Music;
import com.yellowzero.listen.player.bean.DefaultAlbum;
import com.yellowzero.listen.service.ImageService;
import com.yellowzero.listen.service.MusicService;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ImageFragment extends Fragment {

    private static final int PAGE_SIZE = 20;
    private int page = 0;
    private List<Image> itemList = new ArrayList<>();
    private ImageAdapter adapter;
    private RecyclerView rvList;
    private SwipeRefreshLayout refreshLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_image,null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvList = view.findViewById(R.id.rvList);
        refreshLayout = view.findViewById(R.id.refreshLayout);
        rvList.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        adapter = new ImageAdapter(getContext(), itemList);
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
        /*for (int i = 0; i < 2; i ++) {
            Image image1 = new Image();
            image1.setAvatar("https://tvax4.sinaimg.cn/crop.0.0.512.512.180/4c5b3c65ly8gjws7cuwn4j20e80e8dge.jpg?KID=imgbed,tva&Expires=1611418334&ssig=o9T5em%2BIbG");
            image1.setUserName("黄龄");
            image1.setImageUrl("https://wx1.sinaimg.cn/orj360/4c5b3c65gy1gllc3ly6psj21pc29se81.jpg");
            itemList.add(image1);
            Image image2 = new Image();
            image2.setAvatar("https://tvax1.sinaimg.cn/crop.0.0.512.512.180/008dKzr9ly8glecxo4seij30e80e8gn3.jpg?KID=imgbed,tva&Expires=1611418505&ssig=cTOiLuyyfz");
            image2.setUserName("耿耿有掉不光的头发");
            image2.setImageUrl("https://wx2.sinaimg.cn/large/008dKzr9ly1gmqtyob4agj31400u0duf.jpg");
            itemList.add(image2);
            Image image3 = new Image();
            image3.setAvatar("https://tvax1.sinaimg.cn/crop.0.0.512.512.180/008dKzr9ly8glecxo4seij30e80e8gn3.jpg?KID=imgbed,tva&Expires=1611418505&ssig=cTOiLuyyfz");
            image3.setUserName("耿耿有掉不光的头发");
            image3.setImageUrl("https://wx2.sinaimg.cn/large/008dKzr9ly1gmqtyp7s2jj30u01lmn2o.jpg");
            itemList.add(image3);
            Image image4 = new Image();
            image4.setAvatar("https://tvax1.sinaimg.cn/crop.0.0.512.512.180/008dKzr9ly8glecxo4seij30e80e8gn3.jpg?KID=imgbed,tva&Expires=1611418505&ssig=cTOiLuyyfz");
            image4.setUserName("耿耿有掉不光的头发");
            image4.setImageUrl("https://wx2.sinaimg.cn/large/008dKzr9ly1gmqtyou4n6j30qv1kw1en.jpg");
            itemList.add(image4);
            Image image5 = new Image();
            image5.setAvatar("https://tvax1.sinaimg.cn/crop.0.0.512.512.180/008dKzr9ly8glecxo4seij30e80e8gn3.jpg?KID=imgbed,tva&Expires=1611418505&ssig=cTOiLuyyfz");
            image5.setUserName("<a  href=\\\"https://m.weibo.cn/search?containerid=231522type%3D1%26t%3D10%26q%3D%23%E7%94%A8%E9%9F%B3%E4%B9%90%E8%AF%B4%E7%88%B1%E4%BD%A0%23&luicode=10000011&lfid=1076031281047653\\\" data-hide=\\\"\\\"><span class=\\\"surl-text\\\">#用音乐说爱你#</span></a><a  href=\\\"https://m.weibo.cn/search?containerid=231522type%3D1%26t%3D10%26q%3D%23%E4%B8%83%E5%A4%95%E8%BF%99%E6%A0%B7%E6%8B%8D%23&extparam=%23%E4%B8%83%E5%A4%95%E8%BF%99%E6%A0%B7%E6%8B%8D%23&luicode=10000011&lfid=1076031281047653\\\" data-hide=\\\"\\\"><span class=\\\"surl-text\\\">#七夕这样拍#</span></a> 谢谢\uD83D\uDD14duangduang们的❤️  一起来玩表白大赛吧<span class=\\\"url-icon\\\"><img alt=[来] src=\\\"https://h5.sinaimg.cn/m/emoticon/icon/others/h_lai-da75e9a8cc.png\\\" style=\\\"width:1em; height:1em;\\\" /></span><span class=\\\"url-icon\\\"><img alt=[害羞] src=\\\"https://h5.sinaimg.cn/m/emoticon/icon/default/d_haixiu-094a0fcce9.png\\\" style=\\\"width:1em; height:1em;\\\" /></span> <a data-url=\\\"http://t.cn/A6UsBNOQ\\\" href=\\\"https://video.weibo.com/show?fid=1034:4541760181829644\\\" data-hide=\\\"\\\"><span class='url-icon'><img style='width: 1rem;height: 1rem' src='https://h5.sinaimg.cn/upload/2015/09/25/3/timeline_card_small_video_default.png'></span><span class=\\\"surl-text\\\">黄龄的微博视频</span></a> ");
            image5.setImageUrl("https://wx1.sinaimg.cn/large/008dKzr9ly1gmqtypypprj30yo0u0hdt.jpg");
            itemList.add(image5);
        }
        adapter.notifyDataSetChanged();*/
        loadList();
    }

    private void loadList() {
        RxHttpUtils.createApi(ImageService.class)
                .list(page,PAGE_SIZE)
                .compose(Transformer.<BaseData<List<Image>>>switchSchedulers())
                .subscribe(new DataObserver<List<Image>>() {
                    @Override
                    protected void onError(String errorMsg) {
                        ToastUtils.showToast(getResources().getString(R.string.ts_http_error));
                    }

                    @Override
                    protected void onSuccess(List<Image> data) {
                        Log.e("xxxxxx", "data.size()=" + data.size());
                        for (int i = 0; i < data.size(); i++) {
                            Log.e("xxxxx", "i=" + i);
                            Log.e("xxxxxx", "weiboId=" + data.get(i).getWeiboId());
                        }

                        if (page == 0) {
                            itemList.clear();
                            refreshLayout.setRefreshing(false);
                        }
                        itemList.addAll(data);
                        adapter.notifyDataSetChanged();
                        if (adapter.getLoadMoreModule().isLoading())
                            adapter.getLoadMoreModule().loadMoreComplete();
                        adapter.getLoadMoreModule().setEnableLoadMore(data.size() != 0);
                    }
                });
    }
}
