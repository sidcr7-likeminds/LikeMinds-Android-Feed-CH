package com.likeminds.feedsx.post.view.adapter.databinder

import com.likeminds.feedsx.post.view.adapter.databinder.createpostmultiplemedia.ItemCreatePostMultipleMediaImageViewDataBinder
import com.likeminds.feedsx.posttypes.view.adapter.databinder.postmultiplemedia.ItemCreatePostMultipleMediaVideoViewDataBinder
import com.likeminds.feedsx.utils.customview.BaseRecyclerAdapter
import com.likeminds.feedsx.utils.customview.ViewDataBinder
import com.likeminds.feedsx.utils.model.BaseViewType

class MultipleMediaCreatePostAdapter : BaseRecyclerAdapter<BaseViewType>() {

    init {
        initViewDataBinders()
    }

    override fun getSupportedViewDataBinder(): MutableList<ViewDataBinder<*, *>> {
        val viewDataBinders = ArrayList<ViewDataBinder<*, *>>(2)

        val createPostMultipleMediaImageViewDataBinder = ItemCreatePostMultipleMediaImageViewDataBinder()
        viewDataBinders.add(createPostMultipleMediaImageViewDataBinder)

        val createPostMultipleMediaVideoViewDataBinder = ItemCreatePostMultipleMediaVideoViewDataBinder()
        viewDataBinders.add(createPostMultipleMediaVideoViewDataBinder)

        return viewDataBinders
    }
}