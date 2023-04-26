package com.likeminds.feedsx.di.notificationfeed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.likeminds.feedsx.notificationfeed.viewmodel.NotificationFeedViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class NotificationFeedViewModelModule {

    @Binds
    abstract fun bindNotificationFeedViewModelFactory(factory: NotificationFeedViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @NotificationFeedViewModelKey(NotificationFeedViewModel::class)
    abstract fun bindNotificationFeedViewModel(notificationFeedViewModel: NotificationFeedViewModel): ViewModel
}