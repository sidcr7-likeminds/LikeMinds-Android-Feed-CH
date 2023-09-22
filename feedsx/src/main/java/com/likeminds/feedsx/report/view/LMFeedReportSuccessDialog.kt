package com.likeminds.feedsx.report.view

import com.likeminds.feedsx.R
import com.likeminds.feedsx.databinding.LmFeedDialogReportSuccessBinding
import com.likeminds.feedsx.utils.customview.BaseDialogFragment

class LMFeedReportSuccessDialog constructor(
    private val type: String
) : BaseDialogFragment<LmFeedDialogReportSuccessBinding>() {

    override fun getViewBinding(): LmFeedDialogReportSuccessBinding {
        return LmFeedDialogReportSuccessBinding.inflate(layoutInflater)
    }

    override val cancellable: Boolean
        get() = true

    override val margin: Int
        get() = 30

    companion object {
        const val TAG = "ReportSuccessDialog"
    }

    override fun setUpViews() {
        super.setUpViews()
        initView()
    }

    //set header and sub header as per [type] received in constructor
    private fun initView() {
        binding.tvReportedHeader.text = getString(R.string.s_is_reported_for_review, type)
        binding.tvReportSubHeader.text = getString(
            R.string.our_team_will_look_into_your_feedback_and_will_take_appropriate_action_on_this_s,
            type
        )

        binding.btnOk.setOnClickListener {
            dismiss()
        }
    }
}