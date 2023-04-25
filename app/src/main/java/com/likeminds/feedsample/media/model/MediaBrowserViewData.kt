package com.likeminds.feedsample.media.model

import android.os.Parcelable
import com.likeminds.feedsample.utils.model.BaseViewType
import com.likeminds.feedsample.utils.model.ITEM_MEDIA_PICKER_BROWSE
import kotlinx.parcelize.Parcelize

@Parcelize
class MediaBrowserViewData private constructor() : Parcelable, BaseViewType {
    override val viewType: Int
        get() = ITEM_MEDIA_PICKER_BROWSE

    class Builder {
        fun build() = MediaBrowserViewData()
    }

    fun toBuilder(): Builder {
        return Builder()
    }
}