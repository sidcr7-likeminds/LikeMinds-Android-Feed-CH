package com.likeminds.feedsampleapp.post.detail.view.adapter.databinder

import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import com.likeminds.feedsampleapp.R
import com.likeminds.feedsampleapp.databinding.ItemPostDetailReplyBinding
import com.likeminds.feedsampleapp.overflowmenu.model.OverflowMenuItemViewData
import com.likeminds.feedsampleapp.post.detail.util.PostDetailUtil
import com.likeminds.feedsampleapp.post.detail.view.adapter.PostDetailAdapter.PostDetailAdapterListener
import com.likeminds.feedsampleapp.post.detail.view.adapter.PostDetailReplyAdapter.PostDetailReplyAdapterListener
import com.likeminds.feedsampleapp.posttypes.model.CommentViewData
import com.likeminds.feedsampleapp.utils.TimeUtil
import com.likeminds.feedsampleapp.utils.ViewUtils
import com.likeminds.feedsampleapp.utils.ViewUtils.hide
import com.likeminds.feedsampleapp.utils.ViewUtils.show
import com.likeminds.feedsampleapp.utils.customview.ViewDataBinder
import com.likeminds.feedsampleapp.utils.model.ITEM_POST_DETAIL_REPLY

class ItemPostDetailReplyViewDataBinder constructor(
    private val postDetailAdapterListener: PostDetailAdapterListener,
    private val postDetailReplyAdapterListener: PostDetailReplyAdapterListener
) : ViewDataBinder<ItemPostDetailReplyBinding, CommentViewData>() {

    override val viewType: Int
        get() = ITEM_POST_DETAIL_REPLY

    override fun createBinder(parent: ViewGroup): ItemPostDetailReplyBinding {
        return ItemPostDetailReplyBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    }

    override fun bindData(
        binding: ItemPostDetailReplyBinding,
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
        binding: ItemPostDetailReplyBinding,
        data: CommentViewData,
        position: Int
    ) {
        binding.apply {
            val context = root.context

            tvCommenterName.text = data.user.name

            PostDetailUtil.initTextContent(
                tvCommentContent,
                data,
                position,
                postDetailAdapterListener,
                data.parentId
            )

            if (data.isLiked) {
                ivLike.setImageResource(R.drawable.ic_like_comment_filled)
            } else {
                ivLike.setImageResource(R.drawable.ic_like_comment_unfilled)
            }

            tvCommentTime.text = TimeUtil.getRelativeTimeInString(data.createdAt)

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

            ivLike.setOnClickListener { view ->
                // bounce animation for like button
                ViewUtils.showBounceAnim(context, view)
                val parentCommentId = data.parentId ?: ""
                postDetailReplyAdapterListener.likeReply(parentCommentId, data.id)
            }

            ivReplyMenu.setOnClickListener { view ->
                showMenu(
                    view,
                    data.postId,
                    data.parentId,
                    data.id,
                    data.userId,
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
        creatorId: String,
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
                creatorId,
                menuItem.itemId
            )
            true
        }
        popup.show()
    }
}