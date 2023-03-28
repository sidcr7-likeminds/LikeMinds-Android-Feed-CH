package com.likeminds.feedsx.posttypes.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class AttachmentMetaViewData private constructor(
    val name: String?,
    val url: String?,
    val format: String?,
    val size: String?,
    val duration: String?,
    val pageCount: Int?,
    val ogTags: LinkOGTagsViewData,
    var width: Int?,
    var height: Int?
) : Parcelable {

    class Builder {
        private var name: String? = null
        private var url: String? = null
        private var format: String? = null
        private var size: String? = null
        private var duration: String? = null
        private var pageCount: Int? = null
        private var ogTags: LinkOGTagsViewData = LinkOGTagsViewData.Builder().build()
        private var width: Int? = null
        private var height: Int? = null

        fun name(name: String?) = apply { this.name = name }
        fun url(url: String?) = apply { this.url = url }
        fun format(format: String?) = apply { this.format = format }
        fun size(size: String?) = apply { this.size = size }
        fun duration(duration: String?) = apply { this.duration = duration }
        fun pageCount(pageCount: Int?) = apply { this.pageCount = pageCount }
        fun ogTags(ogTags: LinkOGTagsViewData) = apply { this.ogTags = ogTags }
        fun width(width: Int?) = apply { this.width = width }
        fun height(height: Int?) = apply { this.height = height }

        fun build() = AttachmentMetaViewData(
            name,
            url,
            format,
            size,
            duration,
            pageCount,
            ogTags,
            width,
            height
        )
    }

    fun toBuilder(): Builder {
        return Builder()
            .name(name)
            .url(url)
            .format(format)
            .size(size)
            .duration(duration)
            .pageCount(pageCount)
            .ogTags(ogTags)
    }
}