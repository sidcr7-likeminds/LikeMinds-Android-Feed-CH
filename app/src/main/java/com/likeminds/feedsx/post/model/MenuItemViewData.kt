package com.likeminds.feedsx.post.model

import android.os.Parcelable
import com.likeminds.feedsx.utils.model.BaseViewType
import com.likeminds.feedsx.utils.model.ITEM_MENU_ITEM
import kotlinx.parcelize.Parcelize

@Parcelize
class MenuItemViewData private constructor(
    var title: String
) : Parcelable, BaseViewType {
    override val viewType: Int
        get() = ITEM_MENU_ITEM

    class Builder {
        private var title: String = ""

        fun title(title: String) = apply { this.title = title }

        fun build() = MenuItemViewData(
            title
        )
    }

    fun toBuilder(): Builder {
        return Builder().title(title)
    }

}