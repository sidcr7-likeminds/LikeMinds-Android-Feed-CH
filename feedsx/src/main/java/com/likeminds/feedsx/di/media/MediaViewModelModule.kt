package com.likeminds.feedsx.di.media

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.likeminds.feedsx.media.viewmodel.MediaViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
internal abstract class MediaViewModelModule {

    @Binds
    abstract fun bindMediaViewModelFactory(factory: MediaViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @MediaViewModelKey(MediaViewModel::class)
    abstract fun bindMediaViewModel(mediaViewModel: MediaViewModel): ViewModel
}