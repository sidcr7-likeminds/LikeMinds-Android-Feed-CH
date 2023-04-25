package com.likeminds.feedsampleapp.post.detail.view.adapter.databinder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.feedsampleapp.R
import com.likeminds.feedsampleapp.databinding.ItemPostDetailCommentsCountBinding
import com.likeminds.feedsampleapp.post.detail.model.CommentsCountViewData
import com.likeminds.feedsampleapp.utils.customview.ViewDataBinder
import com.likeminds.feedsampleapp.utils.model.ITEM_POST_DETAIL_COMMENTS_COUNT

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