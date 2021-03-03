package com.yellowzero.listen.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
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
                .placeholder(R.drawable.ic_holder_circle)
                .error(R.drawable.ic_holder_circle)
                .into((ImageView) viewHolder.getView(R.id.ivAvatar));
        ImageView ivImage = viewHolder.getView(R.id.ivImage);
        ImageInfo imageInfo = item.getImageInfoSmall();
        boolean isResized = false;
        if (imageInfo.getWidth() !=0 && imageInfo.getHeight() != 0) {
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)ivImage.getLayoutParams();
            lp.width = ScreenUtil.getAppScreenWidth(context)/2 - ScreenUtil.dp2px(5);
            lp.height = lp.width * imageInfo.getHeight() / imageInfo.getWidth();
            ivImage.setLayoutParams(lp);
            isResized = true;
        }
        String urlImage;
        boolean isGif = item.isGif();
        if (isGif)
            urlImage = item.getImageInfoLarge().getUrl();
        else
            urlImage = item.getImageInfoSmall().getUrl();
        RequestBuilder<Drawable> builder = Glide.with(context)
                .load(urlImage)
                .placeholder(R.drawable.ic_holder_square)
                .error(R.drawable.ic_holder_square);
        if(isGif)
            builder = builder.thumbnail(Glide.with(context).load(item.getImageInfoSmall().getUrl()));
        if (!isResized)
            builder = builder.listener(new ImageRequestListener(ivImage));
        builder.into(ivImage);
    }

    class ImageRequestListener implements RequestListener<Drawable> {

        private ImageView ivImage;

        ImageRequestListener(ImageView ivImage) {
            this.ivImage = ivImage;
        }

        @Override
        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
            return false;
        }

        @Override
        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)ivImage.getLayoutParams();
            lp.width = ScreenUtil.getAppScreenWidth(context)/2 - ScreenUtil.dp2px(5);
            lp.height = lp.width * resource.getIntrinsicHeight() / resource.getIntrinsicWidth();
            ivImage.setLayoutParams(lp);
            return false;
        }
    }
}
