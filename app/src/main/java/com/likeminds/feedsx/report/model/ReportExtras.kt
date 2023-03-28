package com.likeminds.feedsx.report.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class ReportExtras private constructor(
    @ReportType
    var entityType: Int,
    var entityCreatorId: String,
    var entityId: String,
) : Parcelable {

    class Builder {
        @ReportType
        private var entityType: Int = REPORT_TYPE_POST
        private var entityCreatorId: String = ""
        private var entityId: String = ""

        fun entityType(@ReportType entityType: Int) = apply { this.entityType = entityType }
        fun entityCreatorId(entityCreatorId: String) =
            apply { this.entityCreatorId = entityCreatorId }

        fun entityId(entityId: String) = apply { this.entityId = entityId }

        fun build() = ReportExtras(
            entityType,
            entityCreatorId,
            entityId
        )
    }

    fun toBuilder(): Builder {
        return Builder().entityCreatorId(entityCreatorId)
            .entityType(entityType)
            .entityId(entityId)
    }
}