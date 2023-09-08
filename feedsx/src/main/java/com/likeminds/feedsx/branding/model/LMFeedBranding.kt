package com.likeminds.feedsx.branding.model

import android.graphics.Color

// responsible for all the branding-related things like colors and fonts
object LMFeedBranding {
    private const val defaultHeaderColor = "#FFFFFF"
    private var headerColor: String = defaultHeaderColor
    private var buttonsColor: String = "#5046E5"
    private var textLinkColor: String = "#007AFF"
    private var fonts: LMFeedFonts? = null

    /**
     * @param setFeedBrandingRequest - Request to set branding with colors and fonts
     * sets headerColor, buttonsColor, textLinkColor and fonts, used throughout the app
     * */
    fun setBranding(setFeedBrandingRequest: SetFeedBrandingRequest) {
        headerColor = setFeedBrandingRequest.headerColor
        buttonsColor = setFeedBrandingRequest.buttonsColor
        textLinkColor = setFeedBrandingRequest.textLinkColor
        fonts = setFeedBrandingRequest.fonts
    }

    // returns button color
    fun getButtonsColor(): Int {
        return Color.parseColor(buttonsColor)
    }

    // returns header color
    fun getHeaderColor(): Int {
        return Color.parseColor(headerColor)
    }

    // returns text link color
    fun getTextLinkColor(): Int {
        return Color.parseColor(textLinkColor)
    }

    // returns toolbar color
    fun getToolbarColor(): Int {
        return if (headerColor == defaultHeaderColor) {
            Color.BLACK
        } else {
            Color.WHITE
        }
    }

    // returns color of subtitle text
    fun getSubtitleColor(): Int {
        return if (headerColor == defaultHeaderColor) {
            Color.GRAY
        } else {
            Color.WHITE
        }
    }

    // returns paths of the current fonts
    fun getCurrentFonts(): LMFeedFonts? {
        return fonts
    }
}