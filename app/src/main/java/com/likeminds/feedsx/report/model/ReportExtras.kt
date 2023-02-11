package com.likeminds.feedsx.report.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class ReportExtras private constructor(
    var type: Int,
    var memberId: String?,
    var entityId: String?,
) : Parcelable {

    class Builder {
        private var type: Int = -1
        private var memberId: String? = null
        private var entityId: String? = null

        fun type(type: Int) = apply { this.type = type }
        fun memberId(memberId: String?) = apply { this.memberId = memberId }
        fun entityId(entityId: String?) = apply { this.entityId = entityId }

        fun build() = ReportExtras(
            type,
            memberId,
            entityId
        )
    }

    fun toBuilder(): Builder {
        return Builder().memberId(memberId)
            .type(type)
            .entityId(entityId)
    }
}