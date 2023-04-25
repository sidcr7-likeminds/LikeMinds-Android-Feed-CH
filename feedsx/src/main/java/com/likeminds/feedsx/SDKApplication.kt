package com.likeminds.feedsx

import android.app.Application
import android.content.Context
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobile.client.Callback
import com.amazonaws.mobile.client.UserStateDetails
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.likeminds.feedsx.branding.model.LMBranding
import com.likeminds.feedsx.branding.model.SetBrandingRequest
import com.likeminds.feedsx.post.PostWithAttachmentsRepository
import javax.inject.Inject

class SDKApplication {

    @Inject
    lateinit var transferUtility: TransferUtility

    @Inject
    lateinit var postWithAttachmentsRepository: PostWithAttachmentsRepository

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
}