package com.likeminds.feedsx.posttypes.view.adapter

import com.likeminds.feedsx.posttypes.view.adapter.databinder.postmultiplemedia.ItemMultipleMediaImageViewDataBinder
import com.likeminds.feedsx.posttypes.view.adapter.databinder.postmultiplemedia.ItemMultipleMediaVideoViewDataBinder
import com.likeminds.feedsx.utils.customview.BaseRecyclerAdapter
import com.likeminds.feedsx.utils.customview.ViewDataBinder
import com.likeminds.feedsx.utils.model.BaseViewType

class MultipleMediaPostAdapter : BaseRecyclerAdapter<BaseViewType>() {

    init {
        initViewDataBinders()
    }

    override fun getSupportedViewDataBinder(): MutableList<ViewDataBinder<*, *>> {
        val viewDataBinders = ArrayList<ViewDataBinder<*, *>>(2)

        val multipleMediaImageBinding = ItemMultipleMediaImageViewDataBinder()
        viewDataBinders.add(multipleMediaImageBinding)

        val multipleMediaVideoBinding = ItemMultipleMediaVideoViewDataBinder()
        viewDataBinders.add(multipleMediaVideoBinding)

        return viewDataBinders
    }
}