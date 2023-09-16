package com.likeminds.feedsx.posttypes.model

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class AttachmentMetaViewData private constructor(
    val name: String?,
    val url: String?,
    val format: String?,
    val size: Long?,
    val duration: Int?,
    val pageCount: Int?,
    val ogTags: LinkOGTagsViewData,
    val width: Int?,
    val height: Int?,
    val uri: Uri?,
    val coverImageUrl: String?,
    val title: String?,
    val body: String?,
    val entityId: String?
) : Parcelable {
    class Builder {
        private var name: String? = null
        private var url: String? = null
        private var format: String? = null
        private var size: Long? = null
        private var duration: Int? = null
        private var pageCount: Int? = null
        private var ogTags: LinkOGTagsViewData = LinkOGTagsViewData.Builder().build()
        private var width: Int? = null
        private var height: Int? = null
        private var uri: Uri? = null
        private var coverImageUrl: String? = null
        private var title: String? = null
        private var body: String? = null
        private var entityId: String? = null

        fun name(name: String?) = apply { this.name = name }
        fun url(url: String?) = apply { this.url = url }
        fun format(format: String?) = apply { this.format = format }
        fun size(size: Long?) = apply { this.size = size }
        fun duration(duration: Int?) = apply { this.duration = duration }
        fun pageCount(pageCount: Int?) = apply { this.pageCount = pageCount }
        fun ogTags(ogTags: LinkOGTagsViewData) = apply { this.ogTags = ogTags }
        fun width(width: Int?) = apply { this.width = width }
        fun height(height: Int?) = apply { this.height = height }
        fun uri(uri: Uri?) = apply { this.uri = uri }
        fun coverImageUrl(coverImageUrl: String?) = apply { this.coverImageUrl = coverImageUrl }
        fun title(title: String?) = apply { this.title = title }
        fun body(body: String?) = apply { this.body = body }
        fun entityId(entityId: String?) = apply { this.entityId = entityId }

        fun build() = AttachmentMetaViewData(
            name,
            url,
            format,
            size,
            duration,
            pageCount,
            ogTags,
            width,
            height,
            uri,
            coverImageUrl,
            title,
            body,
            entityId
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
            .width(width)
            .height(height)
            .uri(uri)
            .coverImageUrl(coverImageUrl)
            .title(title)
            .body(body)
            .entityId(entityId)
    }
}