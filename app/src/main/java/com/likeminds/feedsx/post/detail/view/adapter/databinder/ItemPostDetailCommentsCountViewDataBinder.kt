package com.likeminds.feedsx.post.detail.view.adapter.databinder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.feedsx.databinding.ItemPostDetailCommentsCountBinding
import com.likeminds.feedsx.utils.customview.ViewDataBinder
import com.likeminds.feedsx.utils.model.BaseViewType

class ItemPostDetailCommentsCountViewDataBinder :
    ViewDataBinder<ItemPostDetailCommentsCountBinding, BaseViewType>() {

    override val viewType: Int
        get() = TODO("Not yet implemented")

    override fun createBinder(parent: ViewGroup): ItemPostDetailCommentsCountBinding {
        return ItemPostDetailCommentsCountBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    }

    override fun bindData(
        binding: ItemPostDetailCommentsCountBinding,
        data: BaseViewType,
        position: Int
    ) {
        TODO("Not yet implemented")
    }
}