package com.likeminds.feedsx.notificationfeed.model

import android.os.Parcelable
import com.likeminds.feedsx.posttypes.model.AttachmentViewData
import com.likeminds.feedsx.posttypes.model.CommentViewData
import com.likeminds.feedsx.posttypes.model.UserViewData
import kotlinx.parcelize.Parcelize

@Parcelize
class ActivityEntityViewData private constructor(
    val id: String,
    val text: String,
    val deleteReason: String?,
    val deletedBy: String?,
    val heading: String?,
    val attachments: List<AttachmentViewData>?,
    val communityId: Int,
    val isEdited: Boolean,
    val isPinned: Boolean?,
    val userId: String,
    val user: UserViewData,
    val replies: MutableList<CommentViewData>?,
    val level: Int?,
    val createdAt: Long,
    val updatedAt: Long,
    val uuid: String
) : Parcelable {
    class Builder {
        private var id: String = ""
        private var text: String = ""
        private var deleteReason: String? = null
        private var deletedBy: String? = null
        private var heading: String? = null
        private var attachments: List<AttachmentViewData>? = null
        private var communityId: Int = 0
        private var isEdited: Boolean = false
        private var isPinned: Boolean? = null
        private var userId: String = ""
        private var user: UserViewData = UserViewData.Builder().build()
        private var replies: MutableList<CommentViewData>? = null
        private var level: Int? = null
        private var createdAt: Long = 0
        private var updatedAt: Long = 0
        private var uuid: String = ""

        fun id(id: String) = apply { this.id = id }
        fun text(text: String) = apply { this.text = text }
        fun deleteReason(deleteReason: String?) = apply { this.deleteReason = deleteReason }
        fun deletedBy(deletedBy: String?) = apply { this.deletedBy = deletedBy }
        fun heading(heading: String?) = apply { this.heading = heading }
        fun attachments(attachments: List<AttachmentViewData>?) =
            apply { this.attachments = attachments }

        fun communityId(communityId: Int) = apply { this.communityId = communityId }
        fun isEdited(isEdited: Boolean) = apply { this.isEdited = isEdited }
        fun isPinned(isPinned: Boolean?) = apply { this.isPinned = isPinned }
        fun userId(userId: String) = apply { this.userId = userId }
        fun user(user: UserViewData) = apply { this.user = user }
        fun replies(replies: MutableList<CommentViewData>?) = apply { this.replies = replies }
        fun level(level: Int?) = apply { this.level = level }
        fun createdAt(createdAt: Long) = apply { this.createdAt = createdAt }
        fun updatedAt(updatedAt: Long) = apply { this.updatedAt = updatedAt }
        fun uuid(uuid: String) = apply { this.uuid = uuid }

        fun build() = ActivityEntityViewData(
            id,
            text,
            deleteReason,
            deletedBy,
            heading,
            attachments,
            communityId,
            isEdited,
            isPinned,
            userId,
            user,
            replies,
            level,
            createdAt,
            updatedAt,
            uuid
        )
    }

    fun toBuilder(): Builder {
        return Builder().id(id)
            .text(text)
            .deleteReason(deleteReason)
            .deletedBy(deletedBy)
            .heading(heading)
            .attachments(attachments)
            .communityId(communityId)
            .isEdited(isEdited)
            .isPinned(isPinned)
            .userId(userId)
            .user(user)
            .replies(replies)
            .level(level)
            .createdAt(createdAt)
            .updatedAt(updatedAt)
            .uuid(uuid)
    }
}