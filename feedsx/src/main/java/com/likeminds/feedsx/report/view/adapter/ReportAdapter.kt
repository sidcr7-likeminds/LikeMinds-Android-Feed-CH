package com.likeminds.feedsx.report.view.adapter

import com.likeminds.feedsx.report.model.ReportTagViewData
import com.likeminds.feedsx.report.view.adapter.databinder.ReportTagItemViewDataBinder
import com.likeminds.feedsx.utils.customview.BaseRecyclerAdapter
import com.likeminds.feedsx.utils.customview.ViewDataBinder
import com.likeminds.feedsx.utils.model.BaseViewType

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
        fun reportTagSelected(reportTagViewData: ReportTagViewData) {
            //triggered when a user selects a tag
        }
    }
}