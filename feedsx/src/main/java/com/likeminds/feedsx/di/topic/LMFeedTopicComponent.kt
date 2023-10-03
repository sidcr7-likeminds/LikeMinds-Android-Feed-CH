package com.likeminds.feedsx.di.topic

import com.likeminds.feedsx.topic.view.LMFeedTopicSelectionActivity
import com.likeminds.feedsx.topic.view.LMFeedTopicSelectionFragment
import dagger.Subcomponent

@Subcomponent(modules = [LMFeedTopicSelectionViewModelModule::class])
interface LMFeedTopicComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): LMFeedTopicComponent
    }

    fun inject(lmFeedTopicSelectionActivity: LMFeedTopicSelectionActivity)
    fun inject(lmFeedTopicSelectionFragment: LMFeedTopicSelectionFragment)
}