package com.likeminds.feedsx.di.post.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.likeminds.feedsx.post.detail.viewmodel.PostDetailViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class PostDetailViewModelModule {
    @Binds
    abstract fun bindPostDetailViewModelFactory(factory: PostDetailViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @PostDetailViewModelKey(PostDetailViewModel::class)
    abstract fun bindPostDetailViewModel(postDetailViewModel: PostDetailViewModel): ViewModel

}