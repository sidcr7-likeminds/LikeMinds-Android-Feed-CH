package com.likeminds.feedsampleapp

import android.app.Application
import com.likeminds.feedsampleapp.auth.util.AuthPreferences
import com.likeminds.feedsx.LMFeedUI
import com.likeminds.feedsx.LMFeedUICallback
import com.likeminds.feedsx.branding.model.LMFeedFonts
import com.likeminds.feedsx.branding.model.SetFeedBrandingRequest

class FeedSXApplication : Application(), LMFeedUICallback {

    private lateinit var authPreferences: AuthPreferences

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

        authPreferences = AuthPreferences(this)

        val brandingRequest = SetFeedBrandingRequest.Builder()
            .headerColor(authPreferences.getHeaderColor())
            .buttonsColor(authPreferences.getButtonColor())
            .textLinkColor(authPreferences.getTextLinkColor())
            .fonts(
                LMFeedFonts.Builder()
                    .bold("fonts/montserrat-bold.ttf")
                    .medium("fonts/montserrat-medium.ttf")
                    .regular("fonts/montserrat-regular.ttf")
                    .build()
            )
            .build()

        LMFeedUI.initLikeMindsFeedUI(
            this,
            this,
            brandingRequest
        )
    }

    override fun login() {
        super.login()
    }
}