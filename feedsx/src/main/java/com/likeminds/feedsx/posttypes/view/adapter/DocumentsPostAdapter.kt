package com.likeminds.feedsx.posttypes.view.adapter

import com.likeminds.feedsx.posttypes.view.adapter.databinder.postdocuments.LMFeedItemDocumentViewDataBinder
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

        val documentsBinder = LMFeedItemDocumentViewDataBinder()
        viewDataBinders.add(documentsBinder)

        return viewDataBinders
    }
}