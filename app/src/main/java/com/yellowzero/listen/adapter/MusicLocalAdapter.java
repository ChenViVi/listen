package com.yellowzero.listen.adapter;

import android.content.Context;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.yellowzero.listen.R;
import com.yellowzero.listen.model.MusicLocal;

import java.util.List;

public class MusicLocalAdapter extends BaseQuickAdapter<MusicLocal, BaseViewHolder>  implements LoadMoreModule {

    private Context context;

    public MusicLocalAdapter(Context context, List<MusicLocal> items) {
        super(R.layout.item_music_local, items);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder viewHolder, MusicLocal item) {
        viewHolder.setText(R.id.tvNumber, String.valueOf(item.getNumber()))
                .setText(R.id.tvName, item.getName());
        if (item.isSelected())
            viewHolder.setTextColorRes(R.id.tvName, R.color.colorPrimary)
                    .setGone(R.id.tvNumber, true)
                    .setGone(R.id.ivPlaying, false);
        else
            viewHolder.setTextColorRes(R.id.tvName, R.color.black)
                    .setGone(R.id.tvNumber, false)
                    .setGone(R.id.ivPlaying, true);
    }
}
