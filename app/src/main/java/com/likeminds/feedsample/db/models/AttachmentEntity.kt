package com.likeminds.feedsample.db.models

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.likeminds.feedsample.db.utils.DbConstants

@Entity(tableName = DbConstants.ATTACHMENT_TABLE)
class AttachmentEntity constructor(
    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    @ColumnInfo(name = "attachment_type")
    val attachmentType: Int,
    @Embedded
    val attachmentMeta: AttachmentMetaEntity,
    @ColumnInfo(name = "post_id")
    val postId: String,
    @ColumnInfo(name = "temp_id")
    val temporaryId: Long
) {
    class Builder {
        private var id: Long = 0
        private var attachmentType: Int = 0
        private var attachmentMeta: AttachmentMetaEntity =
            AttachmentMetaEntity.Builder().build()
        private var temporaryId: Long = 0
        private var postId: String = temporaryId.toString()

        fun id(id: Long) = apply { this.id = id }
        fun attachmentType(attachmentType: Int) = apply { this.attachmentType = attachmentType }
        fun attachmentMeta(attachmentMeta: AttachmentMetaEntity) =
            apply { this.attachmentMeta = attachmentMeta }

        fun postId(postId: String) = apply { this.postId = postId }
        fun temporaryId(temporaryId: Long) = apply { this.temporaryId = temporaryId }

        fun build() = AttachmentEntity(
            id,
            attachmentType,
            attachmentMeta,
            postId,
            temporaryId
        )
    }

    fun toBuilder(): Builder {
        return Builder().id(id)
            .attachmentType(attachmentType)
            .attachmentMeta(attachmentMeta)
            .postId(postId)
            .temporaryId(temporaryId)
    }
}