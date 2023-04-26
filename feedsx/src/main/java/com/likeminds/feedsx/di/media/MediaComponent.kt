package com.likeminds.feedsx.di.media

import com.likeminds.feedsx.media.view.MediaPickerActivity
import com.likeminds.feedsx.media.view.MediaPickerDocumentFragment
import com.likeminds.feedsx.media.view.MediaPickerFolderFragment
import com.likeminds.feedsx.media.view.MediaPickerItemFragment
import dagger.Subcomponent

@Subcomponent(modules = [MediaViewModelModule::class])
interface MediaComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): MediaComponent
    }

    fun inject(mediaPickerDocumentFragment: MediaPickerDocumentFragment)
    fun inject(mediaPickerFolderFragment: MediaPickerFolderFragment)
    fun inject(mediaPickerItemFragment: MediaPickerItemFragment)
    fun inject(mediaPickerActivity: MediaPickerActivity)
}