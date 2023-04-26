package com.likeminds.feedsx.di.notificationfeed

import com.likeminds.feedsx.notificationfeed.view.NotificationFeedActivity
import com.likeminds.feedsx.notificationfeed.view.NotificationFeedFragment
import dagger.Subcomponent

@Subcomponent(modules = [NotificationFeedViewModelModule::class])
interface NotificationFeedComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): NotificationFeedComponent
    }

    fun inject(likesFragment: NotificationFeedFragment)
    fun inject(likesActivity: NotificationFeedActivity)
}