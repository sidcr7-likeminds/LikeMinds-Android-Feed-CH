package com.likeminds.feedsx.di.likes

import com.likeminds.feedsx.likes.view.LikesActivity
import com.likeminds.feedsx.likes.view.LikesFragment
import dagger.Subcomponent

@Subcomponent(modules = [LikesViewModelModule::class])
interface LikesComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): LikesComponent
    }

    fun inject(likesFragment: LikesFragment)
    fun inject(likesActivity: LikesActivity)
}