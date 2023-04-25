package com.likeminds.feedsx.post.edit.view.adapter

import com.likeminds.feedsx.posttypes.view.adapter.databinder.postdocuments.ItemDocumentViewDataBinder
import com.likeminds.feedsx.utils.customview.BaseRecyclerAdapter
import com.likeminds.feedsx.utils.customview.ViewDataBinder
import com.likeminds.feedsx.utils.model.BaseViewType

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