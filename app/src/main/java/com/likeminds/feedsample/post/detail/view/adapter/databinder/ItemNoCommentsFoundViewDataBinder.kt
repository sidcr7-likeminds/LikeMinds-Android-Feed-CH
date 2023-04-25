package com.likeminds.feedsample.post.detail.view.adapter.databinder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.feedsample.databinding.ItemNoCommentsFoundBinding
import com.likeminds.feedsample.utils.customview.ViewDataBinder
import com.likeminds.feedsample.utils.model.BaseViewType
import com.likeminds.feedsample.utils.model.ITEM_NO_COMMENTS_FOUND

class ItemNoCommentsFoundViewDataBinder :
    ViewDataBinder<ItemNoCommentsFoundBinding, BaseViewType>() {

    override val viewType: Int
        get() = ITEM_NO_COMMENTS_FOUND

    override fun createBinder(parent: ViewGroup): ItemNoCommentsFoundBinding {
        return ItemNoCommentsFoundBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    }

    override fun bindData(
        binding: ItemNoCommentsFoundBinding,
        data: BaseViewType,
        position: Int
    ) {
    }
}