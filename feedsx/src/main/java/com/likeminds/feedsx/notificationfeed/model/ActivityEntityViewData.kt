package com.likeminds.feedsx.notificationfeed.model

import android.os.Parcelable
import com.likeminds.feedsx.posttypes.model.AttachmentViewData
import com.likeminds.feedsx.posttypes.model.CommentViewData
import kotlinx.parcelize.Parcelize

@Parcelize
class ActivityEntityViewData private constructor(
    val id: String,
    val text: String,
    val deleteReason: String,
    val deletedBy: String,
    val heading: String,
    val attachments: List<AttachmentViewData>?,
    val communityId: Int,
    val isEdited: Boolean,
    val isPinned: Boolean,
    val userId: String,
    val replies: MutableList<CommentViewData>?,
    val level: Int,
    // todo: change to Long
    val createdAt: String,
    val updatedAt: String
) : Parcelable {
    class Builder {
        private var id: String = ""
        private var text: String = ""
        private var deleteReason: String = ""
        private var deletedBy: String = ""
        private var heading: String = ""
        private var attachments: List<AttachmentViewData>? = null
        private var communityId: Int = 0
        private var isEdited: Boolean = false
        private var isPinned: Boolean = false
        private var userId: String = ""
        private var replies: MutableList<CommentViewData>? = null
        private var level: Int = 0

        // todo: change to Long
        private var createdAt: String = ""
        private var updatedAt: String = ""

        fun id(id: String) = apply { this.id = id }
        fun text(text: String) = apply { this.text = text }
        fun deleteReason(deleteReason: String) = apply { this.deleteReason = deleteReason }
        fun deletedBy(deletedBy: String) = apply { this.deletedBy = deletedBy }
        fun heading(heading: String) = apply { this.heading = heading }
        fun attachments(attachments: List<AttachmentViewData>?) =
            apply { this.attachments = attachments }

        fun communityId(communityId: Int) = apply { this.communityId = communityId }
        fun isEdited(isEdited: Boolean) = apply { this.isEdited = isEdited }
        fun isPinned(isPinned: Boolean) = apply { this.isPinned = isPinned }
        fun userId(userId: String) = apply { this.userId = userId }
        fun replies(replies: MutableList<CommentViewData>?) = apply { this.replies = replies }
        fun level(level: Int) = apply { this.level = level }

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
            replies,
            level,
            createdAt,
            updatedAt
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
            .replies(replies)
            .level(level)
    }
}