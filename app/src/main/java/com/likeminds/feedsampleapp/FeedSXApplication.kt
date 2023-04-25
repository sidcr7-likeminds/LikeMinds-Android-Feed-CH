package com.likeminds.feedsampleapp

import android.app.Application
import com.likeminds.feedsx.SDKApplication
import com.likeminds.feedsx.branding.model.LMFonts
import com.likeminds.feedsx.branding.model.SetBrandingRequest
import com.likeminds.likemindsfeed.LMCallback
import com.likeminds.likemindsfeed.LMFeedClient
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class FeedSXApplication @Inject constructor() : Application(), LMCallback {

    companion object {
        const val LOG_TAG = "LikeMinds"
        private var feedSXApplication: FeedSXApplication? = null

        @JvmStatic
        fun getInstance(): FeedSXApplication {
            if (feedSXApplication == null) {
                feedSXApplication = FeedSXApplication()
            }
            return feedSXApplication!!
        }
    }

    override fun onCreate() {
        super.onCreate()

        // extras to instantiate LMFeedClient
        val extra = LMFeedClient.Builder(this)
            .lmCallback(this)
            .build()
        val brandingRequest = SetBrandingRequest.Builder()
            .headerColor("#9B26AF")
            .buttonsColor("#E81D62")
            .textLinkColor("#4BAE4F")
            .fonts(
                LMFonts.Builder()
                    .bold("fonts/montserrat-bold.ttf")
                    .medium("fonts/montserrat-medium.ttf")
                    .regular("fonts/montserrat-regular.ttf")
                    .build()
            )
            .build()
        val sdkApplication = SDKApplication.getInstance()
        sdkApplication.initSDKApplication(
            this,
            brandingRequest
        )
    }

    override fun login() {
        super.login()
    }
}