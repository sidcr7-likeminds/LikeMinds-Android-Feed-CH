package com.likeminds.feedsx.db.models

import androidx.room.ColumnInfo

class SDKClientInfoEntity constructor(
    @ColumnInfo(name = "community", defaultValue = "0")
    var community: Int,
    @ColumnInfo(name = "user", defaultValue = "0")
    var user: Int,
    @ColumnInfo(name = "sdk_client_user_unique_id", defaultValue = "")
    var userUniqueId: String,
    @ColumnInfo(name = "sdk_client_uuid", defaultValue = "")
    var uuid: String
) {

    class Builder {
        private var community: Int = 0
        private var user: Int = 0
        private var userUniqueId: String = ""
        private var uuid: String = ""

        fun community(community: Int) = apply { this.community = community }
        fun user(user: Int) = apply { this.user = user }
        fun userUniqueId(userUniqueId: String) = apply { this.userUniqueId = userUniqueId }
        fun uuid(uuid: String) = apply { this.uuid = uuid }

        fun build() = SDKClientInfoEntity(community, user, userUniqueId, uuid)
    }

    fun toBuilder(): Builder {
        return Builder().user(user)
            .community(community)
            .userUniqueId(userUniqueId)
            .uuid(uuid)
    }
}