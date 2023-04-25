package com.likeminds.feedsampleapp.delete.view.adapter

import com.likeminds.feedsampleapp.delete.model.ReasonChooseViewData
import com.likeminds.feedsampleapp.delete.view.adapter.databinder.ReasonChooseViewDataBinder
import com.likeminds.feedsampleapp.utils.customview.BaseRecyclerAdapter
import com.likeminds.feedsampleapp.utils.customview.ViewDataBinder
import com.likeminds.feedsampleapp.utils.model.BaseViewType

class ReasonChooseAdapter constructor(
    val listener: ReasonChooseAdapterListener,
) : BaseRecyclerAdapter<BaseViewType>() {

    init {
        initViewDataBinders()
    }

    override fun getSupportedViewDataBinder(): MutableList<ViewDataBinder<*, *>> {
        val viewDataBinders = ArrayList<ViewDataBinder<*, *>>(1)
        val reasonChooseViewDataBinder = ReasonChooseViewDataBinder(listener)
        viewDataBinders.add(reasonChooseViewDataBinder)
        return viewDataBinders
    }

    interface ReasonChooseAdapterListener {
        fun onOptionSelected(viewData: ReasonChooseViewData)
    }
}