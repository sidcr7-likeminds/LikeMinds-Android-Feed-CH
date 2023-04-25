package com.likeminds.feedsampleapp.media.model

import android.os.Parcelable
import com.likeminds.feedsampleapp.utils.model.BaseViewType
import com.likeminds.feedsampleapp.utils.model.ITEM_MEDIA_PICKER_HEADER
import kotlinx.parcelize.Parcelize

@Parcelize
class MediaHeaderViewData private constructor(
    var title: String
) : Parcelable, BaseViewType {
    override val viewType: Int
        get() = ITEM_MEDIA_PICKER_HEADER

    class Builder {
        private var title: String = ""

        fun title(title: String) = apply { this.title = title }

        fun build() = MediaHeaderViewData(title)
    }

    fun toBuilder(): Builder {
        return Builder().title(title)
    }
}