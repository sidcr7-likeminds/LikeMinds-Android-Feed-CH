package com.likeminds.feedsx.di.moderation.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.likeminds.feedsx.report.viewmodel.ReportViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ReportViewModelModule {

    @Binds
    abstract fun bindReportViewModelFactory(factory: ReportViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ReportViewModelKey(ReportViewModel::class)
    abstract fun bindReportViewModel(reportViewModel: ReportViewModel): ViewModel
}