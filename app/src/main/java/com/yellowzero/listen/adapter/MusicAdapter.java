package com.yellowzero.listen.adapter;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;

import androidx.browser.customtabs.CustomTabsIntent;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.yellowzero.listen.R;
import com.yellowzero.listen.activity.WebViewActivity;
import com.yellowzero.listen.model.Music;
import com.yellowzero.listen.util.PackageUtil;

import java.util.List;

public class MusicAdapter extends BaseQuickAdapter<Music, BaseViewHolder>  implements LoadMoreModule {

    private static final String FORMAT_TIKTOK_WEB = "https://www.iesdouyin.com/share/video/%s";
    private static final String FORMAT_TIKTOK_APP = "snssdk1128://aweme/detail/%s";
    private static final String FORMAT_TIKTOK_APP_SUFFIX = "?refer=web&gd_label=click_wap_detail_download_feature&appParam=%7B%22__type__%22%3A%22wap%22%2C%22position%22%3A%22900718067%22%2C%22parent_group_id%22%3A%226553813763982626051%22%2C%22webid%22%3A%226568996356873356814%22%2C%22gd_label%22%3A%22click_wap%22%7D&needlaunchlog=1";

    private Context context;

    public MusicAdapter(Context context, List<Music> items) {
        super(R.layout.item_music, items);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder viewHolder, Music item) {
        viewHolder.setText(R.id.tvNumber, String.valueOf(item.getNumber()))
                .setText(R.id.tvName, item.getName())
                .setGone(R.id.ivCached, item.isCached())
                .setVisible(R.id.ivVideo, !TextUtils.isEmpty(item.getLink()));
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
        if (TextUtils.isEmpty(item.getLink()))
            return;
        if (item.getLink().contains("bilibili"))
            viewHolder.getView(R.id.ivVideo).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (PackageUtil.isPackageInstalled(context, "tv.danmaku.bili")) {
                        new CustomTabsIntent.Builder()
                                .build()
                                .launchUrl(context, Uri.parse(item.getLink()));
                    } else
                        WebViewActivity.start(context, item.getLink());
                }
            });
        else {
            viewHolder.getView(R.id.ivVideo).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (PackageUtil.isPackageInstalled(context, "com.ss.android.ugc.aweme")) {
                        new CustomTabsIntent.Builder()
                                .build()
                                .launchUrl(context, Uri.parse(String.format(FORMAT_TIKTOK_APP, item.getLink()) + FORMAT_TIKTOK_APP_SUFFIX));
                    } else
                        WebViewActivity.start(context, String.format(FORMAT_TIKTOK_WEB, item.getLink()));
                }
            });
        }
    }
}
