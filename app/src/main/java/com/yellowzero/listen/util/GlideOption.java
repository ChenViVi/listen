package com.yellowzero.listen.util;

import android.content.Context;

import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.engine.cache.ExternalPreferredCacheDiskCacheFactory;
import com.bumptech.glide.module.AppGlideModule;

import org.jetbrains.annotations.NotNull;

@GlideModule
public class GlideOption extends AppGlideModule {
    @Override
    public void applyOptions(@NotNull Context context, GlideBuilder builder) {
        builder.setDiskCache(new ExternalPreferredCacheDiskCacheFactory(context));
    }
}
