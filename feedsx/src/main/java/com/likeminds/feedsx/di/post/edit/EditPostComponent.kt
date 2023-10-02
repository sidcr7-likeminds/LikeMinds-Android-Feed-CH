package com.likeminds.feedsx.di.post.edit

import com.likeminds.feedsx.post.edit.view.LMFeedEditPostActivity
import com.likeminds.feedsx.post.edit.view.LMFeedEditPostFragment
import dagger.Subcomponent

@Subcomponent(modules = [EditPostViewModelModule::class])
interface EditPostComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): EditPostComponent
    }

    fun inject(LMFeedEditPostFragment: LMFeedEditPostFragment)
    fun inject(LMFeedEditPostActivity: LMFeedEditPostActivity)
}