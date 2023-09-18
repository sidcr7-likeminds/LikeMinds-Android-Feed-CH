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
    val entityId: String?,
    val thumbnailUrl: String?,
    val thumbnailAWSFolderPath: String?,
    val thumbnailLocalFilePath: String?,
    val coverImageUrl: String?,
    val body: String?,
    val title: String?,
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
        private var entityId: String? = null
        private var thumbnailUrl: String? = null
        private var thumbnailAWSFolderPath: String? = null
        private var thumbnailLocalFilePath: String? = null
        private var coverImageUrl: String? = null
        private var body: String? = null
        private var title: String? = null

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
        fun entityId(entityId: String?) = apply { this.entityId = entityId }
        fun thumbnailUrl(thumbnailUrl: String?) = apply { this.thumbnailUrl = thumbnailUrl }
        fun thumbnailAWSFolderPath(thumbnailAWSFolderPath: String?) =
            apply { this.thumbnailAWSFolderPath = thumbnailAWSFolderPath }

        fun thumbnailLocalFilePath(thumbnailLocalFilePath: String?) =
            apply { this.thumbnailLocalFilePath = thumbnailLocalFilePath }

        fun coverImageUrl(coverImageUrl: String?) = apply { this.coverImageUrl = coverImageUrl }
        fun body(body: String?) = apply { this.body = body }
        fun title(title: String?) = apply { this.title = title }

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
            entityId,
            thumbnailUrl,
            thumbnailAWSFolderPath,
            thumbnailLocalFilePath,
            coverImageUrl,
            body,
            title
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
            .entityId(entityId)
            .thumbnailUrl(thumbnailUrl)
            .thumbnailAWSFolderPath(thumbnailAWSFolderPath)
            .thumbnailLocalFilePath(thumbnailLocalFilePath)
            .coverImageUrl(coverImageUrl)
            .body(body)
            .title(title)
    }
}