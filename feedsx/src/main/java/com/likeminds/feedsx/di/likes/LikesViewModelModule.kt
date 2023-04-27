package com.likeminds.feedsx.di.likes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.likeminds.feedsx.likes.viewmodel.LikesViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class LikesViewModelModule {

    @Binds
    abstract fun bindLikeScreenViewModelFactory(factory: LikesViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @LikesViewModelKey(LikesViewModel::class)
    abstract fun bindLikeScreenViewModel(likesViewModel: LikesViewModel): ViewModel
}