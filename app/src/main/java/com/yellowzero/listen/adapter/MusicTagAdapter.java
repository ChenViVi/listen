package com.yellowzero.listen.adapter;

import android.content.Context;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.yellowzero.listen.R;
import com.yellowzero.listen.model.MusicTag;
import com.yellowzero.listen.util.ScreenUtil;

import java.util.List;

public class MusicTagAdapter extends BaseQuickAdapter<MusicTag, BaseViewHolder>  implements LoadMoreModule {

    private Context context;

    public MusicTagAdapter(Context context, List<MusicTag> items) {
        super(R.layout.item_music_tag, items);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder viewHolder, MusicTag item) {
        viewHolder.setText(R.id.tvName, item.getName())
                .setText(R.id.tvNum, String.format(context.getString(R.string.tv_music_count), item.getCount()));
        ImageView ivCover = viewHolder.getView(R.id.ivCover);
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) ivCover.getLayoutParams();
        lp.width = lp.height = ScreenUtil.getScreenWidth(context)/2 - ScreenUtil.dp2px(5);
        ivCover.setLayoutParams(lp);
        Glide.with(context)
                .load(item.getCover())
                .transform(new RoundedCorners(10))
                .into(ivCover);
    }
}
