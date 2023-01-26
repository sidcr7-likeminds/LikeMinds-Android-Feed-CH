package com.likeminds.feedsx.branding.customview

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.likeminds.feedsx.R
import com.likeminds.feedsx.branding.util.BrandingUtil

internal class LikeMindsTextView : AppCompatTextView {

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
        val array = context.obtainStyledAttributes(attrs, R.styleable.LikeMindsTextView)
        val fontStyle = array.getString(R.styleable.LikeMindsTextView_fontType)
        this.typeface = BrandingUtil.getTypeFace(context, fontStyle)
        array.recycle()
    }
}