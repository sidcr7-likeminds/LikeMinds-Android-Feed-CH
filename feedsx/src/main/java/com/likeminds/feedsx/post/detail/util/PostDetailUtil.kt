package com.likeminds.feedsx.post.detail.util

import android.text.*
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.util.Linkify
import android.view.View
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.text.util.LinkifyCompat
import com.likeminds.feedsx.R
import com.likeminds.feedsx.branding.model.LMFeedBranding
import com.likeminds.feedsx.post.detail.view.adapter.PostDetailAdapter
import com.likeminds.feedsx.posttypes.model.CommentViewData
import com.likeminds.feedsx.utils.Route
import com.likeminds.feedsx.utils.SeeMoreUtil
import com.likeminds.feedsx.utils.ValueUtils.getUrlIfExist
import com.likeminds.feedsx.utils.ValueUtils.getValidTextForLinkify
import com.likeminds.feedsx.utils.ViewUtils.hide
import com.likeminds.feedsx.utils.ViewUtils.show
import com.likeminds.feedsx.utils.link.CustomLinkMovementMethod
import com.likeminds.feedsx.utils.membertagging.util.MemberTaggingDecoder

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
                LMFeedBranding.getTextLinkColor()
            ) {
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
                    return@setOnClickListener
                }
                // creates a route and returns an intent to handle the link
                val intent = Route.createWebsiteIntent(context, url)
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
        // open user profile on tag click
    }
}