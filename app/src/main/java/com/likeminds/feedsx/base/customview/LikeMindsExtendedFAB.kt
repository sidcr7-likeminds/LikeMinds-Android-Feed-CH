package com.likeminds.feedsx.base.customview

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.likeminds.feedsx.utils.branding.BrandingData
import com.likeminds.feedsx.R

internal class LikeMindsExtendedFAB : ExtendedFloatingActionButton {
    constructor(context: Context) : super(context) {
        initiate(null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initiate(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        initiate(attrs)
    }

    private fun initiate(attrs: AttributeSet?) {

        /**
         * The following code sets the typeface of text on the extended fab button to the provided font.
         * Uses roboto font by default.
         * **/

        val array = context.obtainStyledAttributes(attrs, R.styleable.LikeMindsExtendedFAB)
        val currentFont = BrandingData.getCurrentFonts()
        val typeface = when (array.getString(R.styleable.LikeMindsExtendedFAB_font_Style)) {
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
        this.typeface = typeface
        array.recycle()

        /**
         * The following code sets the color of extended fab button to provided button color.
         * **/

        this.backgroundTintList = ColorStateList.valueOf(BrandingData.getButtonsColor())
    }
}