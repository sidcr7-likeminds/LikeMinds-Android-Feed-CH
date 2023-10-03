package com.likeminds.feedsx.topic.model

import android.os.Parcelable
import com.likeminds.feedsx.utils.model.BaseViewType
import com.likeminds.feedsx.utils.model.ITEM_TOPIC
import kotlinx.parcelize.Parcelize

@Parcelize
class LMFeedTopicViewData private constructor(
    val id: String,
    val name: String,
    val isEnabled: Boolean,
    val isSelected: Boolean
) : Parcelable, BaseViewType {

    override val viewType: Int
        get() = ITEM_TOPIC

    class Builder {
        private var id: String = ""
        private var name: String = ""
        private var isEnabled: Boolean = false
        private var isSelected: Boolean = false

        fun id(id: String) = apply { this.id = id }
        fun name(name: String) = apply { this.name = name }
        fun isEnabled(isEnabled: Boolean) = apply { this.isEnabled = isEnabled }
        fun isSelected(isSelected: Boolean) = apply { this.isSelected = isSelected }

        fun build() = LMFeedTopicViewData(id, name, isEnabled, isSelected)
    }

    fun toBuilder(): Builder {
        return Builder().id(id)
            .name(name)
            .isEnabled(isEnabled)
            .isSelected(isSelected)
    }
}