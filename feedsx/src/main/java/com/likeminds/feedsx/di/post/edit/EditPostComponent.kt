package com.likeminds.feedsx.di.post.edit

import com.likeminds.feedsx.post.edit.view.EditPostActivity
import com.likeminds.feedsx.post.edit.view.EditPostFragment
import dagger.Subcomponent

@Subcomponent(modules = [EditPostViewModelModule::class])
interface EditPostComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): EditPostComponent
    }

    fun inject(editPostFragment: EditPostFragment)
    fun inject(editPostActivity: EditPostActivity)
}