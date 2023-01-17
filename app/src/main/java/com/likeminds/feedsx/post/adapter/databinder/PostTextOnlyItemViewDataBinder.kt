package com.likeminds.feedsx.post.adapter.databinder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.feedsx.databinding.ItemPostTextOnlyBinding
import com.likeminds.feedsx.utils.customview.ViewDataBinder
import com.likeminds.feedsx.utils.model.BaseViewType
import com.likeminds.feedsx.utils.model.ITEM_POST_TEXT_ONLY

class PostTextOnlyItemViewDataBinder :
    ViewDataBinder<ItemPostTextOnlyBinding, BaseViewType>() {
    override val viewType: Int
        get() = ITEM_POST_TEXT_ONLY

    override fun createBinder(parent: ViewGroup): ItemPostTextOnlyBinding {
        val inflater = LayoutInflater.from(parent.context)
        return ItemPostTextOnlyBinding.inflate(inflater, parent, false)
    }

    override fun bindData(binding: ItemPostTextOnlyBinding, data: BaseViewType, position: Int) {
        TODO("Not yet implemented")
    }
}