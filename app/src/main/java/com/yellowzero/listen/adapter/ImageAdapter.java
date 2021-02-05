package com.yellowzero.listen.adapter;

import android.content.Context;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.yellowzero.listen.R;
import com.yellowzero.listen.model.Image;
import com.yellowzero.listen.model.ImageInfo;
import com.yellowzero.listen.util.ScreenUtil;

import java.util.List;

public class ImageAdapter extends BaseQuickAdapter<Image, BaseViewHolder>  implements LoadMoreModule {

    private Context context;

    public ImageAdapter(Context context, List<Image> items) {
        super(R.layout.item_image, items);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder viewHolder, Image item) {
        viewHolder.setText(R.id.tvName, item.getUser().getName());
        Glide.with(context)
                .load(item.getUser().getAvatar())
                .transform(new CircleCrop())
                .placeholder(R.drawable.ic_holder)
                .error(R.drawable.ic_holder)
                .into((ImageView) viewHolder.getView(R.id.ivAvatar));
        ImageView ivImage = viewHolder.getView(R.id.ivImage);
        ImageInfo imageInfo = item.getImageInfoSmall();
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)ivImage.getLayoutParams();
        lp.width = (ScreenUtil.getAppScreenWidth(context) - ScreenUtil.dp2px(10)) / 2;
        lp.height = lp.width * imageInfo.getHeight() / imageInfo.getWidth();
        ivImage.setLayoutParams(lp);
        if (item.isGif())
            Glide.with(context)
                    .load(item.getImageInfoLarge().getUrl())
                    .placeholder(R.drawable.ic_holder)
                    .error(R.drawable.ic_holder)
                    .into((ImageView) viewHolder.getView(R.id.ivImage));
        else
            Glide.with(context)
                    .load(item.getImageInfoSmall().getUrl())
                    .into((ImageView) viewHolder.getView(R.id.ivImage));
    }
}
