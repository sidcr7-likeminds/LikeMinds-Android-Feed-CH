package com.likeminds.feedsampleapp.post.create.view.adapter

import com.likeminds.feedsampleapp.post.create.util.CreatePostListener
import com.likeminds.feedsampleapp.post.create.view.adapter.databinder.ItemCreatePostDocumentViewDataBinder
import com.likeminds.feedsampleapp.utils.customview.BaseRecyclerAdapter
import com.likeminds.feedsampleapp.utils.customview.ViewDataBinder
import com.likeminds.feedsampleapp.utils.model.BaseViewType

class CreatePostDocumentsAdapter constructor(
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