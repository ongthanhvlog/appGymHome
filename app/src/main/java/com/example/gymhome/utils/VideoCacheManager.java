package com.example.gymhome.utils;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.OptIn;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.datasource.DataSource;
import androidx.media3.datasource.DataSpec;
import androidx.media3.datasource.DefaultDataSource;
import androidx.media3.datasource.DefaultHttpDataSource;
import androidx.media3.datasource.cache.CacheDataSource;
import androidx.media3.datasource.cache.CacheWriter;
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor;
import androidx.media3.datasource.cache.SimpleCache;
import java.io.File;

@OptIn(markerClass = UnstableApi.class)
public class VideoCacheManager {
    private static SimpleCache sDownloadCache;
    private static CacheDataSource.Factory sCacheDataSourceFactory;
    private static final long CACHE_SIZE = 100 * 1024 * 1024;

    public static CacheDataSource.Factory getCacheDataSourceFactory(Context context) {
        if (sCacheDataSourceFactory == null) {
            DefaultHttpDataSource.Factory httpDataSourceFactory = new DefaultHttpDataSource.Factory()
                    .setAllowCrossProtocolRedirects(true);

            DefaultDataSource.Factory dataSourceFactory = new DefaultDataSource.Factory(
                    context, httpDataSourceFactory);

            sCacheDataSourceFactory = new CacheDataSource.Factory()
                    .setCache(getCache(context))
                    .setUpstreamDataSourceFactory(dataSourceFactory)
                    .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR);
        }
        return sCacheDataSourceFactory;
    }

    private static SimpleCache getCache(Context context) {
        if (sDownloadCache == null) {
            File cacheDirectory = new File(context.getExternalCacheDir(), "exo_video_cache");
            sDownloadCache = new SimpleCache(
                    cacheDirectory,
                    new LeastRecentlyUsedCacheEvictor(CACHE_SIZE),
                    new androidx.media3.database.StandaloneDatabaseProvider(context)
            );
        }
        return sDownloadCache;
    }

    public static void preCacheVideo(Context context, String url) {
        if (url == null || url.isEmpty() || !url.startsWith("http")) return;

        new Thread(() -> {
            try {
                Uri uri = Uri.parse(url);
                DataSpec dataSpec = new DataSpec(uri);
                CacheWriter cacheWriter = new CacheWriter(
                        getCacheDataSourceFactory(context.getApplicationContext()).createDataSource(),
                        dataSpec,
                        null,
                        null
                );
                cacheWriter.cache();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}