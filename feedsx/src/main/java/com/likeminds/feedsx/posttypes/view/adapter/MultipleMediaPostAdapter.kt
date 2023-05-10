package com.likeminds.feedsx.posttypes.view.adapter

import com.likeminds.feedsx.posttypes.view.adapter.databinder.postmultiplemedia.ItemMultipleMediaImageViewDataBinder
import com.likeminds.feedsx.posttypes.view.adapter.databinder.postmultiplemedia.ItemMultipleMediaVideoViewDataBinder
import com.likeminds.feedsx.utils.customview.BaseRecyclerAdapter
import com.likeminds.feedsx.utils.customview.ViewDataBinder
import com.likeminds.feedsx.utils.model.BaseViewType

class MultipleMediaPostAdapter(val listener: PostAdapterListener) :
    BaseRecyclerAdapter<BaseViewType>() {

    init {
        initViewDataBinders()
    }

    override fun getSupportedViewDataBinder(): MutableList<ViewDataBinder<*, *>> {
        val viewDataBinders = ArrayList<ViewDataBinder<*, *>>(2)

        val multipleMediaImageBinder = ItemMultipleMediaImageViewDataBinder(listener)
        viewDataBinders.add(multipleMediaImageBinder)

        val multipleMediaVideoBinder = ItemMultipleMediaVideoViewDataBinder(listener)
        viewDataBinders.add(multipleMediaVideoBinder)

        return viewDataBinders
    }
}