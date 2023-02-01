package com.likeminds.feedsx.post.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
internal class ShareExternalData private constructor(
    var sharedLink: String?,
    var isInternalLink: Boolean?,
) : Parcelable {
    internal class Builder {
        private var sharedLink: String? = null
        private var isInternalLink: Boolean? = null

        fun sharedLink(sharedLink: String?) = apply { this.sharedLink = sharedLink }
        fun isInternalLink(isInternalLink: Boolean?) =
            apply { this.isInternalLink = isInternalLink }

        fun build() = ShareExternalData(sharedLink, isInternalLink)
    }

    fun toBuilder(): Builder {
        return Builder().sharedLink(sharedLink).isInternalLink(isInternalLink)
    }
}