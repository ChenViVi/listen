package com.yellowzero.listen.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.yellowzero.listen.AppData;
import com.yellowzero.listen.R;
import com.yellowzero.listen.util.FileUtil;

import java.io.File;

public class CacheDialog extends Dialog {
    public CacheDialog(@NonNull Context context) {
        super(context);
    }

    protected void onDeleteFinish() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_cache);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindow().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = displayMetrics.widthPixels;
        getWindow().setAttributes(lp);
        View llImageCache = findViewById(R.id.llImageCache);
        View llMusicCache = findViewById(R.id.llMusicCache);
        TextView tvImageCacheSize = findViewById(R.id.tvImageCacheSize);
        TextView tvMusicCacheSize = findViewById(R.id.tvMusicCacheSize);
        File dirImageCache = new File(AppData.CACHE_IMAGE_DIR);
        File dirCoverCache = new File(AppData.CACHE_COVER_DIR);
        File dirMusicCache = new File(AppData.CACHE_MUSIC_DIR);
        long imageCacheSize = FileUtil.getFileSize(dirImageCache) + FileUtil.getFileSize(dirCoverCache);
        tvImageCacheSize.setText(FileUtil.formatFileSize(imageCacheSize));
        tvMusicCacheSize.setText(FileUtil.getFileSizeString(dirMusicCache));
        llImageCache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llImageCache.setEnabled(false);
                tvImageCacheSize.setText(R.string.tv_clean_cache_process);
                if (FileUtil.deleteDirectory(dirImageCache.getPath()) && FileUtil.deleteDirectory(dirCoverCache.getPath()))
                    tvImageCacheSize.setText(R.string.tv_clean_cache_success);
                else
                    tvImageCacheSize.setText(R.string.tv_clean_cache_error);
                llImageCache.setEnabled(true);
                onDeleteFinish();
            }
        });
        llMusicCache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llMusicCache.setEnabled(false);
                tvMusicCacheSize.setText(R.string.tv_clean_cache_process);
                if (FileUtil.deleteDirectory(dirMusicCache.getPath()))
                    tvMusicCacheSize.setText(R.string.tv_clean_cache_success);
                else
                    tvMusicCacheSize.setText(R.string.tv_clean_cache_error);
                llMusicCache.setEnabled(true);
                onDeleteFinish();
            }
        });
    }
}
