package com.likeminds.feedsampleapp

import android.app.Application
import com.likeminds.feedsampleapp.auth.util.AuthPreferences
import com.likeminds.feedsx.LMUICallback
import com.likeminds.feedsx.LikeMindsFeedUI
import com.likeminds.feedsx.branding.model.LMFonts
import com.likeminds.feedsx.branding.model.SetBrandingRequest

class FeedSXApplication : Application(), LMUICallback {

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

        val brandingRequest = SetBrandingRequest.Builder()
            .headerColor(authPreferences.getHeaderColor())
            .buttonsColor(authPreferences.getButtonColor())
            .textLinkColor(authPreferences.getTextLinkColor())
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