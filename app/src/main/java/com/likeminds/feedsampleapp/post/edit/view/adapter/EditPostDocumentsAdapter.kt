package com.likeminds.feedsampleapp.post.edit.view.adapter

import com.likeminds.feedsampleapp.posttypes.view.adapter.databinder.postdocuments.ItemDocumentViewDataBinder
import com.likeminds.feedsampleapp.utils.customview.BaseRecyclerAdapter
import com.likeminds.feedsampleapp.utils.customview.ViewDataBinder
import com.likeminds.feedsampleapp.utils.model.BaseViewType

class EditPostDocumentsAdapter : BaseRecyclerAdapter<BaseViewType>() {

    init {
        initViewDataBinders()
    }

    override fun getSupportedViewDataBinder(): MutableList<ViewDataBinder<*, *>> {
        val viewDataBinders = ArrayList<ViewDataBinder<*, *>>(1)

        val documentsBinding = ItemDocumentViewDataBinder()
        viewDataBinders.add(documentsBinding)

        return viewDataBinders
    }
}