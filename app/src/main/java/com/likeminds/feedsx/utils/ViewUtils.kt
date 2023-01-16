package com.likeminds.feedsx.utils

import android.content.res.Resources

object ViewUtils {
    fun dpToPx(dp: Int): Int {
        return (dp * Resources.getSystem().displayMetrics.density).toInt()
    }
}