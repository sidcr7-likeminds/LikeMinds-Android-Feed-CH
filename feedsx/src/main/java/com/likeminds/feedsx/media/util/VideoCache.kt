package com.likeminds.feedsx.media.util

import android.content.Context
import com.google.android.exoplayer2.database.StandaloneDatabaseProvider
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import java.io.File
import javax.inject.Singleton

@Singleton
class VideoCache {
    companion object {
        private var sDownloadCache: SimpleCache? = null

        fun getInstance(context: Context): SimpleCache {
            val exoPlayerCacheSize = 50 * 1024 * 1024.toLong()// Set the size of cache for video
            val leastRecentlyUsedCacheEvictor =
                LeastRecentlyUsedCacheEvictor(exoPlayerCacheSize)
            val exoDatabaseProvider = StandaloneDatabaseProvider(context)

            val cache = File(context.cacheDir, "Video_Cache")
            if (!cache.exists()) {
                cache.mkdirs()
            }

            if (sDownloadCache == null) {
                sDownloadCache =
                    SimpleCache(cache, leastRecentlyUsedCacheEvictor, exoDatabaseProvider)
            }
            return sDownloadCache!!
        }
    }
}