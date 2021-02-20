package com.yellowzero.listen.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.yellowzero.listen.R;
import com.yellowzero.listen.model.BilibiliVideo;

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
                .setText(R.id.tvName, item.getName())
                .setText(R.id.tvPlayCount, String.valueOf(item.getPlayCount()))
                .setText(R.id.tvDanmkuCount, String.valueOf(item.getDanmkuCount()));
        if (item.getAvatar() == null) {
            viewHolder.setGone(R.id.tvName, true)
                    .setGone(R.id.ivAvatar, true);
        } else {
            Glide.with(context)
                    .load(item.getAvatar())
                    .transform(new CircleCrop())
                    .into((ImageView) viewHolder.getView(R.id.ivAvatar));
        }
        Glide.with(context)
                .load(item.getCover())
                .transform(new RoundedCorners(10))
                .into((ImageView) viewHolder.getView(R.id.ivCover));
    }
}
