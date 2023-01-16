package com.likeminds.feedsx.branding.model

import android.graphics.Color

internal object BrandingData {

    private val defaultColor by lazy {
        Color.parseColor("#00897B")
    }

    private val defaultHeaderColor by lazy {
        Color.WHITE
    }

    @JvmField
    var currentPrimary: Int? = null

    @JvmField
    var currentAdvanced: Triple<Int, Int, Int>? = null

    @JvmField
    var currentFont: Fonts? = null

    //is true -> Branding is basic |  is false -> Branding is advanced
    @JvmField
    var isBrandingBasic: Boolean = false

    /**
     * Only call if it was not fetched or invalidate is called
     */
    private fun fetchBranding(colors: List<String>) {
//        val branding = LikeMindsDB.getCommunityBranding(communityId)

        val branding = Branding(
            BrandingBasic(colors[0]),
            BrandingAdvanced(
                colors[1],
                colors[2],
                colors[3]
            )
        )

        val basic = branding?.basic
        val advanced = branding?.advanced

        if (!basic?.primaryColor.isNullOrEmpty()) {
            currentPrimary = parseOrGetDefault(basic?.primaryColor)
            isBrandingBasic = true
        } else if (!advanced?.headerColor.isNullOrEmpty() &&
            !advanced?.buttonsIconsColor.isNullOrEmpty() &&
            !advanced?.textLinksColor.isNullOrEmpty()
        ) {
            currentAdvanced = Triple(
                parseOrGetDefault(advanced?.headerColor),
                parseOrGetDefault(advanced?.buttonsIconsColor),
                parseOrGetDefault(advanced?.textLinksColor),
            )
            isBrandingBasic = false
        } else {
            currentPrimary = parseOrGetDefault()
        }
    }

    //invalidate colors
    fun invalidateColors(colors: List<String>) {
        currentPrimary = null
        currentAdvanced = null
//        if (!communityId.isNullOrEmpty()) {
        fetchBranding(colors)
//        }
    }

    //invalidate fonts
    fun invalidateFonts(fonts: Fonts?) {
        currentFont = fonts
    }

    private fun parseOrGetDefault(color: String? = null): Int {
        if (color == null)
            return defaultColor
        return try {
            Color.parseColor(color)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            defaultColor
        }
    }

    // returns header color
    fun getHeaderColor(): Int {
        return if (currentPrimary != null)
            defaultHeaderColor
        else if (currentAdvanced?.first != null)
            currentAdvanced?.first!!
        else
            defaultHeaderColor
    }

    // returns button color
    fun getButtonsColor(): Int {
        return if (currentPrimary != null)
            currentPrimary!!
        else if (currentAdvanced?.second != null)
            currentAdvanced?.second!!
        else
            defaultColor
    }

    // returns paths of the current font
    fun getCurrentFonts(): Fonts? {
        return currentFont
    }
}