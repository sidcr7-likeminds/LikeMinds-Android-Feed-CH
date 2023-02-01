package com.likeminds.feedsx.post.view.adapter

import com.likeminds.feedsx.post.util.CreatePostListener
import com.likeminds.feedsx.post.view.adapter.databinder.ItemCreatePostDocumentViewDataBinder
import com.likeminds.feedsx.utils.customview.BaseRecyclerAdapter
import com.likeminds.feedsx.utils.customview.ViewDataBinder
import com.likeminds.feedsx.utils.model.BaseViewType

class DocumentsCreatePostAdapter constructor(
    private val listener: CreatePostListener
) : BaseRecyclerAdapter<BaseViewType>() {

    init {
        initViewDataBinders()
    }

    override fun getSupportedViewDataBinder(): MutableList<ViewDataBinder<*, *>> {
        val viewDataBinders = ArrayList<ViewDataBinder<*, *>>(1)

        val documentsBinding = ItemCreatePostDocumentViewDataBinder(listener)
        viewDataBinders.add(documentsBinding)

        return viewDataBinders
    }
}