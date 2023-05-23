package com.likeminds.feedsx.media.model

import com.likeminds.feedsx.utils.model.BaseViewType
import com.likeminds.feedsx.utils.model.ITEM_MEDIA_PICKER_BROWSE

class MediaBrowserViewData private constructor() : BaseViewType {
    override val viewType: Int
        get() = ITEM_MEDIA_PICKER_BROWSE

    class Builder {
        fun build() = MediaBrowserViewData()
    }

    fun toBuilder(): Builder {
        return Builder()
    }
}