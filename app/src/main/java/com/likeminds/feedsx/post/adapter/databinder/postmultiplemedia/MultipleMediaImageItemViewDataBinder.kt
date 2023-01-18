package com.likeminds.feedsx.post.adapter.databinder.postmultiplemedia

import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.feedsx.databinding.ItemMultipleMediaImageBinding
import com.likeminds.feedsx.utils.customview.ViewDataBinder
import com.likeminds.feedsx.utils.model.BaseViewType
import com.likeminds.feedsx.utils.model.ITEM_MULTIPLE_MEDIA_IMAGE

class MultipleMediaImageItemViewDataBinder :
    ViewDataBinder<ItemMultipleMediaImageBinding, BaseViewType>() {

    override val viewType: Int
        get() = ITEM_MULTIPLE_MEDIA_IMAGE

    override fun createBinder(parent: ViewGroup): ItemMultipleMediaImageBinding {
        val inflater = LayoutInflater.from(parent.context)
        return ItemMultipleMediaImageBinding.inflate(inflater, parent, false)
    }

    override fun bindData(
        binding: ItemMultipleMediaImageBinding,
        data: BaseViewType,
        position: Int
    ) {
        TODO("Not yet implemented")
    }
}