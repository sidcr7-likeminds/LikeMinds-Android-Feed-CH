package com.likeminds.feedsx.utils.membertagging.util

import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View
import com.likeminds.feedsx.utils.membertagging.MemberTaggingDecoder

class MemberTaggingClickableSpan(
    val color: Int,
    val regex: String,
    val underLineText: Boolean = false,
    val memberTaggingClickableSpanListener: MemberTaggingClickableSpanListener? = null
) : ClickableSpan() {

    override fun updateDrawState(textPaint: TextPaint) {
        super.updateDrawState(textPaint)
        try {
            textPaint.color = color
            textPaint.isUnderlineText = underLineText
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onClick(widget: View) {
        memberTaggingClickableSpanListener?.onClick(regex)
    }

    fun getMemberId(): String? {
        return MemberTaggingDecoder.getMemberIdFromRegex(regex)
    }
}

fun interface MemberTaggingClickableSpanListener {

    fun onClick(regex: String)

}