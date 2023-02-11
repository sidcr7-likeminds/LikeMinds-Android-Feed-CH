package com.likeminds.feedsx.post.detail.view.adapter.databinder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.feedsx.R
import com.likeminds.feedsx.databinding.ItemPostDetailReplyBinding
import com.likeminds.feedsx.overflowmenu.model.OverflowMenuItemViewData
import com.likeminds.feedsx.overflowmenu.view.OverflowMenuPopup
import com.likeminds.feedsx.overflowmenu.view.adapter.OverflowMenuAdapterListener
import com.likeminds.feedsx.post.detail.view.adapter.PostDetailAdapter.PostDetailAdapterListener
import com.likeminds.feedsx.post.detail.view.adapter.PostDetailReplyAdapter.PostDetailReplyAdapterListener
import com.likeminds.feedsx.posttypes.model.CommentViewData
import com.likeminds.feedsx.posttypes.util.PostTypeUtil
import com.likeminds.feedsx.utils.TimeUtil
import com.likeminds.feedsx.utils.ViewUtils.hide
import com.likeminds.feedsx.utils.ViewUtils.show
import com.likeminds.feedsx.utils.customview.ViewDataBinder
import com.likeminds.feedsx.utils.model.ITEM_POST_DETAIL_REPLY

class ItemPostDetailReplyViewDataBinder constructor(
    val postDetailAdapterListener: PostDetailAdapterListener,
    val postDetailReplyAdapterListener: PostDetailReplyAdapterListener
) : ViewDataBinder<ItemPostDetailReplyBinding, CommentViewData>(),
    OverflowMenuAdapterListener {

    override val viewType: Int
        get() = ITEM_POST_DETAIL_REPLY

    private lateinit var overflowMenu: OverflowMenuPopup

    override fun createBinder(parent: ViewGroup): ItemPostDetailReplyBinding {
        overflowMenu = OverflowMenuPopup.create(parent.context, this)
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
            data
        )
    }

    private fun initReplyView(
        binding: ItemPostDetailReplyBinding,
        data: CommentViewData
    ) {

        binding.apply {
            val context = root.context

            tvCommenterName.text = data.user.name
            tvCommentContent.text = data.text

            if (data.isLiked) ivLike.setImageResource(R.drawable.ic_like_comment_filled)
            else ivLike.setImageResource(R.drawable.ic_like_comment_unfilled)

            tvCommentTime.text = TimeUtil.getDaysHoursOrMinutes(data.createdAt)

            if (data.likesCount == 0) likesCount.hide()
            else {
                likesCount.text =
                    context.resources.getQuantityString(
                        R.plurals.likes,
                        data.likesCount,
                        data.likesCount
                    )
                likesCount.show()
            }

            ivLike.setOnClickListener {
                postDetailAdapterListener.likeComment(data.id)
            }

            ivReplyMenu.setOnClickListener {
                PostTypeUtil.showOverflowMenu(
                    ivReplyMenu,
                    overflowMenu
                )
            }
        }
    }

    override fun onMenuItemClicked(menu: OverflowMenuItemViewData) {
        //TODO: Can we use postDetailAdapterListener here? As reply is also a comment
        postDetailReplyAdapterListener.onReplyMenuItemClicked(menu.dataId, menu.title)
    }
}