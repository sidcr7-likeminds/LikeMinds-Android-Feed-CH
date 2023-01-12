package com.likeminds.feedsx.base.customview

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.children
import com.likeminds.feedsx.utils.branding.BrandingData


class LikeMindsToolbar : Toolbar {
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

        /**
         * The following code sets the background color of toolbar.
         * **/

        var bgColor = if(BrandingData.isBrandingBasic) Color.WHITE else BrandingData.currentAdvanced!!.first
        this.setBackgroundColor(bgColor)

        /**
         * The following code sets the color of navigation icon and overflow menu icon on toolbar.
         * **/

        var color = if (BrandingData.isBrandingBasic) Color.WHITE else Color.BLACK
        this.overflowIcon?.setTint(color)
        this.navigationIcon?.setTint(color)
    }
}