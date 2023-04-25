package com.likeminds.feedsample.db.models

import androidx.room.Embedded
import androidx.room.Relation

data class PostWithAttachments constructor(
    @Embedded
    val post: PostEntity,
    @Relation(
        parentColumn = "post_id",
        entityColumn = "post_id"
    )
    val attachments: List<AttachmentEntity>
)