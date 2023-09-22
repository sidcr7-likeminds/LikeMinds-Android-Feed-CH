package com.likeminds.feedsx.di.moderation.reasonchoose

import com.likeminds.feedsx.delete.view.LMFeedReasonChooseDialog
import dagger.Subcomponent

@Subcomponent(modules = [ReasonChooseViewModelModule::class])
interface ReasonChooseComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): ReasonChooseComponent
    }

    fun inject(LMFeedReasonChooseDialog: LMFeedReasonChooseDialog)
}