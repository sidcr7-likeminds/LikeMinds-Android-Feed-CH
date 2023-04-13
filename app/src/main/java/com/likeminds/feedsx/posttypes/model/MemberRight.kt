package com.likeminds.feedsx.posttypes.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class MemberRight private constructor(
    val id: Int,
    val isLocked: Boolean?,
    val isSelected: Boolean,
    val state: Int,
    val title: String,
    val subtitle: String?,
) : Parcelable {
    class Builder {
        private var id: Int = 0
        private var isLocked: Boolean? = null
        private var isSelected: Boolean = false
        private var state: Int = 0
        private var title: String = ""
        private var subtitle: String? = null

        fun id(id: Int) = apply { this.id = id }
        fun isLocked(isLocked: Boolean?) = apply { this.isLocked = isLocked }
        fun isSelected(isSelected: Boolean) = apply { this.isSelected = isSelected }
        fun state(state: Int) = apply { this.state = state }
        fun title(title: String) = apply { this.title = title }
        fun subtitle(subtitle: String?) = apply { this.subtitle = subtitle }

        fun build() = MemberRight(
            id,
            isLocked,
            isSelected,
            state,
            title,
            subtitle
        )
    }

    fun toBuilder(): Builder {
        return Builder().id(id)
            .isLocked(isLocked)
            .isSelected(isSelected)
            .state(state)
            .title(title)
            .subtitle(subtitle)
    }
}