package com.likeminds.feedsx.base.customview

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.button.MaterialButton
import com.likeminds.feedsx.utils.branding.BrandingData
import com.likeminds.feedsx.R

internal class LikeMindsButton : MaterialButton {
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
        val array = context.obtainStyledAttributes(attrs, R.styleable.LikeMindsButton)
        val currentFont = BrandingData.getCurrentFonts()
        val typeface = when (array.getString(R.styleable.LikeMindsButton_font_type)) {
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
        this.backgroundTintList = ColorStateList.valueOf(BrandingData.getButtonsColor())
        this.typeface = typeface
        array.recycle()
    }
}