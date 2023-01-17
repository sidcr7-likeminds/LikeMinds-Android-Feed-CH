package com.likeminds.feedsx.post.adapter.databinder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.feedsx.databinding.ItemPostDocumentsBinding
import com.likeminds.feedsx.utils.customview.ViewDataBinder
import com.likeminds.feedsx.utils.model.BaseViewType
import com.likeminds.feedsx.utils.model.ITEM_POST_DOCUMENTS

class PostDocumentsItemViewDataBinder :
    ViewDataBinder<ItemPostDocumentsBinding, BaseViewType>() {
    override val viewType: Int
        get() = ITEM_POST_DOCUMENTS

    override fun createBinder(parent: ViewGroup): ItemPostDocumentsBinding {
        val inflater = LayoutInflater.from(parent.context)
        return ItemPostDocumentsBinding.inflate(inflater, parent, false)
    }

    override fun bindData(binding: ItemPostDocumentsBinding, data: BaseViewType, position: Int) {
        TODO("Not yet implemented")
    }
}