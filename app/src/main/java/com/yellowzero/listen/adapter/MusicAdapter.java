package com.yellowzero.listen.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.View;

import androidx.browser.customtabs.CustomTabsIntent;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.yellowzero.listen.R;
import com.yellowzero.listen.model.Music;

import java.util.List;

public class MusicAdapter extends BaseQuickAdapter<Music, BaseViewHolder>  implements LoadMoreModule {

    private Context context;

    public MusicAdapter(Context context, List<Music> items) {
        super(R.layout.item_music, items);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder viewHolder, Music item) {
        viewHolder.setText(R.id.tvNumber, String.valueOf(item.getNumber()))
                .setText(R.id.tvName, item.getName());
        if (!item.isAvailable())
            viewHolder.setTextColorRes(R.id.tvName, R.color.tvNameDisable);
        else {
            if (item.isSelected())
                viewHolder.setTextColorRes(R.id.tvName, R.color.colorPrimary)
                        .setGone(R.id.tvNumber, true)
                        .setGone(R.id.ivPlaying, false);
            else
                viewHolder.setTextColorRes(R.id.tvName, R.color.black)
                        .setGone(R.id.tvNumber, false)
                        .setGone(R.id.ivPlaying, true);
        }
        viewHolder.getView(R.id.ivVideo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CustomTabsIntent.Builder()
                        .build()
                        .launchUrl(context, Uri.parse(item.getLink()));
            }
        });
    }
}
