package com.likeminds.feedsampleapp.media.model

import android.os.Parcelable
import com.likeminds.feedsampleapp.utils.model.BaseViewType
import com.likeminds.feedsampleapp.utils.model.ITEM_MEDIA_PICKER_BROWSE
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