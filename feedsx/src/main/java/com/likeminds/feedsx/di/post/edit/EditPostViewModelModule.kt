package com.likeminds.feedsx.di.post.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
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
    @EditPostViewModelKey(EditPostViewModel::class)
    abstract fun bindEditPostViewModel(editPostViewModel: EditPostViewModel): ViewModel
}