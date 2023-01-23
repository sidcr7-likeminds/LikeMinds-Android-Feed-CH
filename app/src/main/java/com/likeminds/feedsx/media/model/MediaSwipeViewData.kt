package com.likeminds.feedsx.media.model

import android.net.Uri
import android.os.Parcelable
import com.likeminds.feedsx.utils.model.BaseViewType
import com.likeminds.feedsx.utils.model.ITEM_NONE
import kotlinx.parcelize.Parcelize

@Parcelize
class MediaSwipeViewData private constructor(
    var uri: Uri,
    var thumbnail: String?,
    var _viewType: Int,
    var index: Int,
    var title: String?,
    var subTitle: String?
) : Parcelable, BaseViewType {
    override val viewType: Int
        get() = _viewType

    class Builder {
        private var uri: Uri = Uri.parse("")
        private var thumbnail: String? = null
        private var _viewType: Int = ITEM_NONE
        private var index: Int = -1
        private var title: String? = null
        private var subTitle: String? = null

        fun uri(uri: Uri) = apply { this.uri = uri }
        fun thumbnail(thumbnail: String?) = apply { this.thumbnail = thumbnail }
        fun _viewType(_viewType: Int) = apply { this._viewType = _viewType }
        fun index(index: Int) = apply { this.index = index }
        fun title(title: String?) = apply { this.title = title }
        fun subTitle(subTitle: String?) = apply { this.subTitle = subTitle }

        fun build() = MediaSwipeViewData(
            uri,
            thumbnail,
            _viewType,
            index,
            title,
            subTitle
        )
    }

    fun toBuilder(): Builder {
        return Builder().uri(uri)
            .thumbnail(thumbnail)
            ._viewType(_viewType)
            .index(index)
            .title(title)
            .subTitle(subTitle)
    }
}