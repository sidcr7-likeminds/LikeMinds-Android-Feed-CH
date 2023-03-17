package com.likeminds.feedsx

import android.app.Application
import com.likeminds.likemindsfeed.LMFeedClient
import com.likeminds.likemindsfeed.sdk.model.InitiateLikeMindsExtra
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class FeedSXApplication : Application() {
    companion object {
        const val LOG_TAG = "LikeMinds"
    }

    override fun onCreate() {
        super.onCreate()
        // extras to instantiate LMFeedClient
        val extra = InitiateLikeMindsExtra.Builder()
            .application(this)
            .build()
        LMFeedClient.build(extra)
    }
}