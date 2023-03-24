package com.likeminds.feedsx.db.models

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.likeminds.feedsx.db.utils.DbConstants.ATTACHMENT_META_TABLE

@Entity(tableName = ATTACHMENT_META_TABLE)
class AttachmentMetaEntity constructor(
    @ColumnInfo(name = "attachment_meta_id")
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    @ColumnInfo(name = "name")
    val name: String?,
    @ColumnInfo(name = "url")
    val url: String?,
    @ColumnInfo(name = "uri")
    val uri: String?,
    @ColumnInfo(name = "format")
    val format: String?,
    @ColumnInfo(name = "size")
    val size: String?,
    @ColumnInfo(name = "duration")
    val duration: String?,
    @ColumnInfo(name = "page_count")
    val pageCount: Int?,
    @Embedded
    val ogTags: LinkOGTagsEntity?,
) {
    class Builder {
        private var id: Long = 0
        private var name: String? = null
        private var url: String? = null
        private var uri: String? = null
        private var format: String? = null
        private var size: String? = null
        private var duration: String? = null
        private var pageCount: Int? = null
        private var ogTags: LinkOGTagsEntity? = null

        fun id(id: Long) = apply { this.id = id }
        fun name(name: String?) = apply { this.name = name }
        fun url(url: String?) = apply { this.url = url }
        fun uri(uri: String?) = apply { this.uri = uri }
        fun format(format: String?) = apply { this.format = format }
        fun size(size: String?) = apply { this.size = size }
        fun duration(duration: String?) = apply { this.duration = duration }
        fun pageCount(pageCount: Int?) = apply { this.pageCount = pageCount }
        fun ogTags(ogTags: LinkOGTagsEntity?) = apply { this.ogTags = ogTags }

        fun build() =
            AttachmentMetaEntity(
                id,
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
        return Builder().id(id)
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