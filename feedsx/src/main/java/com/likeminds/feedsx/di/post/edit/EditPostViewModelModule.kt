package com.likeminds.feedsx.di.post.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.likeminds.feedsx.di.post.create.CreatePostViewModelKey
import com.likeminds.feedsx.post.create.viewmodel.CreatePostViewModel
import com.likeminds.feedsx.post.edit.viewmodel.EditPostViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class EditPostViewModelModule {
    @Binds
    abstract fun bindEditPostViewModelFactory(factory: EditPostViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @CreatePostViewModelKey(CreatePostViewModel::class)
    abstract fun bindEditPostViewModel(createPostViewModel: EditPostViewModel): ViewModel

}