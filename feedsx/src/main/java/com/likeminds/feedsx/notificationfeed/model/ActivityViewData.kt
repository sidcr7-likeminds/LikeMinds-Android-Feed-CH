package com.likeminds.feedsx.notificationfeed.model

import android.os.Parcelable
import com.likeminds.feedsx.posttypes.model.UserViewData
import com.likeminds.feedsx.utils.model.BaseViewType
import com.likeminds.feedsx.utils.model.ITEM_NOTIFICATION_FEED
import kotlinx.parcelize.Parcelize

@Parcelize
class ActivityViewData private constructor(
    val id: String,
    val isRead: Boolean,
    val actionOn: String,
    val actionBy: List<String>,
    @NotificationEntityType
    val entityType: Int,
    val entityId: String,
    val entityOwnerId: String,
    val action: Int,
    val cta: String,
    val activityText: String,
    val activityEntityData: ActivityEntityViewData,
    val activityByUser: UserViewData,
    val createdAt: Long,
    val updatedAt: Long
) : Parcelable, BaseViewType {

    override val viewType: Int
        get() = ITEM_NOTIFICATION_FEED

    class Builder {
        private var id: String = ""
        private var isRead: Boolean = false
        private var actionOn: String = ""
        private var actionBy: List<String> = listOf()

        @NotificationEntityType
        private var entityType: Int = POST
        private var entityId: String = ""
        private var entityOwnerId: String = ""
        private var action: Int = -1
        private var cta: String = ""
        private var activityText: String = ""
        private var activityEntityData: ActivityEntityViewData =
            ActivityEntityViewData.Builder().build()
        private var activityByUser: UserViewData = UserViewData.Builder().build()
        private var createdAt: Long = 0
        private var updatedAt: Long = 0

        fun id(id: String) = apply { this.id = id }
        fun isRead(isRead: Boolean) = apply { this.isRead = isRead }
        fun actionOn(actionOn: String) = apply { this.actionOn = actionOn }
        fun actionBy(actionBy: List<String>) = apply { this.actionBy = actionBy }
        fun entityType(@NotificationEntityType entityType: Int) =
            apply { this.entityType = entityType }

        fun entityId(entityId: String) = apply { this.entityId = entityId }
        fun entityOwnerId(entityOwnerId: String) = apply { this.entityOwnerId = entityOwnerId }
        fun action(action: Int) = apply { this.action = action }
        fun cta(cta: String) = apply { this.cta = cta }
        fun activityText(activityText: String) =
            apply { this.activityText = activityText }

        fun activityEntityData(activityEntityData: ActivityEntityViewData) =
            apply { this.activityEntityData = activityEntityData }

        fun activityByUser(activityByUser: UserViewData) =
            apply { this.activityByUser = activityByUser }

        fun createdAt(createdAt: Long) = apply { this.createdAt = createdAt }
        fun updatedAt(updatedAt: Long) = apply { this.updatedAt = updatedAt }

        fun build() = ActivityViewData(
            id,
            isRead,
            actionOn,
            actionBy,
            entityType,
            entityId,
            entityOwnerId,
            action,
            cta,
            activityText,
            activityEntityData,
            activityByUser,
            createdAt,
            updatedAt
        )
    }

    fun toBuilder(): Builder {
        return Builder().id(id)
            .isRead(isRead)
            .actionOn(actionOn)
            .actionBy(actionBy)
            .entityType(entityType)
            .entityId(entityId)
            .entityOwnerId(entityOwnerId)
            .action(action)
            .cta(cta)
            .activityText(activityText)
            .activityEntityData(activityEntityData)
            .activityByUser(activityByUser)
            .createdAt(createdAt)
            .updatedAt(updatedAt)
    }
}