package com.likeminds.feedsample.report.view.adapter.databinder

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.likeminds.feedsample.R
import com.likeminds.feedsample.branding.model.LMBranding
import com.likeminds.feedsample.databinding.ItemReportTagsBinding
import com.likeminds.feedsample.report.model.ReportTagViewData
import com.likeminds.feedsample.report.view.adapter.ReportAdapter.ReportAdapterListener
import com.likeminds.feedsample.utils.ViewUtils
import com.likeminds.feedsample.utils.customview.ViewDataBinder
import com.likeminds.feedsample.utils.model.ITEM_REPORT_TAG

class ReportTagItemViewDataBinder constructor(
    private val listener: ReportAdapterListener
) : ViewDataBinder<ItemReportTagsBinding, ReportTagViewData>() {

    override val viewType: Int
        get() = ITEM_REPORT_TAG

    override fun createBinder(parent: ViewGroup): ItemReportTagsBinding {
        val binding = ItemReportTagsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        setListeners(binding)
        return binding
    }

    // sets tag background to the buttons color
    private fun setTagBackground(binding: ItemReportTagsBinding) {
        val drawable = binding.tvReportTag.background as GradientDrawable
        drawable.mutate()
        val width = ViewUtils.dpToPx(1)

        binding.apply {
            if (reportTagViewData?.isSelected == true) {
                drawable.setStroke(width, LMBranding.getButtonsColor())
            } else {
                drawable.setStroke(
                    width,
                    ContextCompat.getColor(root.context, R.color.brown_grey)
                )
            }
        }
    }

    override fun bindData(
        binding: ItemReportTagsBinding,
        data: ReportTagViewData,
        position: Int
    ) {
        binding.apply {
            reportTagViewData = data
            buttonColor = LMBranding.getButtonsColor()
            setTagBackground(this)
        }
    }

    // sets click listener to handle selected report tag
    private fun setListeners(binding: ItemReportTagsBinding) {
        binding.apply {
            tvReportTag.setOnClickListener {
                val reportTagViewData = reportTagViewData ?: return@setOnClickListener
                listener.reportTagSelected(reportTagViewData)
            }
        }
    }
}