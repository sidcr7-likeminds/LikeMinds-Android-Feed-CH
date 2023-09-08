package com.likeminds.feedsx.di.media

import com.likeminds.feedsx.media.view.*
import dagger.Subcomponent

@Subcomponent(modules = [MediaViewModelModule::class])
interface MediaComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): MediaComponent
    }

    fun inject(lmFeedMediaPickerDocumentFragment: LMFeedMediaPickerDocumentFragment)
    fun inject(lmFeedMediaPickerFolderFragment: LMFeedMediaPickerFolderFragment)
    fun inject(lmFeedMediaPickerItemFragment: LMFeedMediaPickerItemFragment)
    fun inject(lmFeedMediaPickerActivity: LMFeedMediaPickerActivity)
}