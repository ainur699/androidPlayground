package com.vicman.jnitest.utils;

import android.content.Context;
import android.support.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.module.GlideModule;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class GlideConfiguration implements GlideModule {
    public static final int TIMEOUT_READ_WRITE = 30000;
    public static final int TIMEOUT_CONNECT_MILLIS = 15000;
    public static final int TIMEOUT_RETRY_MILLIS = 1000;
    public static final int UPLOAD_DOWNLOAD_RETRY_COUNT = 2;

    private static final int DISK_CACHE_SIZE_BYTES = 50 * 1024 * 1024; // 50MB
    @Override
    public void applyOptions(final @NotNull Context context, final @NotNull GlideBuilder builder) {
        // Apply options to the builder here.

        builder.setDecodeFormat(DecodeFormat.PREFER_ARGB_8888);
        builder.setDiskCache(new InternalCacheDiskCacheFactory(context, DISK_CACHE_SIZE_BYTES));
    }

    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {

    }
}