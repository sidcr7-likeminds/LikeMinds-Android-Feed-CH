package com.likeminds.feedsx.post.detail.util

import android.text.*
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.util.Linkify
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.text.util.LinkifyCompat
import com.likeminds.feedsx.R
import com.likeminds.feedsx.branding.model.BrandingData
import com.likeminds.feedsx.post.detail.view.adapter.PostDetailAdapter
import com.likeminds.feedsx.posttypes.model.CommentViewData
import com.likeminds.feedsx.utils.SeeMoreUtil
import com.likeminds.feedsx.utils.ValueUtils.getValidTextForLinkify
import com.likeminds.feedsx.utils.ViewUtils.hide
import com.likeminds.feedsx.utils.ViewUtils.show
import com.likeminds.feedsx.utils.link.CustomLinkMovementMethod
import com.likeminds.feedsx.utils.membertagging.util.MemberTaggingDecoder

object PostDetailUtil {
    fun initTextContent(
        tvCommentContent: TextView,
        data: CommentViewData,
        itemPosition: Int,
        postDetailAdapterListener: PostDetailAdapter.PostDetailAdapterListener,
        parentCommentId: String? = null
    ) {
        val context = tvCommentContent.context

        /**
         * Text is modified as Linkify doesn't accept texts with these specific unicode characters
         * @see #Linkify.containsUnsupportedCharacters(String)
         */
        val textForLinkify = data.text.getValidTextForLinkify()

        var alreadySeenFullContent = data.alreadySeenFullContent == true

        if (textForLinkify.isEmpty()) {
            tvCommentContent.hide()
            return
        } else {
            tvCommentContent.show()
        }

        val seeMoreColor = ContextCompat.getColor(context, R.color.brown_grey)
        val seeMore = SpannableStringBuilder(context.getString(R.string.see_more))
        seeMore.setSpan(
            ForegroundColorSpan(seeMoreColor),
            0,
            seeMore.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        val seeMoreClickableSpan = object : ClickableSpan() {
            override fun onClick(view: View) {
                alreadySeenFullContent = true
                postDetailAdapterListener.updateCommentSeenFullContent(
                    itemPosition,
                    true,
                    parentCommentId
                )
            }

            override fun updateDrawState(textPaint: TextPaint) {
                textPaint.isUnderlineText = false
            }
        }

        // post is used here to get lines count in the text view
        tvCommentContent.post {
            val shortText: String? = SeeMoreUtil.getShortContent(
                data.text,
                tvCommentContent,
                3,
                500
            )

            val trimmedText =
                if (!alreadySeenFullContent && !shortText.isNullOrEmpty()) {
                    shortText
                } else {
                    textForLinkify
                }

            // TODO: remove branding
            MemberTaggingDecoder.decode(
                tvCommentContent,
                trimmedText,
                enableClick = true,
                BrandingData.currentAdvanced?.third ?: ContextCompat.getColor(
                    context,
                    R.color.pure_blue
                )
            ) { tag ->
                onMemberTagClicked()
            }

            val seeMoreSpannableStringBuilder = SpannableStringBuilder()
            if (!alreadySeenFullContent && !shortText.isNullOrEmpty()) {
                seeMoreSpannableStringBuilder.append("...")
                seeMoreSpannableStringBuilder.append(seeMore)
                seeMoreSpannableStringBuilder.setSpan(
                    seeMoreClickableSpan,
                    3,
                    seeMore.length + 3,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }

            LinkifyCompat.addLinks(tvCommentContent, Linkify.WEB_URLS)
            tvCommentContent.movementMethod = CustomLinkMovementMethod {
                //TODO: Handle links etc.
                true
            }

            tvCommentContent.text = TextUtils.concat(
                tvCommentContent.text,
                seeMoreSpannableStringBuilder
            )
        }
    }

    // performs action when member tag is clicked
    private fun onMemberTagClicked() {
        // TODO: Change Implementation
    }
}