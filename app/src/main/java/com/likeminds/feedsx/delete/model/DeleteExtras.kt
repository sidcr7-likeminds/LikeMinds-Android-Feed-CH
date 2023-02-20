package com.likeminds.feedsx.delete.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class DeleteExtras private constructor(
    @DeleteType
    var entityType: Int,
    var entityId: String
) : Parcelable {

    class Builder {
        private var entityType: Int = 0
        private var entityId: String = ""

        fun entityType(@DeleteType entityType: Int) = apply { this.entityType = entityType }
        fun entityId(entityId: String) = apply { this.entityId = entityId }

        fun build() = DeleteExtras(entityType, entityId)
    }

    fun toBuilder(): Builder {
        return Builder().entityType(entityType)
            .entityId(entityId)
    }
}