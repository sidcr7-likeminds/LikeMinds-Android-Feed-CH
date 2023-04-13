package com.likeminds.feedsx.db.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.likeminds.feedsx.db.utils.DbConstants

@Entity(tableName = DbConstants.MEMBER_RIGHTS_TABLE)
class MemberRightsEntity constructor(
    @ColumnInfo(name = "id")
    @PrimaryKey
    val id: Int,
    @ColumnInfo(name = "is_locked")
    val isLocked: Boolean?,
    @ColumnInfo(name = "is_selected")
    val isSelected: Boolean,
    @ColumnInfo(name = "state")
    val state: Int,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "subtitle")
    val subtitle: String?,
    @ColumnInfo(name = "user_unique_id")
    var userUniqueId: String
) {
    class Builder {
        private var id: Int = 0
        private var isLocked: Boolean? = null
        private var isSelected: Boolean = false
        private var state: Int = 0
        private var title: String = ""
        private var subtitle: String? = null
        private var userUniqueId: String = ""

        fun id(id: Int) = apply { this.id = id }
        fun isLocked(isLocked: Boolean?) = apply { this.isLocked = isLocked }
        fun isSelected(isSelected: Boolean) = apply { this.isSelected = isSelected }
        fun state(state: Int) = apply { this.state = state }
        fun title(title: String) = apply { this.title = title }
        fun subtitle(subtitle: String?) = apply { this.subtitle = subtitle }
        fun userUniqueId(userUniqueId: String) = apply { this.userUniqueId = userUniqueId }

        fun build() = MemberRightsEntity(
            id,
            isLocked,
            isSelected,
            state,
            title,
            subtitle,
            userUniqueId
        )
    }

    fun toBuilder(): Builder {
        return Builder().id(id)
            .isLocked(isLocked)
            .isSelected(isSelected)
            .state(state)
            .title(title)
            .subtitle(subtitle)
            .userUniqueId(userUniqueId)
    }
}