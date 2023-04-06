package com.likeminds.feedsx.branding.util

import android.content.Context
import android.graphics.Typeface
import androidx.core.content.res.ResourcesCompat
import com.likeminds.feedsx.R
import com.likeminds.feedsx.branding.model.LMBranding

object BrandingUtil {

    fun getTypeFace(context: Context, fontStyle: String?): Typeface? {
        val currentFont = LMBranding.getCurrentFonts()

        val typeface = when (fontStyle) {
            "bold" -> {
                if (currentFont != null) {
                    Typeface.createFromAsset(context.assets, currentFont.bold)
                } else {
                    ResourcesCompat.getFont(context, R.font.roboto_bold)
                }
            }
            "medium" -> {
                if (currentFont != null) {
                    Typeface.createFromAsset(context.assets, currentFont.medium)
                } else {
                    ResourcesCompat.getFont(context, R.font.roboto_medium)
                }
            }
            "regular" -> {
                if (currentFont != null) {
                    Typeface.createFromAsset(context.assets, currentFont.regular)
                } else {
                    ResourcesCompat.getFont(context, R.font.roboto_regular)
                }
            }
            else -> {
                ResourcesCompat.getFont(context, R.font.roboto_regular)
            }
        }
        return typeface
    }

    fun getTextColor(defaultColor: Int, textType: String?): Int {
        val color = when (textType) {
            "title" -> {
                LMBranding.getToolbarColor()
            }
            "subtitle" -> {
                LMBranding.getSubtitleColor()
            }
            "special" -> {
                LMBranding.getButtonsColor()
            }
            else -> {
                defaultColor
            }
        }
        return color
    }
}