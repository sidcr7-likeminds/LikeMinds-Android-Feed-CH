package com.likeminds.feedsx.post.detail.view.adapter.databinder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.likeminds.feedsx.R
import com.likeminds.feedsx.databinding.ItemPostDetailCommentBinding
import com.likeminds.feedsx.overflowmenu.model.OverflowMenuItemViewData
import com.likeminds.feedsx.overflowmenu.view.OverflowMenuPopup
import com.likeminds.feedsx.overflowmenu.view.adapter.OverflowMenuAdapterListener
import com.likeminds.feedsx.post.detail.model.ViewMoreReplyViewData
import com.likeminds.feedsx.post.detail.view.PostDetailFragment
import com.likeminds.feedsx.post.detail.view.adapter.PostDetailAdapter.PostDetailAdapterListener
import com.likeminds.feedsx.post.detail.view.adapter.PostDetailReplyAdapter
import com.likeminds.feedsx.posttypes.model.CommentViewData
import com.likeminds.feedsx.posttypes.util.PostTypeUtil
import com.likeminds.feedsx.utils.TimeUtil
import com.likeminds.feedsx.utils.ViewUtils.hide
import com.likeminds.feedsx.utils.ViewUtils.show
import com.likeminds.feedsx.utils.customview.ViewDataBinder
import com.likeminds.feedsx.utils.model.ITEM_POST_DETAIL_COMMENT

class ItemPostDetailCommentViewDataBinder constructor(
    val postDetailAdapterListener: PostDetailAdapterListener,
    val postDetailReplyAdapterListener: PostDetailReplyAdapter.PostDetailReplyAdapterListener
) : ViewDataBinder<ItemPostDetailCommentBinding, CommentViewData>(),
    OverflowMenuAdapterListener {

    private lateinit var mRepliesAdapter: PostDetailReplyAdapter

    private lateinit var overflowMenu: OverflowMenuPopup

    override val viewType: Int
        get() = ITEM_POST_DETAIL_COMMENT

    override fun createBinder(parent: ViewGroup): ItemPostDetailCommentBinding {
        overflowMenu = OverflowMenuPopup.create(parent.context, this)
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

        PostTypeUtil.setOverflowMenuItems(
            overflowMenu,
            data.menuItems
        )

        binding.apply {
            val context = root.context

            tvCommenterName.text = data.user.name
            tvCommentContent.text = data.text

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

            tvCommentTime.text = TimeUtil.getDaysHoursOrMinutes(data.createdAt)

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
                PostTypeUtil.showOverflowMenu(
                    ivCommentMenu,
                    overflowMenu
                )
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

    override fun onMenuItemClicked(menu: OverflowMenuItemViewData) {
        overflowMenu.dismiss()
        postDetailAdapterListener.onCommentMenuItemClicked(
            menu.postId ?: "",
            menu.entityId,
            menu.title,
            menu.entityCreatorId
        )
    }
}