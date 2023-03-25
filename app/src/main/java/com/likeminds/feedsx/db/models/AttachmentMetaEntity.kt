package com.likeminds.feedsx.db.models

import androidx.room.ColumnInfo

class AttachmentMetaEntity constructor(
    @ColumnInfo(name = "name")
    val name: String?,
    @ColumnInfo(name = "url")
    val url: String?,
    @ColumnInfo(name = "uri")
    val uri: String?,
    @ColumnInfo(name = "size")
    val size: Long?,
    @ColumnInfo(name = "duration")
    val duration: Int?,
    @ColumnInfo(name = "page_count")
    val pageCount: Int?,
    @ColumnInfo(name = "format")
    val format: String?
) {
    class Builder {
        private var name: String? = null
        private var url: String? = null
        private var uri: String? = null
        private var size: Long? = null
        private var duration: Int? = null
        private var pageCount: Int? = null
        private var format: String? = null

        fun name(name: String?) = apply { this.name = name }
        fun url(url: String?) = apply { this.url = url }
        fun uri(uri: String?) = apply { this.uri = uri }
        fun size(size: Long?) = apply { this.size = size }
        fun duration(duration: Int?) = apply { this.duration = duration }
        fun pageCount(pageCount: Int?) = apply { this.pageCount = pageCount }
        fun format(format: String?) = apply { this.format = format }

        fun build() =
            AttachmentMetaEntity(
                name,
                url,
                uri,
                size,
                duration,
                pageCount,
                format
            )
    }

    fun toBuilder(): Builder {
        return Builder().name(name)
            .url(url)
            .uri(uri)
            .size(size)
            .duration(duration)
            .pageCount(pageCount)
            .format(format)
    }
}