package com.likeminds.feedsx.report.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.likeminds.feedsx.databinding.LmFeedActivityReportBinding
import com.likeminds.feedsx.report.model.ReportExtras
import com.likeminds.feedsx.utils.customview.BaseAppCompatActivity

class LMFeedReportActivity : BaseAppCompatActivity() {


    companion object {
        const val ARG_REPORTS = "ARG_REPORTS"

        @JvmStatic
        fun start(context: Context, extras: ReportExtras) {
            val intent = Intent(context, LMFeedReportActivity::class.java)
            val bundle = Bundle()
            bundle.putParcelable(ARG_REPORTS, extras)
            intent.putExtra("bundle", bundle)
            context.startActivity(intent)
        }

        @JvmStatic
        fun getIntent(context: Context, extras: ReportExtras): Intent {
            val intent = Intent(context, LMFeedReportActivity::class.java)
            val bundle = Bundle()
            bundle.putParcelable(ARG_REPORTS, extras)
            intent.putExtra("bundle", bundle)
            return intent
        }
    }

    private lateinit var binding: LmFeedActivityReportBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LmFeedActivityReportBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}