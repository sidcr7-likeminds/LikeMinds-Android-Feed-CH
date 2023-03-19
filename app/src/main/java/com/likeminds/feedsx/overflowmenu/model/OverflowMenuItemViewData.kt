package com.likeminds.feedsx.overflowmenu.model

import android.os.Parcelable
import com.likeminds.feedsx.utils.model.BaseViewType
import com.likeminds.feedsx.utils.model.ITEM_OVERFLOW_MENU_ITEM
import kotlinx.parcelize.Parcelize

// title -> title of the menu item
// entityId -> id of the corresponding data (post-id/comment-id, etc)
// postId -> id of post id in case of comment
// entityCreatorId -> userId of the creator of entity
@Parcelize
class OverflowMenuItemViewData private constructor(
    @OverflowMenuItemTitle
    var title: String,
    var entityId: String,
    var entityCreatorId: String,
    var postId: String? = null,
) : Parcelable, BaseViewType {
    override val viewType: Int
        get() = ITEM_OVERFLOW_MENU_ITEM

    class Builder {
        @OverflowMenuItemTitle
        private var title: String = DELETE_POST_MENU_ITEM
        private var entityId: String = ""
        private var postId: String? = null
        private var entityCreatorId: String = ""

        fun title(@OverflowMenuItemTitle title: String) = apply { this.title = title }
        fun entityId(entityId: String) = apply { this.entityId = entityId }
        fun postId(postId: String?) = apply { this.postId = postId }
        fun entityCreatorId(entityCreatorId: String) =
            apply { this.entityCreatorId = entityCreatorId }

        fun build() = OverflowMenuItemViewData(
            title,
            entityId,
            entityCreatorId,
            postId
        )
    }

    fun toBuilder(): Builder {
        return Builder().title(title)
            .postId(postId)
            .entityId(entityId)
            .entityCreatorId(entityCreatorId)
    }

}