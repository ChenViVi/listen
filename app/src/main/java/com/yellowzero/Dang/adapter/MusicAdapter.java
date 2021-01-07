package com.yellowzero.Dang.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.View;

import androidx.browser.customtabs.CustomTabsIntent;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.yellowzero.Dang.R;
import com.yellowzero.Dang.model.Music;

import java.util.List;

public class MusicAdapter extends BaseQuickAdapter<Music, BaseViewHolder>  implements LoadMoreModule {

    private Context context;

    public MusicAdapter(Context context, List<Music> items) {
        super(R.layout.item_music, items);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder viewHolder, Music item) {
        viewHolder.setText(R.id.tvNumber, String.valueOf(item.getId()))
                .setText(R.id.tvName, item.getName());
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
