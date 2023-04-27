package com.likeminds.feedsx.di.moderation.report

import com.likeminds.feedsx.report.view.ReportActivity
import com.likeminds.feedsx.report.view.ReportFragment
import dagger.Subcomponent

@Subcomponent(modules = [ReportViewModelModule::class])
interface ReportComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): ReportComponent
    }

    fun inject(reportFragment: ReportFragment)
    fun inject(reportActivity: ReportActivity)
}