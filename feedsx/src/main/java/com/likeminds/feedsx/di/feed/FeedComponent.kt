package com.likeminds.feedsx.di.feed

import com.likeminds.feedsx.feed.view.FeedFragment
import dagger.Subcomponent

@Subcomponent(modules = [FeedViewModelModule::class])
interface FeedComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): FeedComponent
    }

    fun inject(feedFragment: FeedFragment)
}