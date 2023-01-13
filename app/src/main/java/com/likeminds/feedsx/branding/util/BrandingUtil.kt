package com.likeminds.feedsx.branding.util

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.core.content.res.ResourcesCompat
import com.likeminds.feedsx.R
import com.likeminds.feedsx.branding.model.BrandingData

object BrandingUtil {

    fun getTypeFace(context: Context, array: TypedArray, fontType: Int): Typeface? {
        val currentFont = BrandingData.getCurrentFonts()

        val typeface = when (array.getString(fontType)) {
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

}