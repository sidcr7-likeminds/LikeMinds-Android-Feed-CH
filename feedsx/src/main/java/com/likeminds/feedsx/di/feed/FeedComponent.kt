package com.likeminds.feedsx.di.feed

import com.likeminds.feedsx.feed.view.LMFeedFragment
import com.likeminds.feedsx.post.create.view.LMFeedCreateResourceDialog
import com.likeminds.feedsx.post.create.view.LMFeedLinkResourceDialogFragment
import dagger.Subcomponent

@Subcomponent(modules = [FeedViewModelModule::class])
interface FeedComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): FeedComponent
    }

    fun inject(lmFeedFragment: LMFeedFragment)
    fun inject(lmFeedCreateResourceDialog: LMFeedCreateResourceDialog)
    fun inject(lmFeedLinkResourceDialogFragment: LMFeedLinkResourceDialogFragment)
}