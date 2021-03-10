package com.yellowzero.listen.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import androidx.browser.customtabs.CustomTabsIntent;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import com.yellowzero.listen.R;
import com.yellowzero.listen.activity.WebViewActivity;
import com.yellowzero.listen.model.Music;
import com.yellowzero.listen.model.entity.MusicEntity;
import com.yellowzero.listen.model.entity.MusicEntityDao;
import com.yellowzero.listen.util.PackageUtil;

import java.util.List;

public class MusicAdapter extends BaseQuickAdapter<Music, BaseViewHolder>  implements LoadMoreModule {

    private static final String FORMAT_TIKTOK_WEB = "https://www.iesdouyin.com/share/video/%s";
    private static final String FORMAT_TIKTOK_APP = "snssdk1128://aweme/detail/%s";
    private static final String FORMAT_TIKTOK_APP_SUFFIX = "?refer=web&gd_label=click_wap_detail_download_feature&appParam=%7B%22__type__%22%3A%22wap%22%2C%22position%22%3A%22900718067%22%2C%22parent_group_id%22%3A%226553813763982626051%22%2C%22webid%22%3A%226568996356873356814%22%2C%22gd_label%22%3A%22click_wap%22%7D&needlaunchlog=1";

    private Context context;
    private MusicEntityDao musicEntityDao;

    public MusicAdapter(Context context, List<Music> items, MusicEntityDao musicEntityDao) {
        super(R.layout.item_music, items);
        this.context = context;
        this.musicEntityDao = musicEntityDao;
    }

    @Override
    protected void convert(BaseViewHolder viewHolder, Music item) {
        viewHolder.setText(R.id.tvNumber, String.valueOf(item.getNumber()))
                .setText(R.id.tvName, item.getName())
                .setImageResource(R.id.ivFav, item.isFav()? R.drawable.ic_fav_enable : R.drawable.ic_fav_grey);
        ImageView ivFav = viewHolder.getView(R.id.ivFav);
        ivFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<MusicEntity> musicEntities = musicEntityDao.queryBuilder().where(MusicEntityDao.Properties.Url.eq(item.getUrl())).list();
                boolean isFav = musicEntities.size() > 0;
                if (isFav)
                    musicEntityDao.delete(musicEntities.get(0));
                else
                    musicEntityDao.insert(item.toEntity());
                ivFav.setImageResource(!isFav ? R.drawable.ic_fav_enable : R.drawable.ic_fav_grey);
            }
        });
        if (item.isLocal()) {
            viewHolder.setText(R.id.tvNumber, String.valueOf(item.getNumber()))
                    .setText(R.id.tvName, item.getName())
                    .setGone(R.id.ivVideo, true)
                    .setVisible(R.id.ivCached, true);
            if (item.isSelected())
                viewHolder.setTextColorRes(R.id.tvName, R.color.colorPrimary)
                        .setGone(R.id.tvNumber, true)
                        .setGone(R.id.ivPlaying, false);
            else
                viewHolder.setTextColorRes(R.id.tvName, R.color.black)
                        .setGone(R.id.tvNumber, false)
                        .setGone(R.id.ivPlaying, true);
        } else {
            viewHolder.setVisible(R.id.ivCached, item.isCached())
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
            else if (item.getLink().contains("weibo")) {
                String link = item.getLink().replace("weibo","");
                viewHolder.getView(R.id.ivVideo).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (PackageUtil.isPackageInstalled(context, "com.sina.weibo")) {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.addCategory(Intent.CATEGORY_DEFAULT);
                            intent.addCategory(Intent.CATEGORY_BROWSABLE);
                            intent.setData(Uri.parse("sinaweibo://detail?mblogid=" + link));
                            context.startActivity(intent);
                        } else
                            WebViewActivity.start(context, "https://m.weibo.cn/detail/" + link);
                    }
                });
            }
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
}
