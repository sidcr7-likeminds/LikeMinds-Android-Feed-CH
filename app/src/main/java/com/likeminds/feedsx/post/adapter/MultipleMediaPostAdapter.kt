package com.likeminds.feedsx.post.adapter

import com.likeminds.feedsx.post.adapter.databinder.postmultiplemedia.MultipleMediaImageItemViewDataBinder
import com.likeminds.feedsx.utils.customview.BaseRecyclerAdapter
import com.likeminds.feedsx.utils.customview.ViewDataBinder
import com.likeminds.feedsx.utils.model.BaseViewType

class MultipleMediaPostAdapter : BaseRecyclerAdapter<BaseViewType>() {

    init {
        initViewDataBinders()
    }

    override fun getSupportedViewDataBinder(): MutableList<ViewDataBinder<*, *>> {
        val viewDataBinders = ArrayList<ViewDataBinder<*, *>>(2)

        val multipleMediaImageBinding = MultipleMediaImageItemViewDataBinder()
        viewDataBinders.add(multipleMediaImageBinding)

        val multipleMediaVideoBinding = MultipleMediaImageItemViewDataBinder()
        viewDataBinders.add(multipleMediaVideoBinding)

        return viewDataBinders
    }
}