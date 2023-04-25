package com.likeminds.feedsample.posttypes.view.adapter.databinder.postdocuments

import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.feedsample.databinding.ItemDocumentBinding
import com.likeminds.feedsample.posttypes.model.AttachmentViewData
import com.likeminds.feedsample.posttypes.util.PostTypeUtil
import com.likeminds.feedsample.utils.customview.ViewDataBinder
import com.likeminds.feedsample.utils.model.ITEM_POST_DOCUMENTS_ITEM

class ItemDocumentViewDataBinder : ViewDataBinder<ItemDocumentBinding, AttachmentViewData>() {
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
        // sets the document data on the doc view
        PostTypeUtil.initDocument(
            binding,
            data
        )
    }
}