package com.likeminds.feedsx.base.customview

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.likeminds.feedsx.utils.branding.BrandingData

class LikeMindsFab : FloatingActionButton {
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
         * The following code sets the color of fab button to provided button color.
         * **/

        this.backgroundTintList = ColorStateList.valueOf(BrandingData.getButtonsColor())
    }

}