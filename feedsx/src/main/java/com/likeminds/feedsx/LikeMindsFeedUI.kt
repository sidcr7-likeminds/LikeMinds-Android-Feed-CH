package com.likeminds.feedsx

import android.app.Application
import android.util.Log
import com.likeminds.feedsx.branding.model.SetBrandingRequest

object LikeMindsFeedUI {

    /**
     * Call this function to configure SDK in client's app
     *
     * @param application: application instance of client's app
     * @param brandingRequest: branding request from client
     **/
    fun initLikeMindsFeedUI(
        application: Application,
        lmUICallback: LMUICallback,
        brandingRequest: SetBrandingRequest
    ) {
        Log.d(SDKApplication.LOG_TAG, "initiate LikeMindsFeedUI called")

        //create object of SDKApplication
        val sdk = SDKApplication.getInstance()

        //call initSDKApplication to initialise sdk
        sdk.initSDKApplication(
            application,
            lmUICallback,
            brandingRequest
        )
    }

    fun setBranding(brandingRequest: SetBrandingRequest) {
        val sdk = SDKApplication.getInstance()
        sdk.setupBranding(brandingRequest)
    }
}