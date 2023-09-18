package com.likeminds.feedsx.di.post.create

import com.likeminds.feedsx.post.create.view.*
import dagger.Subcomponent

@Subcomponent(modules = [CreatePostViewModelModule::class])
interface CreatePostComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): CreatePostComponent
    }

    fun inject(lmFeedCreatePostFragment: LMFeedCreatePostFragment)
    fun inject(lmFeedCreatePostActivity: LMFeedCreatePostActivity)
    fun inject(lmFeedDiscardResourceDialog: LMFeedDiscardResourceDialog)
    fun inject(lmFeedRemoveAttachmentDialogFragment: LMFeedRemoveAttachmentDialogFragment)
}