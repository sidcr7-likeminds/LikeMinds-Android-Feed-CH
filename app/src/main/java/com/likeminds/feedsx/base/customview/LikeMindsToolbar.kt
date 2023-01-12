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
        // background color
        this.setBackgroundColor(BrandingData.getHeaderColor())

        // icon color
        val color = if (BrandingData.isBrandingBasic) Color.WHITE else Color.BLACK
        this.overflowIcon?.setTint(color)
        this.navigationIcon?.setTint(color)
    }
}