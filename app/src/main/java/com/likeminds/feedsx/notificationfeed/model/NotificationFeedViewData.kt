package com.likeminds.feedsx.notificationfeed.model

import android.os.Parcelable
import com.likeminds.feedsx.utils.model.BaseViewType
import kotlinx.parcelize.Parcelize

@Parcelize
class NotificationFeedViewData private constructor(
    var id: String,
    var actionBy: String,
    var actionOn: List<String>,
    var communityId: Int,
    @NotificationEntityType
    var entityType: String,
    var entityId: String,
    var action: String,
    var cta: String,
    var activityMessage: String,
    var createdAt: Long,
    var updatedAt: Long
) : Parcelable, BaseViewType {

    override val viewType: Int
        get() = TODO("Not yet implemented")

    class Builder {
        private var id: String = ""
        private var actionBy: String = ""
        private var actionOn: List<String> = listOf()
        private var communityId: Int = 0

        @NotificationEntityType
        private var entityType: String = POST
        private var entityId: String = ""
        private var action: String = ""
        private var cta: String = ""
        private var activityMessage: String = ""
        private var createdAt: Long = 0
        private var updatedAt: Long = 0

        fun id(id: String) = apply { this.id = id }
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

        fun createdAt(createdAt: Long) = apply { this.createdAt = createdAt }
        fun updatedAt(updatedAt: Long) = apply { this.updatedAt = updatedAt }
        
        fun build() = NotificationFeedViewData(
            id,
            actionBy,
            actionOn,
            communityId,
            entityType,
            entityId,
            action,
            cta,
            activityMessage,
            createdAt,
            updatedAt
        )
    }

    fun toBuilder(): Builder {
        return Builder()
    }
}