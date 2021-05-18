package com.yellowzero.listen.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.yellowzero.listen.R;
import com.yellowzero.listen.model.BilibiliVideo;

import java.util.List;

import androidx.annotation.Nullable;

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
                    .placeholder(R.drawable.ic_holder_square)
                    .error(R.drawable.ic_holder_square)
                    .into((ImageView) viewHolder.getView(R.id.ivAvatar));
        }
        Glide.with(context)
                .load(item.getCover().replace("http","https"))
                .transform(new RoundedCorners(10))
                .placeholder(R.drawable.ic_holder_rectangle)
                .error(R.drawable.ic_holder_rectangle)
                .listener(new RequestListener() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target target, boolean isFirstResource) {
                        Log.e("xxxx", item.getCover().replace("http","https"));
                        Log.e("xxxx", e.getMessage());
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Object resource, Object model, Target target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                })
                .into((ImageView) viewHolder.getView(R.id.ivCover));
//        if(item.getCover().equals("http://i1.hdslb.com/bfs/archive/5ceaa9e1e422b173311f4226f69dc1add0219d60.jpg")) {
//            Log.e("xxxx", "name=" + item.getName());
//        } else {
//            Log.e("xxxx", "name=" + item.getCover());
//        }
    }
}