package com.likeminds.feedsx

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
        val extra = LMFeedClient.Builder(this)
            .lmCallback(this)
            .build()
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