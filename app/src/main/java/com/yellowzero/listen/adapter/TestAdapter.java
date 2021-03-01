package com.yellowzero.listen.adapter;

import android.content.Context;
import android.text.Html;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.yellowzero.listen.R;
import com.yellowzero.listen.activity.ImageActivity;
import com.yellowzero.listen.model.Image;
import com.yellowzero.listen.view.TagCloudView;

import java.util.List;

public class TestAdapter extends BaseQuickAdapter<Image, BaseViewHolder>  implements LoadMoreModule {

    private Context context;

    public TestAdapter(Context context, List<Image> items) {
        super(R.layout.item_test, items);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder viewHolder, Image item) {
        ImageView ivImage = viewHolder.getView(R.id.ivImage);
        View svDetail = viewHolder.getView(R.id.svDetail);
        TagCloudView viewTag = viewHolder.getView(R.id.viewTag);
        ivImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ivImage.setVisibility(View.GONE);
                svDetail.setVisibility(View.VISIBLE);
            }
        });
        viewTag.setTags(item.getTagList());
        viewTag.setOnTagClickListener(new TagCloudView.OnTagClickListener() {
            @Override
            public void onTagClick(int position) {
                ImageActivity.start(context, item.getTags().get(position));
            }
        });
        viewHolder.setText(R.id.tvText, Html.fromHtml(item.getText()));
        if (item.isGif())
            Glide.with(context)
                    .load(item.getImageInfoLarge().getUrl())
                    .placeholder(R.drawable.ic_holder_square)
                    .error(R.drawable.ic_holder_square)
                    .into(ivImage);
        else
            Glide.with(context)
                    .load(item.getImageInfoLarge().getUrl())
                    .thumbnail(Glide.with(context).load(item.getImageInfoSmall().getUrl()))
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .placeholder(R.drawable.ic_holder_square)
                    .error(R.drawable.ic_holder_square)
                    .into(ivImage);
        svDetail.setOnTouchListener(new Test(ivImage, svDetail));
    }

    class Test implements View.OnTouchListener {

        private View ivImage;
        private View svDetail;

        Test(View ivImage, View svDetail) {
            this.ivImage = ivImage;
            this.svDetail = svDetail;
        }

        private int mXOld,mYOld;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int x = (int) event.getX();
            int y = (int) event.getY();
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                mXOld = x;
                mYOld = y;
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                if (x == mXOld || y == mYOld) {
                    if (ivImage.getVisibility() != View.VISIBLE) {
                        ivImage.setVisibility(View.VISIBLE);
                        svDetail.setVisibility(View.GONE);
                    }
                    return false;
                }
            }
            return false;
        }
    }
}
