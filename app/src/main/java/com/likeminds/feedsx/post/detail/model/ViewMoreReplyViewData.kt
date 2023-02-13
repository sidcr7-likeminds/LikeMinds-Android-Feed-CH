package com.likeminds.feedsx.post.detail.model

import android.os.Parcelable
import com.likeminds.feedsx.utils.model.BaseViewType
import com.likeminds.feedsx.utils.model.ITEM_REPLY_VIEW_MORE_REPLY
import kotlinx.parcelize.Parcelize

@Parcelize
class ViewMoreReplyViewData private constructor(
    var currentCount: Int,
    var totalCommentsCount: Int,
    var parentCommentId: String,
    var parentCommentPosition: Int
) : Parcelable, BaseViewType {

    override val viewType: Int
        get() = ITEM_REPLY_VIEW_MORE_REPLY

    class Builder {
        private var currentCount: Int = 0
        private var totalCommentsCount: Int = 0
        private var parentCommentId: String = ""
        private var parentCommentPosition: Int = 0

        fun currentCount(currentCount: Int) = apply { this.currentCount = currentCount }
        fun totalCommentsCount(totalCommentsCount: Int) =
            apply { this.totalCommentsCount = totalCommentsCount }

        fun parentCommentId(parentCommentId: String) =
            apply { this.parentCommentId = parentCommentId }

        fun parentCommentPosition(parentCommentPosition: Int) =
            apply { this.parentCommentPosition = parentCommentPosition }

        fun build() = ViewMoreReplyViewData(
            currentCount,
            totalCommentsCount,
            parentCommentId,
            parentCommentPosition
        )
    }

    fun toBuilder(): Builder {
        return Builder().currentCount(currentCount)
            .totalCommentsCount(totalCommentsCount)
            .parentCommentId(parentCommentId)
            .parentCommentPosition(parentCommentPosition)
    }
}