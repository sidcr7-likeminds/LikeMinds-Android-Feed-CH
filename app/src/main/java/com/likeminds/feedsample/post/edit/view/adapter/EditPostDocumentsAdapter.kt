package com.likeminds.feedsample.post.edit.view.adapter

import com.likeminds.feedsample.posttypes.view.adapter.databinder.postdocuments.ItemDocumentViewDataBinder
import com.likeminds.feedsample.utils.customview.BaseRecyclerAdapter
import com.likeminds.feedsample.utils.customview.ViewDataBinder
import com.likeminds.feedsample.utils.model.BaseViewType

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