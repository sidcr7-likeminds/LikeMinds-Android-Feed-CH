package com.likeminds.feedsample.posttypes.view.adapter

import com.likeminds.feedsample.posttypes.view.adapter.databinder.postdocuments.ItemDocumentViewDataBinder
import com.likeminds.feedsample.utils.customview.BaseRecyclerAdapter
import com.likeminds.feedsample.utils.customview.ViewDataBinder
import com.likeminds.feedsample.utils.model.BaseViewType

class DocumentsPostAdapter constructor(
    val listener: PostAdapterListener
) : BaseRecyclerAdapter<BaseViewType>() {

    init {
        initViewDataBinders()
    }

    override fun getSupportedViewDataBinder(): MutableList<ViewDataBinder<*, *>> {
        val viewDataBinders = ArrayList<ViewDataBinder<*, *>>(1)

        val documentsBinder = ItemDocumentViewDataBinder()
        viewDataBinders.add(documentsBinder)

        return viewDataBinders
    }
}