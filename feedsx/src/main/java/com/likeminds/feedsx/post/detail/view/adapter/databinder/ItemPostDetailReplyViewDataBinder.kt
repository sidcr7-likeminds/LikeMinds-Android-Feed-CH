package com.likeminds.feedsx.post.detail.view.adapter.databinder

import android.view.*
import android.widget.PopupMenu
import com.likeminds.feedsx.*
import com.likeminds.feedsx.databinding.LmFeedItemPostDetailReplyBinding
import com.likeminds.feedsx.overflowmenu.model.OverflowMenuItemViewData
import com.likeminds.feedsx.post.detail.util.PostDetailUtil
import com.likeminds.feedsx.post.detail.view.adapter.PostDetailAdapter.PostDetailAdapterListener
import com.likeminds.feedsx.post.detail.view.adapter.PostDetailReplyAdapter.PostDetailReplyAdapterListener
import com.likeminds.feedsx.posttypes.model.CommentViewData
import com.likeminds.feedsx.utils.TimeUtil
import com.likeminds.feedsx.utils.ViewUtils
import com.likeminds.feedsx.utils.ViewUtils.hide
import com.likeminds.feedsx.utils.ViewUtils.show
import com.likeminds.feedsx.utils.customview.ViewDataBinder
import com.likeminds.feedsx.utils.model.ITEM_POST_DETAIL_REPLY

class ItemPostDetailReplyViewDataBinder constructor(
    private val postDetailAdapterListener: PostDetailAdapterListener,
    private val postDetailReplyAdapterListener: PostDetailReplyAdapterListener
) : ViewDataBinder<LmFeedItemPostDetailReplyBinding, CommentViewData>() {

    override val viewType: Int
        get() = ITEM_POST_DETAIL_REPLY

    override fun createBinder(parent: ViewGroup): LmFeedItemPostDetailReplyBinding {
        return LmFeedItemPostDetailReplyBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    }

    override fun bindData(
        binding: LmFeedItemPostDetailReplyBinding,
        data: CommentViewData,
        position: Int
    ) {
        initReplyView(
            binding,
            data,
            position
        )
    }

    private fun initReplyView(
        binding: LmFeedItemPostDetailReplyBinding,
        data: CommentViewData,
        position: Int
    ) {
        binding.apply {
            val context = root.context

            tvCommenterName.text = data.user.name

            tvCommenterName.setOnClickListener {
                LikeMindsFeedUI.lmFeedListener.openProfile(
                    data.user.sdkClientInfoViewData.uuid,
                    data.id,
                    LMFeedAnalytics.Source.FEED
                )
            }

            PostDetailUtil.initTextContent(
                tvCommentContent,
                data,
                position,
                postDetailAdapterListener,
                data.parentId
            )

            if (data.isEdited) {
                viewDotEdited.show()
                tvEdited.show()
            } else {
                viewDotEdited.hide()
                tvEdited.hide()
            }

            if (data.isLiked) {
                ivLike.setImageResource(R.drawable.ic_like_comment_filled)
            } else {
                ivLike.setImageResource(R.drawable.ic_like_comment_unfilled)
            }

            tvCommentTime.text = TimeUtil.getRelativeTimeInString(data.createdAt)

            if (data.isEdited) {
                viewDotEdited.show()
                tvEdited.show()
            } else {
                viewDotEdited.hide()
                tvEdited.hide()
            }

            if (data.likesCount == 0) {
                likesCount.isEnabled = false
                likesCount.hide()
            } else {
                likesCount.isEnabled = true
                likesCount.text =
                    context.resources.getQuantityString(
                        R.plurals.likes,
                        data.likesCount,
                        data.likesCount
                    )
                likesCount.show()

                likesCount.setOnClickListener {
                    postDetailAdapterListener.showLikesScreen(
                        data.postId,
                        data.id
                    )
                }
            }

            ivLike.setOnClickListener { view ->
                // bounce animation for like button
                ViewUtils.showBounceAnim(context, view)
                val parentCommentId = data.parentId ?: ""
                postDetailReplyAdapterListener.likeReply(parentCommentId, data.id)
            }

            val replyCreatorUUID = data.user.sdkClientInfoViewData.uuid
            ivReplyMenu.setOnClickListener { view ->
                showMenu(
                    view,
                    data.postId,
                    data.parentId,
                    data.id,
                    replyCreatorUUID,
                    data.menuItems
                )
            }
        }
    }

    //to show overflow menu for reply
    private fun showMenu(
        view: View,
        postId: String,
        parentCommentId: String?,
        commentId: String,
        replyCreatorUUID: String,
        menuItems: List<OverflowMenuItemViewData>
    ) {
        val popup = PopupMenu(view.context, view)
        menuItems.forEach { menuItem ->
            popup.menu.add(
                Menu.NONE,
                menuItem.id,
                Menu.NONE,
                menuItem.title
            )
        }

        val updatedParentId = parentCommentId ?: ""
        popup.setOnMenuItemClickListener { menuItem ->
            postDetailReplyAdapterListener.onReplyMenuItemClicked(
                postId,
                updatedParentId,
                commentId,
                replyCreatorUUID,
                menuItem.itemId
            )
            true
        }
        popup.show()
    }
}