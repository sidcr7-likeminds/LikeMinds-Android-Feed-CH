package com.likeminds.feedsx.overflowmenu.model

import android.os.Parcelable
import com.likeminds.feedsx.utils.model.BaseViewType
import com.likeminds.feedsx.utils.model.ITEM_OVERFLOW_MENU_ITEM
import kotlinx.parcelize.Parcelize

// title -> title of the menu item
@Parcelize
class OverflowMenuItemViewData private constructor(
    @OverflowMenuItemTitle
    var title: String
) : Parcelable, BaseViewType {
    override val viewType: Int
        get() = ITEM_OVERFLOW_MENU_ITEM

    class Builder {
        @OverflowMenuItemTitle
        private var title: String = DELETE_POST_MENU_ITEM

        fun title(@OverflowMenuItemTitle title: String) = apply { this.title = title }

        fun build() = OverflowMenuItemViewData(
            title
        )
    }

    fun toBuilder(): Builder {
        return Builder().title(title)
    }

}