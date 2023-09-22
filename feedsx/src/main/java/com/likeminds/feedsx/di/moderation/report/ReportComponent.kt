package com.likeminds.feedsx.di.moderation.report

import com.likeminds.feedsx.report.view.LMFeedReportActivity
import com.likeminds.feedsx.report.view.LMFeedReportFragment
import dagger.Subcomponent

@Subcomponent(modules = [ReportViewModelModule::class])
interface ReportComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): ReportComponent
    }

    fun inject(lmFeedReportFragment: LMFeedReportFragment)
    fun inject(lmFeedReportActivity: LMFeedReportActivity)
}