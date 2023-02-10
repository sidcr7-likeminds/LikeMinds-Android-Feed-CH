package com.likeminds.feedsx.posttypes.view.adapter

import com.likeminds.feedsx.posttypes.view.adapter.PostAdapter.PostAdapterListener
import com.likeminds.feedsx.posttypes.view.adapter.databinder.postdocuments.ItemDocumentViewDataBinder
import com.likeminds.feedsx.utils.customview.BaseRecyclerAdapter
import com.likeminds.feedsx.utils.customview.ViewDataBinder
import com.likeminds.feedsx.utils.model.BaseViewType

class DocumentsPostAdapter constructor(
    val listener: PostAdapterListener
) : BaseRecyclerAdapter<BaseViewType>() {

    init {
        initViewDataBinders()
    }

    override fun getSupportedViewDataBinder(): MutableList<ViewDataBinder<*, *>> {
        val viewDataBinders = ArrayList<ViewDataBinder<*, *>>(1)

        val documentsBinder = ItemDocumentViewDataBinder(listener)
        viewDataBinders.add(documentsBinder)

        return viewDataBinders
    }
}