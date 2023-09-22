package com.likeminds.feedsx.di.likes

import com.likeminds.feedsx.likes.view.LMFeedLikesActivity
import com.likeminds.feedsx.likes.view.LMFeedLikesFragment
import dagger.Subcomponent

@Subcomponent(modules = [LikesViewModelModule::class])
interface LikesComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): LikesComponent
    }

    fun inject(lmFeedLikesFragment: LMFeedLikesFragment)
    fun inject(lmFeedLikesActivity: LMFeedLikesActivity)
}