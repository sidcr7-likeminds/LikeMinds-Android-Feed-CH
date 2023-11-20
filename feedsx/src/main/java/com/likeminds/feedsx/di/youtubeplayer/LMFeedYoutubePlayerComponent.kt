package com.likeminds.feedsx.di.youtubeplayer

import com.likeminds.feedsx.youtubeplayer.view.LMFeedYoutubePlayerActivity
import com.likeminds.feedsx.youtubeplayer.view.LMFeedYoutubePlayerFragment
import dagger.Subcomponent

@Subcomponent(modules = [LMFeedYoutubePlayerViewModelModule::class])
interface LMFeedYoutubePlayerComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): LMFeedYoutubePlayerComponent
    }

    fun inject(lmFeedYoutubePlayerActivity: LMFeedYoutubePlayerActivity)
    fun inject(lmFeedYoutubePlayerFragment: LMFeedYoutubePlayerFragment)
}