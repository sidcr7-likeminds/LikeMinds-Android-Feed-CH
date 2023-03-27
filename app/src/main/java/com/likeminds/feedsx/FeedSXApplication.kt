package com.likeminds.feedsx

import android.app.Application
import com.likeminds.likemindsfeed.LMFeedClient
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class FeedSXApplication : Application() {
    companion object {
        const val LOG_TAG = "LikeMinds"
    }

    override fun onCreate() {
        super.onCreate()
        // extras to instantiate LMFeedClient
        val extra = LMFeedClient.Builder(this)
            .build()
    }
}