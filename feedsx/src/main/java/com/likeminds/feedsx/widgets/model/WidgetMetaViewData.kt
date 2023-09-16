package com.likeminds.feedsx.widgets.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class WidgetMetaViewData private constructor(
    val body: String,
    val coverImageUrl: String,
    val title: String
) : Parcelable {
    class Builder {
        private var body: String = ""
        private var coverImageUrl: String = ""
        private var title: String = ""

        fun body(body: String) = apply { this.body = body }
        fun coverImageUrl(coverImageUrl: String) = apply { this.coverImageUrl = coverImageUrl }
        fun title(title: String) = apply { this.title = title }

        fun build() = WidgetMetaViewData(
            body,
            coverImageUrl,
            title
        )
    }

    fun toBuilder(): Builder {
        return Builder().body(body)
            .coverImageUrl(coverImageUrl)
            .title(title)
    }
}