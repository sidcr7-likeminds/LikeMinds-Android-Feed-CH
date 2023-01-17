package com.likeminds.feedsx.post.adapter.databinder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.feedsx.databinding.ItemPostSingleImageBinding
import com.likeminds.feedsx.utils.customview.ViewDataBinder
import com.likeminds.feedsx.utils.model.BaseViewType
import com.likeminds.feedsx.utils.model.ITEM_POST_SINGLE_IMAGE

class PostSingleImageItemViewDataBinder :
    ViewDataBinder<ItemPostSingleImageBinding, BaseViewType>() {

    override val viewType: Int
        get() = ITEM_POST_SINGLE_IMAGE

    override fun createBinder(parent: ViewGroup): ItemPostSingleImageBinding {
        val inflater = LayoutInflater.from(parent.context)
        return ItemPostSingleImageBinding.inflate(inflater, parent, false)
    }

    override fun bindData(binding: ItemPostSingleImageBinding, data: BaseViewType, position: Int) {
        TODO("Not yet implemented")
    }
}