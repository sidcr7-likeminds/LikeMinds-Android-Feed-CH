package com.likeminds.feedsx.post.adapter.databinder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.feedsx.databinding.ItemPostLinkBinding
import com.likeminds.feedsx.utils.customview.ViewDataBinder
import com.likeminds.feedsx.utils.model.BaseViewType
import com.likeminds.feedsx.utils.model.ITEM_POST_LINK

class PostLinkItemViewDataBinder :
    ViewDataBinder<ItemPostLinkBinding, BaseViewType>() {
    override val viewType: Int
        get() = ITEM_POST_LINK

    override fun createBinder(parent: ViewGroup): ItemPostLinkBinding {
        val inflater = LayoutInflater.from(parent.context)
        return ItemPostLinkBinding.inflate(inflater, parent, false)
    }

    override fun bindData(binding: ItemPostLinkBinding, data: BaseViewType, position: Int) {
        TODO("Not yet implemented")
    }
}