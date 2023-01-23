package com.likeminds.feedsx.media.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class MediaPickerExtras private constructor(
    var creatorName: String,
    @MediaType
    val mediaTypes: List<String>,
    val allowMultipleSelect: Boolean,
) : Parcelable {

    class Builder {
        private var creatorName: String = ""

        @MediaType
        private var mediaTypes: List<String> = listOf()
        private var allowMultipleSelect: Boolean = false

        fun creatorName(creatorName: String) = apply { this.creatorName = creatorName }
        fun mediaTypes(@MediaType mediaTypes: List<String>) = apply { this.mediaTypes = mediaTypes }
        fun allowMultipleSelect(allowMultipleSelect: Boolean) =
            apply { this.allowMultipleSelect = allowMultipleSelect }

        fun build() = MediaPickerExtras(
            creatorName,
            mediaTypes,
            allowMultipleSelect
        )
    }

    fun toBuilder(): Builder {
        return Builder().creatorName(creatorName)
            .mediaTypes(mediaTypes)
            .allowMultipleSelect(allowMultipleSelect)
    }
}