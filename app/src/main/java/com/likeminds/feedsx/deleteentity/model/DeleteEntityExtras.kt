package com.likeminds.feedsx.deleteentity.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class DeleteEntityExtras private constructor(
    @DeleteEntityType
    var entityType: Int,
    var entityId: String
) : Parcelable {

    class Builder {
        private var entityType: Int = 0
        private var entityId: String = ""

        fun entityType(@DeleteEntityType entityType: Int) = apply { this.entityType = entityType }
        fun entityId(entityId: String) = apply { this.entityId = entityId }

        fun build() = DeleteEntityExtras(entityType, entityId)
    }

    fun toBuilder(): Builder {
        return Builder().entityType(entityType)
            .entityId(entityId)
    }
}