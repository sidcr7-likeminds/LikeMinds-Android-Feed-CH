package com.likeminds.feedsx.post.detail.view.adapter.databinder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.feedsx.R
import com.likeminds.feedsx.databinding.ItemReplyViewMoreReplyBinding
import com.likeminds.feedsx.post.detail.model.ViewMoreReplyViewData
import com.likeminds.feedsx.post.detail.view.adapter.PostDetailReplyAdapter.PostDetailReplyAdapterListener
import com.likeminds.feedsx.utils.customview.ViewDataBinder
import com.likeminds.feedsx.utils.model.ITEM_REPLY_VIEW_MORE_REPLY

class ItemReplyViewMoreReplyViewDataBinder constructor(
    val listener: PostDetailReplyAdapterListener
) : ViewDataBinder<ItemReplyViewMoreReplyBinding, ViewMoreReplyViewData>() {

    override val viewType: Int
        get() = ITEM_REPLY_VIEW_MORE_REPLY

    override fun createBinder(parent: ViewGroup): ItemReplyViewMoreReplyBinding {
        return ItemReplyViewMoreReplyBinding.inflate(
            LayoutInflater.from(parent.context), parent,
            false
        )
    }

    override fun bindData(
        binding: ItemReplyViewMoreReplyBinding,
        data: ViewMoreReplyViewData,
        position: Int
    ) {
        initViewMoreReplies(
            binding,
            data
        )
    }

    // sets data and listeners to view more replies
    private fun initViewMoreReplies(
        binding: ItemReplyViewMoreReplyBinding,
        data: ViewMoreReplyViewData
    ) {
        binding.apply {
            tvReplies.text = root.context.getString(
                R.string.placeholder_replies,
                data.currentCount,
                data.totalCommentsCount
            )
            tvViewMoreReplies.setOnClickListener {
                listener.viewMoreReplies(
                    data.parentCommentId,
                    data.page
                )
            }
        }
    }
}