package com.likeminds.feedsx.widgets.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class WidgetsViewData private constructor(
    val id: String,
    val createdAt: Long,
    val widgetMetaData: WidgetMetaViewData?,
    val parentEntityId: String,
    val parentEntityType: String,
    val updatedAt: Long
) : Parcelable {
    class Builder {
        private var id: String = ""
        private var createdAt: Long = 0L
        private var widgetMetaData: WidgetMetaViewData? = null
        private var parentEntityId: String = ""
        private var parentEntityType: String = ""
        private var updatedAt: Long = 0L

        fun id(id: String) = apply { this.id = id }
        fun createdAt(createdAt: Long) = apply { this.createdAt = createdAt }
        fun metaData(widgetMetaData: WidgetMetaViewData?) =
            apply { this.widgetMetaData = widgetMetaData }

        fun parentEntityId(parentEntityId: String) = apply { this.parentEntityId = parentEntityId }
        fun parentEntityType(parentEntityType: String) =
            apply { this.parentEntityType = parentEntityType }

        fun updatedAt(updatedAt: Long) = apply { this.updatedAt = updatedAt }

        fun build() = WidgetsViewData(
            id,
            createdAt,
            widgetMetaData,
            parentEntityId,
            parentEntityType,
            updatedAt
        )
    }

    fun toBuilder(): Builder {
        return Builder().id(id)
            .createdAt(createdAt)
            .metaData(widgetMetaData)
            .parentEntityId(parentEntityId)
            .parentEntityType(parentEntityType)
            .updatedAt(updatedAt)
    }
}