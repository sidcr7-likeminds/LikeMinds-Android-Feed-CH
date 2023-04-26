package com.likeminds.feedsx.post.detail.model

import android.os.Parcelable
import com.likeminds.feedsx.utils.model.BaseViewType
import com.likeminds.feedsx.utils.model.ITEM_POST_DETAIL_COMMENTS_COUNT
import kotlinx.parcelize.Parcelize

@Parcelize
class CommentsCountViewData private constructor(
    val commentsCount: Int
) : Parcelable, BaseViewType {

    override val viewType: Int
        get() = ITEM_POST_DETAIL_COMMENTS_COUNT

    class Builder {
        private var commentsCount: Int = 0

        fun commentsCount(commentsCount: Int) = apply { this.commentsCount = commentsCount }

        fun build() = CommentsCountViewData(commentsCount)
    }

    fun toBuilder(): Builder {
        return Builder().commentsCount(commentsCount)
    }
}