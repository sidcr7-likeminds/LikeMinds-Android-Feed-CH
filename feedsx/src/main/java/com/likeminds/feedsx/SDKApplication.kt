package com.likeminds.feedsx

import android.app.Application
import android.content.Context
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobile.client.Callback
import com.amazonaws.mobile.client.UserStateDetails
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.likeminds.feedsx.branding.model.LMBranding
import com.likeminds.feedsx.branding.model.SetBrandingRequest
import com.likeminds.feedsx.di.AppComponent
import com.likeminds.feedsx.di.feed.FeedComponent
import com.likeminds.feedsx.di.likes.LikesComponent
import com.likeminds.feedsx.di.media.MediaComponent
import com.likeminds.feedsx.di.notificationfeed.NotificationFeedComponent
import com.likeminds.feedsx.post.PostWithAttachmentsRepository
import javax.inject.Inject

class SDKApplication {

    @Inject
    lateinit var transferUtility: TransferUtility

    @Inject
    lateinit var postWithAttachmentsRepository: PostWithAttachmentsRepository

    private var appComponent: AppComponent? = null
    private var feedComponent: FeedComponent? = null
    private var likesComponent: LikesComponent? = null
    private var mediaComponent: MediaComponent? = null
    private var notificationFeedComponent: NotificationFeedComponent? = null

    companion object {
        const val LOG_TAG = "LikeMinds"
        private var sdkApplicationInstance: SDKApplication? = null

        /**
         * @return Singleton Instance of SDK Application class, which used for injecting dagger in fragments.
         * */
        @JvmStatic
        fun getInstance(): SDKApplication {
            if (sdkApplicationInstance == null) {
                sdkApplicationInstance = SDKApplication()
            }
            return sdkApplicationInstance!!
        }
    }

    private lateinit var domain: String

    fun initSDKApplication(
        application: Application,
        brandingRequest: SetBrandingRequest
    ) {
        setupBranding(brandingRequest)
        setupDomain()
        initAppComponent(application)
        initAWSMobileClient(application)
    }

    // TODO: testing data
    // sets branding to the app
    private fun setupBranding(setBrandingRequest: SetBrandingRequest) {
        LMBranding.setBranding(setBrandingRequest)
    }

    // function to set client domain
    private fun setupDomain() {
        domain = "https://www.sampleapp.com"
    }

    // function to get client domain
    fun getDomain(): String {
        return domain
    }

    private fun initAWSMobileClient(applicationContext: Context) {
        AWSMobileClient.getInstance()
            .initialize(applicationContext, object : Callback<UserStateDetails> {
                override fun onResult(result: UserStateDetails?) {
                }

                override fun onError(e: java.lang.Exception?) {
                }
            })
    }

    /**
     * initiate dagger for the sdk
     *
     * @param application : The client will pass instance application to the function
     * */
    private fun initAppComponent(application: Application) {
        if (appComponent == null) {
            appComponent = DaggerAppComponent.builder()
                .application(application)
                .build()
        }
        appComponent!!.inject(this)
    }

    /**
     * initiate and return FeedComponent: All dependencies required for feed package
     * */
    fun feedComponent(): FeedComponent? {
        if (feedComponent == null) {
            feedComponent = appComponent?.feedComponent()?.create()
        }
        return feedComponent
    }

    /**
     * initiate and return LikesComponent: All dependencies required for likes package
     * */
    fun likesComponent(): LikesComponent? {
        if (likesComponent == null) {
            likesComponent = appComponent?.likesComponent()?.create()
        }
        return likesComponent
    }

    /**
     * initiate and return MediaComponent: All dependencies required for media package
     * */
    fun mediaComponent(): MediaComponent? {
        if (mediaComponent == null) {
            mediaComponent = appComponent?.mediaComponent()?.create()
        }
        return mediaComponent
    }

    /**
     * initiate and return NotificationFeedComponent: All dependencies required for notificationfeed package
     * */
    fun notificationFeedComponent(): NotificationFeedComponent? {
        if (notificationFeedComponent == null) {
            notificationFeedComponent = appComponent?.notificationFeedComponent()?.create()
        }
        return notificationFeedComponent
    }
}