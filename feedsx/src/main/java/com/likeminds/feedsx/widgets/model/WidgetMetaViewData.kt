package com.likeminds.feedsx.widgets.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class WidgetMetaViewData private constructor(
    val body: String?,
    val coverImageUrl: String?,
    val title: String?,
    val name: String?,
    val size: Long?,
    val url: String?,
) : Parcelable {
    class Builder {
        private var body: String? = ""
        private var coverImageUrl: String? = ""
        private var title: String? = ""
        private var name: String? = null
        private var size: Long? = null
        private var url: String? = null

        fun body(body: String?) = apply { this.body = body }
        fun coverImageUrl(coverImageUrl: String?) = apply { this.coverImageUrl = coverImageUrl }
        fun title(title: String?) = apply { this.title = title }
        fun name(name: String?) = apply { this.name = name }
        fun size(size: Long?) = apply { this.size = size }
        fun url(url: String?) = apply { this.url = url }

        fun build() = WidgetMetaViewData(
            body,
            coverImageUrl,
            title,
            name,
            size,
            url
        )
    }

    fun toBuilder(): Builder {
        return Builder().body(body)
            .coverImageUrl(coverImageUrl)
            .title(title)
            .name(name)
            .size(size)
            .url(url)
    }
}