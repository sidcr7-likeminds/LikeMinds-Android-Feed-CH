package com.likeminds.feedsx.likes.model

import android.os.Parcelable
import com.likeminds.feedsx.posttypes.model.UserViewData
import com.likeminds.feedsx.utils.model.BaseViewType
import com.likeminds.feedsx.utils.model.ITEM_LIKES_SCREEN
import kotlinx.parcelize.Parcelize

@Parcelize
class LikeViewData private constructor(
    val id: String,
    val userId: String,
    val createdAt: Long,
    val updatedAt: Long,
    val user: UserViewData
) : Parcelable, BaseViewType {

    override val viewType: Int
        get() = ITEM_LIKES_SCREEN

    class Builder {
        private var id: String = ""
        private var userId: String = ""
        private var createdAt: Long = 0
        private var updatedAt: Long = 0
        private var user: UserViewData = UserViewData.Builder().build()

        fun id(id: String) = apply { this.id = id }
        fun userId(userId: String) = apply { this.userId = userId }
        fun createdAt(createdAt: Long) = apply { this.createdAt = createdAt }
        fun updatedAt(updatedAt: Long) = apply { this.updatedAt = updatedAt }
        fun user(user: UserViewData) = apply { this.user = user }

        fun build() = LikeViewData(
            id,
            userId,
            createdAt,
            updatedAt,
            user
        )
    }

    fun toBuilder(): Builder {
        return Builder().id(id)
            .userId(userId)
            .createdAt(createdAt)
            .updatedAt(updatedAt)
            .user(user)
    }
}