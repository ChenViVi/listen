package com.yellowzero.listen.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.yellowzero.listen.R;
import com.yellowzero.listen.model.Image;

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
/*        TextView tvName = viewHolder.getView(R.id.tvName);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            tvName.setText(Html.fromHtml(item.getUserName(), Html.FROM_HTML_MODE_COMPACT));
        } else {
            tvName.setText(Html.fromHtml(item.getUserName()));
        }*/
        Glide.with(context)
                .load(item.getUrlSmall())
                .into((ImageView) viewHolder.getView(R.id.ivImage));
        Glide.with(context)
                .load(item.getUser().getAvatar())
                .transform(new CircleCrop())
                .placeholder(R.drawable.ic_holder)
                .into((ImageView) viewHolder.getView(R.id.ivAvatar));
    }
}
