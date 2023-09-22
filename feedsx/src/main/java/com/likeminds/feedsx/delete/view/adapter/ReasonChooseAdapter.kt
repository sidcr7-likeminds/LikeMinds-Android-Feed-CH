package com.likeminds.feedsx.delete.view.adapter

import com.likeminds.feedsx.delete.model.ReasonChooseViewData
import com.likeminds.feedsx.delete.view.adapter.databinder.LMFeedReasonChooseViewDataBinder
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
        val lmFeedReasonChooseViewDataBinder = LMFeedReasonChooseViewDataBinder(listener)
        viewDataBinders.add(lmFeedReasonChooseViewDataBinder)
        return viewDataBinders
    }

    interface ReasonChooseAdapterListener {
        fun onOptionSelected(viewData: ReasonChooseViewData)
    }
}