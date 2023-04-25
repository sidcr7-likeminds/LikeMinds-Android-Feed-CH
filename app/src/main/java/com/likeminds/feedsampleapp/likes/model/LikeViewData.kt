package com.likeminds.feedsampleapp.likes.model

import android.os.Parcelable
import com.likeminds.feedsampleapp.posttypes.model.UserViewData
import com.likeminds.feedsampleapp.utils.model.BaseViewType
import com.likeminds.feedsampleapp.utils.model.ITEM_LIKES_SCREEN
import kotlinx.parcelize.Parcelize

@Parcelize
class LikeViewData private constructor(
    var id: String,
    var userId: String,
    var createdAt: Long,
    var updatedAt: Long,
    var user: UserViewData
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