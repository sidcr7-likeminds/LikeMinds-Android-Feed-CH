package com.likeminds.feedsx.branding.customview

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.likeminds.feedsx.branding.model.LMFeedBranding

class LMFeedFAB : FloatingActionButton {
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
        this.backgroundTintList = ColorStateList.valueOf(LMFeedBranding.getButtonsColor())
    }

}