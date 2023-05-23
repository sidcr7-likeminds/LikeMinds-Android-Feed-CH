package com.likeminds.feedsampleapp

import android.app.Application
import com.LMUICallback
import com.likeminds.feedsx.LikeMindsFeedUI
import com.likeminds.feedsx.branding.model.LMFonts
import com.likeminds.feedsx.branding.model.SetBrandingRequest

class FeedSXApplication : Application(), LMUICallback {

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

        val brandingRequest = SetBrandingRequest.Builder()
            .headerColor("#02A8F3")
            .buttonsColor("#4BAE4F")
            .textLinkColor("#FE9700")
            .fonts(
                LMFonts.Builder()
                    .bold("fonts/montserrat-bold.ttf")
                    .medium("fonts/montserrat-medium.ttf")
                    .regular("fonts/montserrat-regular.ttf")
                    .build()
            )
            .build()

        LikeMindsFeedUI.initLikeMindsFeedUI(
            this,
            this,
            brandingRequest
        )
    }

    override fun login() {
        super.login()
    }
}