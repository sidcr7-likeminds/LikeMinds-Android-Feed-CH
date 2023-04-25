package com.likeminds.feedsampleapp

import android.app.Application
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobile.client.Callback
import com.amazonaws.mobile.client.UserStateDetails
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.likeminds.feedsx.post.PostWithAttachmentsRepository
import com.likeminds.likemindsfeed.LMCallback
import com.likeminds.likemindsfeed.LMFeedClient
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class FeedSXApplication @Inject constructor() : Application(), LMCallback {

    @Inject
    lateinit var transferUtility: TransferUtility

    @Inject
    lateinit var postWithAttachmentsRepository: PostWithAttachmentsRepository

    private lateinit var domain: String

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

        setupBranding()
        setupDomain()
        initAWSMobileClient()

        // extras to instantiate LMFeedClient
        val extra = LMFeedClient.Builder(this)
            .lmCallback(this)
            .build()
    }

    // TODO: testing data
    // sets branding to the app
    private fun setupBranding() {
        val lmFonts = com.likeminds.feedsx.branding.model.LMFonts.Builder()
            .bold("fonts/montserrat-bold.ttf")
            .medium("fonts/montserrat-medium.ttf")
            .regular("fonts/montserrat-regular.ttf")
            .build()

        val setBrandingRequest = com.likeminds.feedsx.branding.model.SetBrandingRequest.Builder()
            .headerColor("#9B26AF")
            .buttonsColor("#E81D62")
            .textLinkColor("#4BAE4F")
            .fonts(lmFonts)
            .build()
        com.likeminds.feedsx.branding.model.LMBranding.setBranding(setBrandingRequest)
    }

    // function to set client domain
    private fun setupDomain() {
        domain = "https://www.sampleapp.com"
    }

    // function to get client domain
    fun getDomain(): String {
        return domain
    }

    private fun initAWSMobileClient() {
        AWSMobileClient.getInstance()
            .initialize(applicationContext, object : Callback<UserStateDetails> {
                override fun onResult(result: UserStateDetails?) {
                }

                override fun onError(e: java.lang.Exception?) {
                }
            })
    }

    override fun login() {
        super.login()
    }
}