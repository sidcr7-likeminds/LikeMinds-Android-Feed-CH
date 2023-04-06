package com.likeminds.feedsx.report.view.adapter.databinder

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.likeminds.feedsx.R
import com.likeminds.feedsx.branding.model.LMBranding
import com.likeminds.feedsx.databinding.ItemReportTagsBinding
import com.likeminds.feedsx.report.model.ReportTagViewData
import com.likeminds.feedsx.report.view.adapter.ReportAdapter.ReportAdapterListener
import com.likeminds.feedsx.utils.ViewUtils
import com.likeminds.feedsx.utils.customview.ViewDataBinder
import com.likeminds.feedsx.utils.model.ITEM_REPORT_TAG

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
        setTagBackground(binding)
        setListeners(binding)
        return binding
    }

    // sets tag background to the buttons color
    private fun setTagBackground(binding: ItemReportTagsBinding) {
        val drawable = binding.tvReportTag.background as GradientDrawable
        drawable.mutate()
        val width = ViewUtils.dpToPx(1)

        if (binding.reportTagViewData?.isSelected == true) {
            drawable.color = ColorStateList.valueOf(LMBranding.getButtonsColor())
            drawable.setStroke(width, LMBranding.getButtonsColor())
        } else {
            drawable.color = ColorStateList.valueOf(Color.WHITE)
            drawable.setStroke(
                width,
                ContextCompat.getColor(binding.root.context, R.color.brown_grey)
            )
        }
    }

    override fun bindData(
        binding: ItemReportTagsBinding,
        data: ReportTagViewData,
        position: Int
    ) {
        binding.reportTagViewData = data
    }

    // sets click listener to handle selected report tag
    private fun setListeners(binding: ItemReportTagsBinding) {
        binding.tvReportTag.setOnClickListener {
            val reportTagViewData = binding.reportTagViewData ?: return@setOnClickListener
            listener.reportTagSelected(reportTagViewData)
        }
    }
}