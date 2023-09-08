package com.likeminds.feedsx.post.create.view.adapter

import com.likeminds.feedsx.post.create.util.CreatePostListener
import com.likeminds.feedsx.post.create.view.adapter.databinder.LMFeedItemCreatePostDocumentViewDataBinder
import com.likeminds.feedsx.utils.customview.BaseRecyclerAdapter
import com.likeminds.feedsx.utils.customview.ViewDataBinder
import com.likeminds.feedsx.utils.model.BaseViewType

class CreatePostDocumentsAdapter constructor(
    private val listener: CreatePostListener
) : BaseRecyclerAdapter<BaseViewType>() {

    init {
        initViewDataBinders()
    }

    override fun getSupportedViewDataBinder(): MutableList<ViewDataBinder<*, *>> {
        val viewDataBinders = ArrayList<ViewDataBinder<*, *>>(1)

        val documentsBinding = LMFeedItemCreatePostDocumentViewDataBinder(listener)
        viewDataBinders.add(documentsBinding)

        return viewDataBinders
    }
}