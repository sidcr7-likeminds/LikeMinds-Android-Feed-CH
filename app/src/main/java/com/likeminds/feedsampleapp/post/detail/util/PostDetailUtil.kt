package com.likeminds.feedsampleapp.post.detail.util

import android.text.*
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.util.Linkify
import android.view.View
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.text.util.LinkifyCompat
import com.likeminds.feedsampleapp.R
import com.likeminds.feedsampleapp.branding.model.LMBranding
import com.likeminds.feedsampleapp.post.detail.view.adapter.PostDetailAdapter
import com.likeminds.feedsampleapp.posttypes.model.CommentViewData
import com.likeminds.feedsampleapp.utils.Route
import com.likeminds.feedsampleapp.utils.SeeMoreUtil
import com.likeminds.feedsampleapp.utils.ValueUtils.getValidTextForLinkify
import com.likeminds.feedsampleapp.utils.ViewUtils.hide
import com.likeminds.feedsampleapp.utils.ViewUtils.show
import com.likeminds.feedsampleapp.utils.link.CustomLinkMovementMethod
import com.likeminds.feedsampleapp.utils.membertagging.util.MemberTaggingDecoder

object PostDetailUtil {
    // initialized text content in comment with level-0,1
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

        // span for seeMore feature
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
            // decodes tags in text and creates span around those tags
            MemberTaggingDecoder.decode(
                tvCommentContent,
                textForLinkify,
                enableClick = true,
                LMBranding.getTextLinkColor()
            ) { tag ->
                onMemberTagClicked()
            }

            // gets short text to set with seeMore
            val shortText: String? = SeeMoreUtil.getShortContent(
                tvCommentContent,
                3,
                500
            )

            val trimmedText =
                if (!alreadySeenFullContent && !shortText.isNullOrEmpty()) {
                    tvCommentContent.editableText.subSequence(0, shortText.length)
                } else {
                    tvCommentContent.editableText
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

            // appends see more text at last
            tvCommentContent.text = TextUtils.concat(
                trimmedText,
                seeMoreSpannableStringBuilder
            )

            LinkifyCompat.addLinks(tvCommentContent, Linkify.WEB_URLS)
            tvCommentContent.movementMethod = CustomLinkMovementMethod { url ->
                tvCommentContent.setOnClickListener {
                    null
                }
                // creates a route and returns an intent to handle the link
                val intent = Route.handleDeepLink(context, url)
                if (intent != null) {
                    try {
                        // starts activity with the intent
                        ActivityCompat.startActivity(context, intent, null)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                true
            }
        }
    }

    // performs action when member tag is clicked
    private fun onMemberTagClicked() {
        // TODO: Change Implementation
    }
}