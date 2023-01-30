package com.likeminds.feedsx.post.view.adapter.databinder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.feedsx.databinding.ItemCreatePostDocumentBinding
import com.likeminds.feedsx.posttypes.model.AttachmentViewData
import com.likeminds.feedsx.utils.customview.ViewDataBinder
import com.likeminds.feedsx.utils.model.ITEM_CREATE_POST_DOCUMENTS_ITEM

class ItemCreatePostDocumentViewDataBinder :
    ViewDataBinder<ItemCreatePostDocumentBinding, AttachmentViewData>() {
    override val viewType: Int
        get() = ITEM_CREATE_POST_DOCUMENTS_ITEM

    override fun createBinder(parent: ViewGroup): ItemCreatePostDocumentBinding {
        return ItemCreatePostDocumentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    }

    override fun bindData(
        binding: ItemCreatePostDocumentBinding,
        data: AttachmentViewData,
        position: Int
    ) {
        TODO("Not yet implemented")
    }
}