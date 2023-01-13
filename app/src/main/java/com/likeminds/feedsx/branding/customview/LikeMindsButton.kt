package com.likeminds.feedsx.branding.customview

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.Log
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.button.MaterialButton
import com.likeminds.feedsx.branding.model.BrandingData
import com.likeminds.feedsx.R
import com.likeminds.feedsx.branding.util.BrandingUtil

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
        // fonts
        val array = context.obtainStyledAttributes(attrs, R.styleable.LikeMindsButton)
        this.typeface = BrandingUtil.getTypeFace(context, array, R.styleable.LikeMindsButton_font_type)
        array.recycle()

        // color
        this.backgroundTintList = ColorStateList.valueOf(BrandingData.getButtonsColor())
    }
}