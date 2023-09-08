package com.likeminds.feedsx.di.notificationfeed

import com.likeminds.feedsx.notificationfeed.view.LMFeedNotificationFeedActivity
import com.likeminds.feedsx.notificationfeed.view.LMFeedNotificationFeedFragment
import dagger.Subcomponent

@Subcomponent(modules = [NotificationFeedViewModelModule::class])
interface NotificationFeedComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): NotificationFeedComponent
    }

    fun inject(likesFragment: LMFeedNotificationFeedFragment)
    fun inject(likesActivity: LMFeedNotificationFeedActivity)
}