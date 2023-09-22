package com.likeminds.feedsx.post.detail.view.adapter.databinder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.feedsx.R
import com.likeminds.feedsx.databinding.LmFeedItemPostDetailCommentsCountBinding
import com.likeminds.feedsx.post.detail.model.CommentsCountViewData
import com.likeminds.feedsx.utils.customview.ViewDataBinder
import com.likeminds.feedsx.utils.model.ITEM_POST_DETAIL_COMMENTS_COUNT

class ItemPostDetailCommentsCountViewDataBinder :
    ViewDataBinder<LmFeedItemPostDetailCommentsCountBinding, CommentsCountViewData>() {

    override val viewType: Int
        get() = ITEM_POST_DETAIL_COMMENTS_COUNT

    override fun createBinder(parent: ViewGroup): LmFeedItemPostDetailCommentsCountBinding {
        return LmFeedItemPostDetailCommentsCountBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    }

    override fun bindData(
        binding: LmFeedItemPostDetailCommentsCountBinding,
        data: CommentsCountViewData,
        position: Int
    ) {

        val context = binding.root.context
        binding.tvCommentsCount.text = context.resources.getQuantityString(
            R.plurals.comments,
            data.commentsCount,
            data.commentsCount
        )
    }
}