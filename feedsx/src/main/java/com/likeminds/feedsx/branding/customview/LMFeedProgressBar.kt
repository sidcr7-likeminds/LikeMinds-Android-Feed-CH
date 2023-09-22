package com.likeminds.feedsx.branding.customview

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.widget.ProgressBar
import com.likeminds.feedsx.branding.model.LMFeedBranding

class LMFeedProgressBar : ProgressBar {
    constructor(context: Context) : super(context) {
        initiate()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initiate()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        initiate()
    }

    private fun initiate() {
        // color
        this.progressTintList = ColorStateList.valueOf(LMFeedBranding.getButtonsColor())
        this.indeterminateTintList = ColorStateList.valueOf(LMFeedBranding.getButtonsColor())
    }
}