package com.likeminds.feedsx

import android.app.Application
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobile.client.Callback
import com.amazonaws.mobile.client.UserStateDetails
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.likeminds.likemindsfeed.LMFeedClient
import com.likeminds.likemindsfeed.sdk.model.InitiateLikeMindsExtra
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class FeedSXApplication @Inject constructor() : Application() {

    @Inject
    lateinit var transferUtility: TransferUtility

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

        initAWSMobileClient()

        // extras to instantiate LMFeedClient
        val extra = InitiateLikeMindsExtra.Builder()
            .application(this)
            .build()
        LMFeedClient.build(extra)
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
}