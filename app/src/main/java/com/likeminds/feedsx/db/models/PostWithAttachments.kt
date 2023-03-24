package com.likeminds.feedsx.db.models

import androidx.room.Embedded
import androidx.room.Relation

data class PostWithAttachments constructor(
    @Embedded
    val post: PostEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "post_id"
    )
    val attachments: List<AttachmentEntity>
)