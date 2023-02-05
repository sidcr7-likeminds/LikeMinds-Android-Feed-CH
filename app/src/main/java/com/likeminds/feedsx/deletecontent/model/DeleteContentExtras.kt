package com.likeminds.feedsx.deletecontent.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class DeleteContentExtras private constructor(
    @DeleteContentType
    var contentType: Int,
    var contentId: String
) : Parcelable {

    class Builder {
        private var contentType: Int = 0
        private var contentId: String = ""

        fun contentType(contentType: Int) = apply { this.contentType = contentType }
        fun contentId(contentId: String) = apply { this.contentId = contentId }

        fun build() = DeleteContentExtras(contentType, contentId)
    }

    fun toBuilder(): Builder {
        return Builder().contentType(contentType)
            .contentId(contentId)
    }
}