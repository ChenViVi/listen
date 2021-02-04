package com.yellowzero.listen.adapter;

import android.net.Uri;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.yellowzero.listen.R;
import com.yellowzero.listen.model.BilibiliVideo;

import java.util.List;

public class BilibiliVideoAdapter extends BaseQuickAdapter<BilibiliVideo, BaseViewHolder>  implements LoadMoreModule {

    public BilibiliVideoAdapter(List<BilibiliVideo> items) {
        super(R.layout.item_video_bilibili, items);
    }

    @Override
    protected void convert(BaseViewHolder viewHolder, BilibiliVideo item) {
        viewHolder.setText(R.id.tvTitle, item.getTitle())
                .setText(R.id.tvPlayCount, "播放量：" + item.getPlay())
                .setText(R.id.tvDanmkuCount, "弹幕数：" + item.getVideoReview());
        SimpleDraweeView ivCover = viewHolder.getView(R.id.ivCover);
        ivCover.setImageURI(Uri.parse(item.getPic()));
    }
}
