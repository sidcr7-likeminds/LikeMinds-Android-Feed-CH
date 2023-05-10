package com.likeminds.feedsx.di.post.detail

import com.likeminds.feedsx.post.detail.view.PostDetailActivity
import com.likeminds.feedsx.post.detail.view.PostDetailFragment
import dagger.Subcomponent

@Subcomponent(modules = [PostDetailViewModelModule::class])
interface PostDetailComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): PostDetailComponent
    }

    fun inject(postDetailFragment: PostDetailFragment)
    fun inject(postDetailActivity: PostDetailActivity)
}