package com.likeminds.feedsx.utils

import android.widget.TextView

internal object SeeMoreUtil {

    /**
     * This function is for getting short post content for the see more feature
     */
    fun getShortContent(
        textView: TextView,
        maxLines: Int,
        seeMoreCountLimit: Int
    ): String? {
        val textContent = textView.editableText ?: return null

        // variable to hold text limit as per max number of lines
        var shortTextLine: String? = null

        //variable to hold text limit as per max character limit
        val shortLimitText: String? =
            if (textContent.length > seeMoreCountLimit) textContent.substring(
                0,
                seeMoreCountLimit
            ) else null

        //calculation of text limit as per max number of lines
        if (textView.lineCount >= maxLines) {
            val lineEndIndex: Int = textView.layout.getLineEnd(maxLines - 1)
            shortTextLine = textView.text.subSequence(0, lineEndIndex).toString()
        }

        // returns null or minimum of shortTextLine & shortLimitText
        if (shortTextLine != null && shortTextLine.length != textContent.length) {
            if (shortLimitText != null && shortLimitText.length < shortTextLine.length) {
                return shortLimitText
            }
            return shortTextLine
        }
        return shortLimitText
    }
}