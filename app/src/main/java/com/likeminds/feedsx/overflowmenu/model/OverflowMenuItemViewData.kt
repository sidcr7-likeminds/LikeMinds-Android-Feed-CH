package com.likeminds.feedsx.overflowmenu.model

import android.os.Parcelable
import com.likeminds.feedsx.utils.model.BaseViewType
import com.likeminds.feedsx.utils.model.ITEM_OVERFLOW_MENU_ITEM
import kotlinx.parcelize.Parcelize

@Parcelize
class OverflowMenuItemViewData private constructor(
    @OverflowMenuItemId
    val id: Int,
    var title: String
) : Parcelable, BaseViewType {
    override val viewType: Int
        get() = ITEM_OVERFLOW_MENU_ITEM

    class Builder {
        @OverflowMenuItemId
        private var id: Int = DELETE_POST_MENU_ITEM_ID
        private var title: String = ""

        fun id(@OverflowMenuItemId id: Int) = apply { this.id = id }
        fun title(title: String) = apply { this.title = title }

        fun build() = OverflowMenuItemViewData(
            id,
            title
        )
    }

    fun toBuilder(): Builder {
        return Builder().id(id).title(title)
    }

}