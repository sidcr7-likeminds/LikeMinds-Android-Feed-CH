package com.likeminds.feedsx.di.topic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.likeminds.feedsx.topic.viewmodel.LMFeedTopicSelectionViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class LMFeedTopicSelectionViewModelModule {

    @Binds
    abstract fun bindLMFeedTopicSelectionViewModelFactory(factory: LMFeedTopicSelectionViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @LMFeedTopicSelectionViewModelKey(LMFeedTopicSelectionViewModel::class)
    abstract fun bindLMFeedTopicSelectionViewModel(lmFeedTopicSelectionViewModel: LMFeedTopicSelectionViewModel): ViewModel
}