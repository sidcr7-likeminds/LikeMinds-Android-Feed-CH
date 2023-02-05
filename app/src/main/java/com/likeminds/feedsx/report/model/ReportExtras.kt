package com.likeminds.feedsx.report.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class ReportExtras private constructor(
    var type: Int,
    var memberId: String?,
    var dataId: String?,
) : Parcelable {

    class Builder {
        private var type: Int = -1
        private var memberId: String? = null
        private var dataId: String? = null

        fun type(type: Int) = apply { this.type = type }
        fun memberId(memberId: String?) = apply { this.memberId = memberId }
        fun dataId(dataId: String?) = apply { this.dataId = dataId }

        fun build() = ReportExtras(
            type,
            memberId,
            dataId
        )
    }

    fun toBuilder(): Builder {
        return Builder().memberId(memberId)
            .type(type)
            .dataId(dataId)
    }
}