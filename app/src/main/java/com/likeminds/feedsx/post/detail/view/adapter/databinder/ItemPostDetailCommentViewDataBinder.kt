package com.likeminds.feedsx.post.detail.view.adapter.databinder

import android.text.*
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.util.Linkify
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.text.util.LinkifyCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.likeminds.feedsx.R
import com.likeminds.feedsx.branding.model.BrandingData
import com.likeminds.feedsx.databinding.ItemPostDetailCommentBinding
import com.likeminds.feedsx.post.detail.model.ViewMoreReplyViewData
import com.likeminds.feedsx.post.detail.view.PostDetailFragment
import com.likeminds.feedsx.post.detail.view.adapter.PostDetailAdapter.PostDetailAdapterListener
import com.likeminds.feedsx.post.detail.view.adapter.PostDetailReplyAdapter
import com.likeminds.feedsx.posttypes.model.CommentViewData
import com.likeminds.feedsx.utils.SeeMoreUtil
import com.likeminds.feedsx.utils.TimeUtil
import com.likeminds.feedsx.utils.ValueUtils.getValidTextForLinkify
import com.likeminds.feedsx.utils.ViewUtils.hide
import com.likeminds.feedsx.utils.ViewUtils.show
import com.likeminds.feedsx.utils.customview.ViewDataBinder
import com.likeminds.feedsx.utils.link.CustomLinkMovementMethod
import com.likeminds.feedsx.utils.membertagging.util.MemberTaggingDecoder
import com.likeminds.feedsx.utils.model.ITEM_POST_DETAIL_COMMENT

class ItemPostDetailCommentViewDataBinder constructor(
    private val postDetailAdapterListener: PostDetailAdapterListener,
    private val postDetailReplyAdapterListener: PostDetailReplyAdapter.PostDetailReplyAdapterListener
) : ViewDataBinder<ItemPostDetailCommentBinding, CommentViewData>() {

    private lateinit var mRepliesAdapter: PostDetailReplyAdapter
    override val viewType: Int
        get() = ITEM_POST_DETAIL_COMMENT

    override fun createBinder(parent: ViewGroup): ItemPostDetailCommentBinding {
        return ItemPostDetailCommentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    }

    override fun bindData(
        binding: ItemPostDetailCommentBinding,
        data: CommentViewData,
        position: Int
    ) {
        initCommentsView(
            binding,
            data,
            position
        )
    }

    // sets the data to comments item and handles the replies click and rv
    private fun initCommentsView(
        binding: ItemPostDetailCommentBinding,
        data: CommentViewData,
        position: Int
    ) {
        //todo
//        PostTypeUtil.setOverflowMenuItems(
//            overflowMenu,
//            data.menuItems
//        )

        binding.apply {
            val context = root.context

            tvCommenterName.text = data.user.name

            initTextContent(
                tvCommentContent,
                data,
                position
            )

            if (data.isLiked) {
                ivLike.setImageResource(R.drawable.ic_like_comment_filled)
            } else {
                ivLike.setImageResource(R.drawable.ic_like_comment_unfilled)
            }

            if (data.likesCount == 0) {
                likesCount.hide()
            } else {
                likesCount.text =
                    context.resources.getQuantityString(
                        R.plurals.likes,
                        data.likesCount,
                        data.likesCount
                    )
                likesCount.show()
            }

            likesCount.setOnClickListener {
                postDetailAdapterListener.showLikesScreen(
                    data.postId,
                    data.id
                )
            }

            tvCommentTime.text = TimeUtil.getRelativeTimeInString(data.createdAt)

            if (data.repliesCount == 0) {
                groupReplies.hide()
            } else {
                groupReplies.show()
                tvReplyCount.text = context.resources.getQuantityString(
                    R.plurals.replies,
                    data.repliesCount,
                    data.repliesCount
                )
            }

            mRepliesAdapter = PostDetailReplyAdapter(
                postDetailAdapterListener,
                postDetailReplyAdapterListener
            )

            rvReplies.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = mRepliesAdapter
            }

            tvReply.setOnClickListener {
                postDetailAdapterListener.replyOnComment(
                    data.id,
                    position,
                    data.user
                )
            }

            ivLike.setOnClickListener {
                postDetailAdapterListener.likeComment(data.id)
            }

            tvReplyCount.setOnClickListener {
                postDetailAdapterListener.fetchReplies(data.id, position)
            }

            ivCommentMenu.setOnClickListener {
                //todo
//                PostTypeUtil.showOverflowMenu(
//                    ivCommentMenu,
//                    overflowMenu
//                )
            }

            if (data.replies.isNotEmpty()) {
                rvReplies.show()
                mRepliesAdapter.replace(data.replies.toList())
                commentSeparator.hide()
                replyCommentSeparator.show()
                tvReplyCount.isClickable = false
                handleViewMore(data, position)
            } else {
                rvReplies.hide()
                commentSeparator.show()
                replyCommentSeparator.hide()
                tvReplyCount.isClickable = true
            }
        }
    }

    private fun initTextContent(
        tvCommentContent: TextView,
        data: CommentViewData,
        itemPosition: Int,
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
                postDetailAdapterListener.updateCommentSeenFullContent(itemPosition, true)
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

    // adds ViewMoreReply view when required
    private fun handleViewMore(data: CommentViewData, position: Int) {
        if (data.repliesCount > PostDetailFragment.REPLIES_THRESHOLD && data.replies.size < data.repliesCount) {
            mRepliesAdapter.add(
                ViewMoreReplyViewData.Builder()
                    .totalCommentsCount(data.repliesCount)
                    .currentCount(data.replies.size)
                    .parentCommentId(data.id)
                    .parentCommentPosition(position)
                    .build()
            )
        }
    }

    // performs action when member tag is clicked
    private fun onMemberTagClicked() {
        // TODO: Change Implementation
    }
}