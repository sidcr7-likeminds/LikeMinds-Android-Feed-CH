package com.likeminds.feedsampleapp.db.models

import androidx.room.Embedded
import androidx.room.Relation

data class UserWithRights constructor(
    @Embedded
    val user: UserEntity,
    @Relation(
        parentColumn = "user_unique_id",
        entityColumn = "user_unique_id"
    )
    val memberRights: List<MemberRightsEntity>
)