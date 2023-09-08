package com.likeminds.feedsx.branding.customview

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.Toolbar
import com.likeminds.feedsx.branding.model.LMFeedBranding


class LMFeedToolbar : Toolbar {
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
        // background color
        this.setBackgroundColor(LMFeedBranding.getHeaderColor())

        // toolbar color
        val color = LMFeedBranding.getToolbarColor()
        this.overflowIcon?.setTint(color)
        this.navigationIcon?.setTint(color)
    }
}