package com.likeminds.feedsx.di.post.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.likeminds.feedsx.post.create.viewmodel.LMFeedCreatePostViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class CreatePostViewModelModule {
    @Binds
    abstract fun bindCreatePostViewModelFactory(factory: CreatePostViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @CreatePostViewModelKey(LMFeedCreatePostViewModel::class)
    abstract fun bindCreatePostViewModel(LMFeedCreatePostViewModel: LMFeedCreatePostViewModel): ViewModel
}