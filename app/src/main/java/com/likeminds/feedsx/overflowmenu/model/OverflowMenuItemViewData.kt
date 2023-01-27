package com.likeminds.feedsx.overflowmenu.model

import android.os.Parcelable
import com.likeminds.feedsx.utils.model.BaseViewType
import com.likeminds.feedsx.utils.model.ITEM_OVERFLOW_MENU_ITEM
import kotlinx.parcelize.Parcelize

@Parcelize
class OverflowMenuItemViewData private constructor(
    var title: String,
    var dataId: String
) : Parcelable, BaseViewType {
    override val viewType: Int
        get() = ITEM_OVERFLOW_MENU_ITEM

    class Builder {
        private var title: String = ""
        private var dataId: String = ""

        fun title(title: String) = apply { this.title = title }
        fun dataId(dataId: String) = apply { this.dataId = dataId }

        fun build() = OverflowMenuItemViewData(
            title,
            dataId
        )
    }

    fun toBuilder(): Builder {
        return Builder().title(title).dataId(dataId)
    }

}