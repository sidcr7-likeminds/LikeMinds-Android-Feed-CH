package com.likeminds.feedsample.delete.view.adapter

import com.likeminds.feedsample.delete.model.ReasonChooseViewData
import com.likeminds.feedsample.delete.view.adapter.databinder.ReasonChooseViewDataBinder
import com.likeminds.feedsample.utils.customview.BaseRecyclerAdapter
import com.likeminds.feedsample.utils.customview.ViewDataBinder
import com.likeminds.feedsample.utils.model.BaseViewType

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