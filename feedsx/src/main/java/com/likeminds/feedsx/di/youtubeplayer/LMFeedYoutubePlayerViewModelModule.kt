package com.likeminds.feedsx.di.youtubeplayer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.likeminds.feedsx.posttypes.viewmodel.LMFeedYoutubePlayerViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class LMFeedYoutubePlayerViewModelModule {

    @Binds
    abstract fun bindLMFeedYoutubePlayerViewModelFactory(factory: LMFeedYoutubePlayerViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @LMFeedYoutubePlayerViewModelKey(LMFeedYoutubePlayerViewModel::class)
    abstract fun bindLMFeedYoutubePlayerViewModel(lmFeedYoutubePlayerViewModel: LMFeedYoutubePlayerViewModel): ViewModel
}