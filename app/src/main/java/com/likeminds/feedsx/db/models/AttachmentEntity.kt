package com.likeminds.feedsx.db.models

import androidx.room.Entity
import androidx.room.ForeignKey
import com.likeminds.feedsx.db.utils.DbConstants

@Entity(tableName = DbConstants.ATTACHMENT_TABLE, foreignKeys = [
    ForeignKey(
        entity = PostEntity::class,
        parentColumns = arrayOf("remoteId"),
        childColumns = arrayOf("listId"),
        onDelete = ForeignKey.CASCADE
    )
])
class AttachmentEntity {
}