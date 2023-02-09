package com.likeminds.feedsx.deleteentity.view.adapter

import com.likeminds.feedsx.deleteentity.model.ReasonChooseViewData
import com.likeminds.feedsx.deleteentity.view.adapter.databinder.ReasonChooseViewDataBinder
import com.likeminds.feedsx.utils.customview.BaseRecyclerAdapter
import com.likeminds.feedsx.utils.customview.ViewDataBinder
import com.likeminds.feedsx.utils.model.BaseViewType

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