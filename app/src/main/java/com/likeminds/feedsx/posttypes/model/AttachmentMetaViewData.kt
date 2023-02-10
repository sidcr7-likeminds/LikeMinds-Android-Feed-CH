package com.likeminds.feedsx.posttypes.model

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class AttachmentMetaViewData private constructor(
    val name: String?,
    val url: String?,
    val uri: Uri?,
    val format: String?,
    val size: String?,
    val duration: String?,
    val pageCount: Int?,
    val ogTags: LinkOGTags,
) : Parcelable {

    class Builder {
        private var name: String? = null
        private var url: String? = null
        private var uri: Uri? = null
        private var format: String? = null
        private var size: String? = null
        private var duration: String? = null
        private var pageCount: Int? = null
        private var ogTags: LinkOGTags = LinkOGTags.Builder().build()

        fun name(name: String?) = apply { this.name = name }
        fun url(url: String?) = apply { this.url = url }
        fun uri(uri: Uri?) = apply { this.uri = uri }
        fun format(format: String?) = apply { this.format = format }
        fun size(size: String?) = apply { this.size = size }
        fun duration(duration: String?) = apply { this.duration = duration }
        fun pageCount(pageCount: Int?) = apply { this.pageCount = pageCount }
        fun ogTags(ogTags: LinkOGTags) = apply { this.ogTags = ogTags }

        fun build() = AttachmentMetaViewData(
            name,
            url,
            uri,
            format,
            size,
            duration,
            pageCount,
            ogTags
        )
    }

    fun toBuilder(): Builder {
        return Builder()
            .name(name)
            .url(url)
            .uri(uri)
            .format(format)
            .size(size)
            .duration(duration)
            .pageCount(pageCount)
            .ogTags(ogTags)
    }
}