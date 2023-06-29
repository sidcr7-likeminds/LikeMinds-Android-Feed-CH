package com.likeminds.feedsx.db.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.likeminds.feedsx.db.utils.DbConstants

@Entity(tableName = DbConstants.SDK_CLIENT_INFO_TABLE)
class SDKClientInfoEntity constructor(
    @ColumnInfo(name = "community")
    var community: Int,
    @ColumnInfo(name = "user")
    var user: Int,
    @ColumnInfo(name = "sdk_client_user_unique_id")
    var userUniqueId: String,
    @ColumnInfo(name = "sdk_client_uuid")
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