package com.likeminds.feedsx.db.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.likeminds.feedsx.db.utils.DbConstants

@Entity(tableName = DbConstants.POST_TABLE)
class PostEntity constructor(
    @PrimaryKey
    @ColumnInfo(name = "id")
    var id: Long,
    @ColumnInfo(name = "text")
    var text: String?,
    @ColumnInfo(name = "thumbnail")
    var thumbnail: String,
    @ColumnInfo(name = "uuid")
    var uuid: String
) {
    class Builder {
        private var id: Long = 0
        private var text: String? = null
        private var thumbnail: String = ""
        private var uuid: String = ""

        fun id(id: Long) = apply { this.id = id }
        fun text(text: String?) = apply { this.text = text }
        fun thumbnail(thumbnail: String) = apply { this.thumbnail = thumbnail }
        fun uuid(uuid: String) = apply { this.uuid = uuid }

        fun build() =
            PostEntity(
                id,
                text,
                thumbnail,
                uuid
            )
    }

    fun toBuilder(): Builder {
        return Builder().id(id)
            .text(text)
            .thumbnail(thumbnail)
            .uuid(uuid)
    }
}