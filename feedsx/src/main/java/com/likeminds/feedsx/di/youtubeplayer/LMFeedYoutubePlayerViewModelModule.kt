package com.likeminds.feedsx.di.youtubeplayer

import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module

@Module
abstract class LMFeedYoutubePlayerViewModelModule {

    @Binds
    abstract fun bindLMFeedYoutubePlayerViewModelFactory(factory: LMFeedYoutubePlayerViewModelFactory): ViewModelProvider.Factory
}