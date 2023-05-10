package com.likeminds.feedsx.di.post.create

import com.likeminds.feedsx.post.create.view.CreatePostActivity
import com.likeminds.feedsx.post.create.view.CreatePostFragment
import dagger.Subcomponent

@Subcomponent(modules = [CreatePostViewModelModule::class])
interface CreatePostComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): CreatePostComponent
    }

    fun inject(createPostFragment: CreatePostFragment)
    fun inject(createPostActivity: CreatePostActivity)
}