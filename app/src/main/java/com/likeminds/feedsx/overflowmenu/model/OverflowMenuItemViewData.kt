package com.likeminds.feedsx.overflowmenu.model

import android.os.Parcelable
import com.likeminds.feedsx.utils.model.BaseViewType
import com.likeminds.feedsx.utils.model.ITEM_OVERFLOW_MENU_ITEM
import kotlinx.parcelize.Parcelize

// title -> title of the menu item
// entityId -> id of the corresponding data (post-id/comment-id, etc)
@Parcelize
class OverflowMenuItemViewData private constructor(
    var title: String,
    var entityId: String
) : Parcelable, BaseViewType {
    override val viewType: Int
        get() = ITEM_OVERFLOW_MENU_ITEM

    class Builder {
        private var title: String = ""
        private var entityId: String = ""

        fun title(title: String) = apply { this.title = title }
        fun entityId(entityId: String) = apply { this.entityId = entityId }

        fun build() = OverflowMenuItemViewData(
            title,
            entityId
        )
    }

    fun toBuilder(): Builder {
        return Builder().title(title).entityId(entityId)
    }

}