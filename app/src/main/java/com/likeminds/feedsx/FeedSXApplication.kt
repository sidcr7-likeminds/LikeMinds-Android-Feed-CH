package com.likeminds.feedsx

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class FeedSXApplication : Application() {
    companion object {
        const val LOG_TAG = "LikeMinds"
    }
}