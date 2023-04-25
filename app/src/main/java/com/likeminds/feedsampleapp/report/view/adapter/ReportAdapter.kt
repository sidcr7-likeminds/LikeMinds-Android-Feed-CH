package com.likeminds.feedsampleapp.report.view.adapter

import com.likeminds.feedsampleapp.report.model.ReportTagViewData
import com.likeminds.feedsampleapp.report.view.adapter.databinder.ReportTagItemViewDataBinder
import com.likeminds.feedsampleapp.utils.customview.BaseRecyclerAdapter
import com.likeminds.feedsampleapp.utils.customview.ViewDataBinder
import com.likeminds.feedsampleapp.utils.model.BaseViewType

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