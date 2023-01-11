package com.likeminds.feedsx.utils.branding

import android.graphics.Color
import android.util.Log
import com.likeminds.feedsx.Fonts

internal object BrandingData {

    const val COLOR_HEADER = "color_header"
    const val COLOR_BUTTON_ICONS = "color_buttons_icons"
    const val COLOR_TEXT_LINKS = "color_text_links"

    val defaultColor by lazy {
        Color.parseColor("#00897B")
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

        if (!branding?.basic?.primaryColor.isNullOrEmpty()) {
            currentPrimary = parseOrGetDefault(branding?.basic?.primaryColor)
            isBrandingBasic = true
        } else if (!branding?.advanced?.headerColor.isNullOrEmpty() &&
            !branding?.advanced?.buttonsIconsColor.isNullOrEmpty() &&
            !branding?.advanced?.textLinksColor.isNullOrEmpty()
        ) {
            currentAdvanced = Triple(
                parseOrGetDefault(branding?.advanced?.headerColor),
                parseOrGetDefault(branding?.advanced?.buttonsIconsColor),
                parseOrGetDefault(branding?.advanced?.textLinksColor),
            )
            Log.d("TAG", "fetchBranding: " + currentAdvanced.toString())
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

    fun getButtonsColor(): Int {
        return if (currentPrimary != null)
            currentPrimary!!
        else if (currentAdvanced?.second != null)
            currentAdvanced?.second!!
        else
            defaultColor
    }

    fun getCurrentFonts(): Fonts? {
        return currentFont
    }

    fun isHeaderColorWhite(): Boolean {
        return !isBrandingBasic && (currentAdvanced?.first == Color.WHITE)
    }
}