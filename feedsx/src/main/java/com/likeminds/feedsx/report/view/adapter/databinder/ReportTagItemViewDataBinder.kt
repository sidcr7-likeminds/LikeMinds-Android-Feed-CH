package com.likeminds.feedsx.report.view.adapter.databinder

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.likeminds.feedsx.R
import com.likeminds.feedsx.branding.model.LMFeedBranding
import com.likeminds.feedsx.databinding.LmFeedItemReportTagsBinding
import com.likeminds.feedsx.report.model.ReportTagViewData
import com.likeminds.feedsx.report.view.adapter.ReportAdapter.ReportAdapterListener
import com.likeminds.feedsx.utils.ViewUtils
import com.likeminds.feedsx.utils.customview.ViewDataBinder
import com.likeminds.feedsx.utils.model.ITEM_REPORT_TAG

class ReportTagItemViewDataBinder constructor(
    private val listener: ReportAdapterListener
) : ViewDataBinder<LmFeedItemReportTagsBinding, ReportTagViewData>() {

    override val viewType: Int
        get() = ITEM_REPORT_TAG

    override fun createBinder(parent: ViewGroup): LmFeedItemReportTagsBinding {
        val binding = LmFeedItemReportTagsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        setListeners(binding)
        return binding
    }

    // sets tag background to the buttons color
    private fun setTagBackground(binding: LmFeedItemReportTagsBinding) {
        val drawable = binding.tvReportTag.background as GradientDrawable
        drawable.mutate()
        val width = ViewUtils.dpToPx(1)

        binding.apply {
            if (reportTagViewData?.isSelected == true) {
                drawable.setStroke(width, LMFeedBranding.getButtonsColor())
            } else {
                drawable.setStroke(
                    width,
                    ContextCompat.getColor(root.context, R.color.brown_grey)
                )
            }
        }
    }

    override fun bindData(
        binding: LmFeedItemReportTagsBinding,
        data: ReportTagViewData,
        position: Int
    ) {
        binding.apply {
            reportTagViewData = data
            buttonColor = LMFeedBranding.getButtonsColor()
            setTagBackground(this)
        }
    }

    // sets click listener to handle selected report tag
    private fun setListeners(binding: LmFeedItemReportTagsBinding) {
        binding.apply {
            tvReportTag.setOnClickListener {
                val reportTagViewData = reportTagViewData ?: return@setOnClickListener
                listener.reportTagSelected(reportTagViewData)
            }
        }
    }
}