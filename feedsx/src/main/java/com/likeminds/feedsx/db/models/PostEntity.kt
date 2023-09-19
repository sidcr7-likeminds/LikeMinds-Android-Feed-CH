package com.likeminds.feedsx.db.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.likeminds.feedsx.db.utils.DbConstants

@Entity(tableName = DbConstants.POST_TABLE, primaryKeys = ["temp_id", "post_id"])
class PostEntity constructor(
    @ColumnInfo(name = "temp_id")
    var temporaryId: Long,
    @ColumnInfo(name = "text")
    var text: String?,
    @ColumnInfo(name = "thumbnail")
    var thumbnail: String?,
    @ColumnInfo(name = "uuid")
    var uuid: String,
    @ColumnInfo(name = "is_posted")
    var isPosted: Boolean,
    @ColumnInfo(name = "post_id")
    var postId: String,
    @ColumnInfo(name = "heading")
    var heading: String?,
    @ColumnInfo(name = "on_behalf_of_uuid")
    var onBehalfOfUUID: String?
) {
    class Builder {
        private var temporaryId: Long = -1
        private var text: String? = null
        private var thumbnail: String? = null
        private var uuid: String = ""
        private var isPosted: Boolean = false
        private var postId: String = temporaryId.toString()
        private var heading: String? = null
        private var onBehalfOfUUID: String? = null

        fun temporaryId(temporaryId: Long) = apply { this.temporaryId = temporaryId }
        fun text(text: String?) = apply { this.text = text }
        fun thumbnail(thumbnail: String?) = apply { this.thumbnail = thumbnail }
        fun uuid(uuid: String) = apply { this.uuid = uuid }
        fun isPosted(isPosted: Boolean) = apply { this.isPosted = isPosted }
        fun postId(postId: String) = apply { this.postId = postId }
        fun heading(heading: String?) = apply { this.heading = heading }
        fun onBehalfOfUUID(onBehalfOfUUID: String?) = apply { this.onBehalfOfUUID = onBehalfOfUUID }

        fun build() =
            PostEntity(
                temporaryId,
                text,
                thumbnail,
                uuid,
                isPosted,
                postId,
                heading,
                onBehalfOfUUID
            )
    }

    fun toBuilder(): Builder {
        return Builder().temporaryId(temporaryId)
            .text(text)
            .thumbnail(thumbnail)
            .uuid(uuid)
            .isPosted(isPosted)
            .postId(postId)
            .heading(heading)
            .onBehalfOfUUID(onBehalfOfUUID)
    }
}