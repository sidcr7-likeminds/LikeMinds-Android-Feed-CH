package com.likeminds.feedsx.post.adapter

import com.likeminds.feedsx.post.adapter.databinder.OverflowMenuItemBinder
import com.likeminds.feedsx.post.model.OverflowMenuItemViewData
import com.likeminds.feedsx.utils.customview.BaseRecyclerAdapter
import com.likeminds.feedsx.utils.customview.ViewDataBinder
import com.likeminds.feedsx.utils.model.BaseViewType

class OverflowMenuAdapter constructor(
    val overflowMenuAdapterListener: OverflowMenuAdapterListener
) : BaseRecyclerAdapter<BaseViewType>() {

    init {
        initViewDataBinders()
    }

    override fun getSupportedViewDataBinder(): MutableList<ViewDataBinder<*, BaseViewType>> {
        val viewDataBinders = ArrayList<ViewDataBinder<*, BaseViewType>>(1)
        viewDataBinders.add(OverflowMenuItemBinder(overflowMenuAdapterListener))
        return viewDataBinders
    }

}

fun interface OverflowMenuAdapterListener {
    fun onMenuItemClicked(menu: OverflowMenuItemViewData)
}