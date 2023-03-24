package com.likeminds.feedsx.db.models

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.likeminds.feedsx.db.utils.DbConstants

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
    val postId: Long,
    @ColumnInfo(name = "dynamic_type")
    val dynamicType: Int?
) {
    class Builder {
        private var id: Long = 0
        private var attachmentType: Int = 0
        private var attachmentMeta: AttachmentMetaEntity = AttachmentMetaEntity.Builder().build()
        private var postId: Long = 0
        private var dynamicType: Int? = null

        fun id(id: Long) = apply { this.id = id }
        fun attachmentType(attachmentType: Int) = apply { this.attachmentType = attachmentType }
        fun attachmentMeta(attachmentMeta: AttachmentMetaEntity) =
            apply { this.attachmentMeta = attachmentMeta }

        fun postId(postId: Long) = apply { this.postId = postId }
        fun dynamicType(dynamicType: Int?) = apply { this.dynamicType = dynamicType }

        fun build() = AttachmentEntity(
            id,
            attachmentType,
            attachmentMeta,
            postId,
            dynamicType
        )
    }

    fun toBuilder(): Builder {
        return Builder().id(id)
            .attachmentType(attachmentType)
            .attachmentMeta(attachmentMeta)
            .postId(postId)
            .dynamicType(dynamicType)
    }
}