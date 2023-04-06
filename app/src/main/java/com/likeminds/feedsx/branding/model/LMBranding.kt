package com.likeminds.feedsx.branding.model

object LMBranding {

    private var headerColor: String = "#FFFFFF"
    private var buttonColor: String = "#5046E5"
    private var textLinkColor: String = "#007AFF"
    private var fonts: LMFonts? = null

    fun setBranding(setBrandingRequest: SetBrandingRequest) {
        this.headerColor = setBrandingRequest.headerColor
        this.buttonColor = setBrandingRequest.buttonColor
        this.textLinkColor = setBrandingRequest.textLinkColor
        this.fonts = setBrandingRequest.fonts
    }
}