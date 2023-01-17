package com.likeminds.feedsx.post.adapter.databinder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.feedsx.databinding.ItemPostMultipleMediaBinding
import com.likeminds.feedsx.utils.customview.ViewDataBinder
import com.likeminds.feedsx.utils.model.BaseViewType
import com.likeminds.feedsx.utils.model.ITEM_POST_MULTIPLE_MEDIA

class PostMultipleMediaItemViewDataBinder :
    ViewDataBinder<ItemPostMultipleMediaBinding, BaseViewType>() {
    override val viewType: Int
        get() = ITEM_POST_MULTIPLE_MEDIA

    override fun createBinder(parent: ViewGroup): ItemPostMultipleMediaBinding {
        val inflater = LayoutInflater.from(parent.context)
        return ItemPostMultipleMediaBinding.inflate(inflater, parent, false)
    }

    override fun bindData(
        binding: ItemPostMultipleMediaBinding,
        data: BaseViewType,
        position: Int
    ) {
        TODO("Not yet implemented")
    }
}