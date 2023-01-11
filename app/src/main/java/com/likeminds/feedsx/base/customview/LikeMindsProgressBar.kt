package com.likeminds.feedsx.base.customview

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import android.util.Log
import android.widget.ProgressBar
import com.likeminds.feedsx.utils.branding.BrandingData

class LikeMindsProgressBar : ProgressBar {
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
        Log.d("TAG", "initiate1141: " + BrandingData.getButtonsColor())
        this.progressTintList = ColorStateList.valueOf(BrandingData.getButtonsColor())
    }
}