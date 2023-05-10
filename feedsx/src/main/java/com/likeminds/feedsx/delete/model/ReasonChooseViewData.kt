package com.likeminds.feedsx.delete.model

import android.os.Parcelable
import com.likeminds.feedsx.utils.model.BaseViewType
import com.likeminds.feedsx.utils.model.ITEM_REASON_CHOOSE
import kotlinx.parcelize.Parcelize

@Parcelize
class ReasonChooseViewData private constructor(
    val value: String,
    val hideBottomLine: Boolean?
) : Parcelable, BaseViewType {

    override val viewType: Int
        get() = ITEM_REASON_CHOOSE

    class Builder {
        private var value: String = ""
        private var hideBottomLine: Boolean? = null

        fun value(value: String) = apply { this.value = value }
        fun hideBottomLine(hideBottomLine: Boolean?) =
            apply { this.hideBottomLine = hideBottomLine }

        fun build() = ReasonChooseViewData(
            value,
            hideBottomLine
        )
    }

    fun toBuilder(): Builder {
        return Builder().value(value)
            .hideBottomLine(hideBottomLine)
    }
}