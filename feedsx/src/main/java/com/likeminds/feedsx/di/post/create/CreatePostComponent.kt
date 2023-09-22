package com.likeminds.feedsx.di.post.create

import com.likeminds.feedsx.post.create.view.LMFeedCreatePostActivity
import com.likeminds.feedsx.post.create.view.LMFeedCreatePostFragment
import dagger.Subcomponent

@Subcomponent(modules = [CreatePostViewModelModule::class])
interface CreatePostComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): CreatePostComponent
    }

    fun inject(lmFeedCreatePostFragment: LMFeedCreatePostFragment)
    fun inject(lmFeedCreatePostActivity: LMFeedCreatePostActivity)
}