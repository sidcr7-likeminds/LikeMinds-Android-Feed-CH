package com.likeminds.feedsx.di.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.likeminds.feedsx.feed.viewmodel.FeedViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class FeedViewModelModule {
    @Binds
    abstract fun bindFeedViewModelFactory(factory: FeedViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @FeedViewModelKey(FeedViewModel::class)
    abstract fun bindFeedViewModel(feedViewModel: FeedViewModel): ViewModel
}