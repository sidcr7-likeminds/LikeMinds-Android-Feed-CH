package com.likeminds.feedsample.report.view.adapter

import com.likeminds.feedsample.report.model.ReportTagViewData
import com.likeminds.feedsample.report.view.adapter.databinder.ReportTagItemViewDataBinder
import com.likeminds.feedsample.utils.customview.BaseRecyclerAdapter
import com.likeminds.feedsample.utils.customview.ViewDataBinder
import com.likeminds.feedsample.utils.model.BaseViewType

class ReportAdapter constructor(
    private val listener: ReportAdapterListener
) : BaseRecyclerAdapter<BaseViewType>() {

    init {
        initViewDataBinders()
    }

    override fun getSupportedViewDataBinder(): MutableList<ViewDataBinder<*, *>> {
        val viewDataBinders = ArrayList<ViewDataBinder<*, *>>()
        viewDataBinders.add(ReportTagItemViewDataBinder(listener))
        return viewDataBinders
    }

    interface ReportAdapterListener {
        fun reportTagSelected(reportTagViewData: ReportTagViewData) {}
    }
}