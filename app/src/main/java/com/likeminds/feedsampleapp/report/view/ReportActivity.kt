package com.likeminds.feedsampleapp.report.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.likeminds.feedsampleapp.databinding.ActivityReportBinding
import com.likeminds.feedsampleapp.report.model.ReportExtras
import com.likeminds.feedsampleapp.utils.customview.BaseAppCompatActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReportActivity : BaseAppCompatActivity() {


    companion object {
        const val ARG_REPORTS = "ARG_REPORTS"

        @JvmStatic
        fun start(context: Context, extras: ReportExtras) {
            val intent = Intent(context, ReportActivity::class.java)
            val bundle = Bundle()
            bundle.putParcelable(ARG_REPORTS, extras)
            intent.putExtra("bundle", bundle)
            context.startActivity(intent)
        }

        @JvmStatic
        fun getIntent(context: Context, extras: ReportExtras): Intent {
            val intent = Intent(context, ReportActivity::class.java)
            val bundle = Bundle()
            bundle.putParcelable(ARG_REPORTS, extras)
            intent.putExtra("bundle", bundle)
            return intent
        }
    }

    private lateinit var binding: ActivityReportBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}