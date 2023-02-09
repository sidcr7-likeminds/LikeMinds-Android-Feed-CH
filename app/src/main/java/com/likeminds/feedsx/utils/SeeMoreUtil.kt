package com.likeminds.feedsx.utils

import android.widget.TextView

internal object SeeMoreUtil {

    /**
     * This function is for getting short post content for the see more feature
     */
    fun getShortContent(
        textContent: String?,
        textView: TextView,
        maxLines: Int,
        seeMoreCountLimit: Int
    ): String? {
        if (textContent == null)
            return null

        var shortTextLine: String? = null
        val shortLimitText: String? =
            if (textContent.length > seeMoreCountLimit) textContent.substring(
                0,
                seeMoreCountLimit
            ) else null

        textView.text = textContent
        if (textView.lineCount >= maxLines) {
            val lineEndIndex: Int = textView.layout.getLineEnd(maxLines - 1)
            shortTextLine = textView.text.subSequence(0, lineEndIndex).toString()
        }
        if (shortTextLine != null && shortTextLine.length != textContent.length) {
            if (shortLimitText != null && shortLimitText.length < shortTextLine.length) {
                return shortLimitText
            }
            return shortTextLine
        }
        return shortLimitText
    }
}