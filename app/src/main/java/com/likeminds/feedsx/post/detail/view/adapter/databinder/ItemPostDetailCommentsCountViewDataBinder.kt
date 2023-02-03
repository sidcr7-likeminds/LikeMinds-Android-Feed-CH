package com.likeminds.feedsx.post.detail.view.adapter.databinder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.feedsx.R
import com.likeminds.feedsx.databinding.ItemPostDetailCommentsCountBinding
import com.likeminds.feedsx.post.detail.model.CommentsCountViewData
import com.likeminds.feedsx.utils.customview.ViewDataBinder
import com.likeminds.feedsx.utils.model.ITEM_POST_DETAIL_COMMENTS_COUNT

class ItemPostDetailCommentsCountViewDataBinder :
    ViewDataBinder<ItemPostDetailCommentsCountBinding, CommentsCountViewData>() {

    override val viewType: Int
        get() = ITEM_POST_DETAIL_COMMENTS_COUNT

    override fun createBinder(parent: ViewGroup): ItemPostDetailCommentsCountBinding {
        return ItemPostDetailCommentsCountBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    }

    override fun bindData(
        binding: ItemPostDetailCommentsCountBinding,
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