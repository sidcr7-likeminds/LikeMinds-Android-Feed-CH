package com.likeminds.feedsx.di.post.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.likeminds.feedsx.post.edit.viewmodel.LMFeedEditPostViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class EditPostViewModelModule {
    @Binds
    abstract fun bindEditPostViewModelFactory(factory: EditPostViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @EditPostViewModelKey(LMFeedEditPostViewModel::class)
    abstract fun bindEditPostViewModel(LMFeedEditPostViewModel: LMFeedEditPostViewModel): ViewModel
}