package com.likeminds.feedsx.post.detail.view.adapter.databinder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.feedsx.databinding.ItemPostDetailCommentBinding
import com.likeminds.feedsx.utils.customview.ViewDataBinder
import com.likeminds.feedsx.utils.model.BaseViewType

class ItemPostDetailCommentViewDataBinder :
    ViewDataBinder<ItemPostDetailCommentBinding, BaseViewType>() {

    override val viewType: Int
        get() = TODO("Not yet implemented")

    override fun createBinder(parent: ViewGroup): ItemPostDetailCommentBinding {
        return ItemPostDetailCommentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    }

    override fun bindData(
        binding: ItemPostDetailCommentBinding,
        data: BaseViewType,
        position: Int
    ) {
        TODO("Not yet implemented")
    }
}