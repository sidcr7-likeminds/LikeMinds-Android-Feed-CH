package com.likeminds.feedsx.db.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.likeminds.feedsx.db.utils.DbConstants

@Entity(tableName = DbConstants.USER_TABLE, primaryKeys = ["id", "user_unique_id"])
class UserEntity constructor(
    @ColumnInfo(name = "id")
    var id: Int,
    @ColumnInfo(name = "image_url")
    var imageUrl: String,
    @ColumnInfo(name = "is_guest")
    var isGuest: Boolean,
    @ColumnInfo(name = "name")
    var name: String,
    @ColumnInfo(name = "updated_at")
    var updatedAt: Long,
    @ColumnInfo(name = "custom_title")
    var customTitle: String?,
    @ColumnInfo(name = "is_deleted")
    var isDeleted: Boolean?,
    @ColumnInfo(name = "user_unique_id")
    var userUniqueId: String,
    @ColumnInfo(name = "state")
    var state: Int,
    @ColumnInfo(name = "is_owner")
    var isOwner: Boolean
) {
    class Builder {
        private var id: Int = 0
        private var imageUrl: String = ""
        private var isGuest: Boolean = false
        private var name: String = ""
        private var updatedAt: Long = 0L
        private var customTitle: String? = null
        private var isDeleted: Boolean? = null
        private var userUniqueId: String = ""
        private var state: Int = -1
        private var isOwner: Boolean = false

        fun id(id: Int) = apply { this.id = id }
        fun imageUrl(imageUrl: String) = apply { this.imageUrl = imageUrl }
        fun isGuest(isGuest: Boolean) = apply { this.isGuest = isGuest }
        fun name(name: String) = apply { this.name = name }
        fun updatedAt(updatedAt: Long) = apply { this.updatedAt = updatedAt }
        fun customTitle(customTitle: String?) = apply { this.customTitle = customTitle }
        fun isDeleted(isDeleted: Boolean?) = apply { this.isDeleted = isDeleted }
        fun userUniqueId(userUniqueId: String) = apply { this.userUniqueId = userUniqueId }
        fun state(state: Int) = apply { this.state = state }
        fun isOwner(isOwner: Boolean) = apply { this.isOwner = isOwner }

        fun build() =
            UserEntity(
                id,
                imageUrl,
                isGuest,
                name,
                updatedAt,
                customTitle,
                isDeleted,
                userUniqueId,
                state,
                isOwner
            )
    }

    fun toBuilder(): Builder {
        return Builder().id(id)
            .imageUrl(imageUrl)
            .isGuest(isGuest)
            .name(name)
            .updatedAt(updatedAt)
            .customTitle(customTitle)
            .isDeleted(isDeleted)
            .userUniqueId(userUniqueId)
            .state(state)
            .isOwner(isOwner)
    }
}