package com.likeminds.feedsx.notificationfeed.model

import android.os.Parcelable
import com.likeminds.feedsx.overflowmenu.model.OverflowMenuItemViewData
import com.likeminds.feedsx.posttypes.model.UserViewData
import com.likeminds.feedsx.utils.model.BaseViewType
import com.likeminds.feedsx.utils.model.ITEM_NOTIFICATION_FEED
import kotlinx.parcelize.Parcelize

@Parcelize
class NotificationFeedViewData private constructor(
    val id: String,
    val isRead: Boolean,
    val actionBy: String,
    val actionOn: List<String>,
    val communityId: Int,
    @NotificationEntityType
    val entityType: String,
    val entityId: String,
    val action: String,
    val cta: String,
    val activityMessage: String,
    val user: UserViewData,
    val menuItems: List<OverflowMenuItemViewData>,
    val createdAt: Long,
    val updatedAt: Long
) : Parcelable, BaseViewType {

    override val viewType: Int
        get() = ITEM_NOTIFICATION_FEED

    class Builder {
        private var id: String = ""
        private var isRead: Boolean = false
        private var actionBy: String = ""
        private var actionOn: List<String> = listOf()
        private var communityId: Int = 0

        @NotificationEntityType
        private var entityType: String = POST
        private var entityId: String = ""
        private var action: String = ""
        private var cta: String = ""
        private var activityMessage: String = ""
        private var user: UserViewData = UserViewData.Builder().build()
        private var menuItems: List<OverflowMenuItemViewData> = listOf()
        private var createdAt: Long = 0
        private var updatedAt: Long = 0

        fun id(id: String) = apply { this.id = id }
        fun isRead(isRead: Boolean) = apply { this.isRead = isRead }
        fun actionBy(actionBy: String) = apply { this.actionBy = actionBy }
        fun actionOn(actionOn: List<String>) = apply { this.actionOn = actionOn }
        fun communityId(communityId: Int) = apply { this.communityId = communityId }
        fun entityType(@NotificationEntityType entityType: String) =
            apply { this.entityType = entityType }

        fun entityId(entityId: String) = apply { this.entityId = entityId }
        fun action(action: String) = apply { this.action = action }
        fun cta(cta: String) = apply { this.cta = cta }
        fun activityMessage(activityMessage: String) =
            apply { this.activityMessage = activityMessage }

        fun user(user: UserViewData) = apply { this.user = user }
        fun menuItems(menuItems: List<OverflowMenuItemViewData>) =
            apply { this.menuItems = menuItems }

        fun createdAt(createdAt: Long) = apply { this.createdAt = createdAt }
        fun updatedAt(updatedAt: Long) = apply { this.updatedAt = updatedAt }

        fun build() = NotificationFeedViewData(
            id,
            isRead,
            actionBy,
            actionOn,
            communityId,
            entityType,
            entityId,
            action,
            cta,
            activityMessage,
            user,
            menuItems,
            createdAt,
            updatedAt
        )
    }

    fun toBuilder(): Builder {
        return Builder().id(id)
            .isRead(isRead)
            .actionBy(actionBy)
            .actionOn(actionOn)
            .communityId(communityId)
            .entityType(entityType)
            .entityId(entityId)
            .action(action)
            .cta(cta)
            .activityMessage(activityMessage)
            .user(user)
            .menuItems(menuItems)
            .createdAt(createdAt)
            .updatedAt(updatedAt)
    }
}