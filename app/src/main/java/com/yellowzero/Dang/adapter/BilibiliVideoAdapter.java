package com.yellowzero.Dang.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.yellowzero.Dang.R;
import com.yellowzero.Dang.model.BilibiliVideo;

import java.util.List;

public class BilibiliVideoAdapter extends BaseQuickAdapter<BilibiliVideo, BaseViewHolder>  implements LoadMoreModule {

    private Context context;

    public BilibiliVideoAdapter(Context context, List<BilibiliVideo> items) {
        super(R.layout.item_video_bilibili, items);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder viewHolder, BilibiliVideo item) {
        viewHolder.setText(R.id.tvTitle, item.getTitle())
                .setText(R.id.tvPlayCount, "播放量：" + item.getPlay())
                .setText(R.id.tvDanmkuCount, "弹幕数：" + item.getVideoReview());
        Glide.with(context)
                .load(item.getPic())
                .into((ImageView) viewHolder.getView(R.id.ivCover));
    }
}
