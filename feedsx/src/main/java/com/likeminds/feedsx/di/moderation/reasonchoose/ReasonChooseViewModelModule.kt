package com.likeminds.feedsx.di.moderation.reasonchoose

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.likeminds.feedsx.delete.viewmodel.ReasonChooseViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ReasonChooseViewModelModule {
    @Binds
    abstract fun bindReasonChooseViewModelFactory(factory: ReasonChooseViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ReasonChooseViewModelKey(ReasonChooseViewModel::class)
    abstract fun bindReasonChooseViewModel(reasonChooseViewModel: ReasonChooseViewModel): ViewModel
}