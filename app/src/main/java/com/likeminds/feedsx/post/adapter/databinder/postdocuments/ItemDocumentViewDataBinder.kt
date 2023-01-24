package com.likeminds.feedsx.post.adapter.databinder.postdocuments

import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.feedsx.databinding.ItemDocumentBinding
import com.likeminds.feedsx.post.adapter.PostAdapter.PostAdapterListener
import com.likeminds.feedsx.post.model.AttachmentViewData
import com.likeminds.feedsx.utils.customview.ViewDataBinder
import com.likeminds.feedsx.utils.model.ITEM_POST_DOCUMENTS_ITEM

class ItemDocumentViewDataBinder constructor(
    val listener: PostAdapterListener
) : ViewDataBinder<ItemDocumentBinding, AttachmentViewData>() {
    override val viewType: Int
        get() = ITEM_POST_DOCUMENTS_ITEM

    override fun createBinder(parent: ViewGroup): ItemDocumentBinding {
        return ItemDocumentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    }

    override fun bindData(
        binding: ItemDocumentBinding,
        data: AttachmentViewData,
        position: Int
    ) {

    }

}