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
    val size: String?,
    @ColumnInfo(name = "duration")
    val duration: String?,
    @ColumnInfo(name = "page_count")
    val pageCount: Int?
) {
    class Builder {
        private var name: String? = null
        private var url: String? = null
        private var uri: String? = null
        private var size: String? = null
        private var duration: String? = null
        private var pageCount: Int? = null

        fun name(name: String?) = apply { this.name = name }
        fun url(url: String?) = apply { this.url = url }
        fun uri(uri: String?) = apply { this.uri = uri }
        fun size(size: String?) = apply { this.size = size }
        fun duration(duration: String?) = apply { this.duration = duration }
        fun pageCount(pageCount: Int?) = apply { this.pageCount = pageCount }

        fun build() =
            AttachmentMetaEntity(
                name,
                url,
                uri,
                size,
                duration,
                pageCount
            )
    }

    fun toBuilder(): Builder {
        return Builder().name(name)
            .url(url)
            .uri(uri)
            .size(size)
            .duration(duration)
            .pageCount(pageCount)
    }
}