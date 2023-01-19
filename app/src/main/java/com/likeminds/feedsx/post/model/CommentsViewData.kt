package com.likeminds.feedsx.post.model

import android.os.Parcelable
import com.likeminds.feedsx.utils.model.BaseViewType
import com.likeminds.feedsx.utils.model.ITEM_COMMENT
import kotlinx.parcelize.Parcelize

@Parcelize
class CommentsViewData private constructor(
    var userId: String,
    var text: String,
    var level: Int,
    var likesCount: Int,
    var repliesCount: Int,
    var menuItems: List<MenuItemsViewData>
) : Parcelable, BaseViewType {
    override val viewType: Int
        get() = ITEM_COMMENT

    class Builder {
        private var userId: String = ""
        private var text: String = ""
        private var level: Int = 0
        private var likesCount: Int = 0
        private var repliesCount: Int = 0
        private var menuItems: List<MenuItemsViewData> = listOf()

        fun userId(userId: String) = apply { this.userId = userId }
        fun text(text: String) = apply { this.text = text }
        fun level(level: Int) = apply { this.level = level }
        fun likesCount(likesCount: Int) = apply { this.likesCount = likesCount }
        fun repliesCount(repliesCount: Int) = apply { this.repliesCount = repliesCount }
        fun menuItems(menuItems: List<MenuItemsViewData>) = apply { this.menuItems = menuItems }

        fun build() = CommentsViewData(
            userId,
            text,
            level,
            likesCount,
            repliesCount,
            menuItems
        )
    }

    fun toBuilder(): Builder {
        return Builder().userId(userId)
            .text(text)
            .level(level)
            .likesCount(likesCount)
            .repliesCount(repliesCount)
            .menuItems(menuItems)
    }

}