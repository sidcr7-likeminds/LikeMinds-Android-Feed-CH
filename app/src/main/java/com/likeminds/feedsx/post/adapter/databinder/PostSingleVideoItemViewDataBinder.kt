package com.likeminds.feedsx.post.adapter.databinder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.feedsx.databinding.ItemPostSingleVideoBinding
import com.likeminds.feedsx.utils.customview.ViewDataBinder
import com.likeminds.feedsx.utils.model.BaseViewType
import com.likeminds.feedsx.utils.model.ITEM_POST_SINGLE_VIDEO

class PostSingleVideoItemViewDataBinder :
    ViewDataBinder<ItemPostSingleVideoBinding, BaseViewType>() {
    override val viewType: Int
        get() = ITEM_POST_SINGLE_VIDEO

    override fun createBinder(parent: ViewGroup): ItemPostSingleVideoBinding {
        val inflater = LayoutInflater.from(parent.context)
        return ItemPostSingleVideoBinding.inflate(inflater, parent, false)
    }

    override fun bindData(binding: ItemPostSingleVideoBinding, data: BaseViewType, position: Int) {
        TODO("Not yet implemented")
    }
}