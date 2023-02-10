package com.likeminds.feedsx.media.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class MediaPickerExtras private constructor(
    @MediaType
    val mediaTypes: List<String>,
    val allowMultipleSelect: Boolean,
) : Parcelable {

    class Builder {

        @MediaType
        private var mediaTypes: List<String> = listOf()
        private var allowMultipleSelect: Boolean = false

        fun mediaTypes(@MediaType mediaTypes: List<String>) = apply { this.mediaTypes = mediaTypes }
        fun allowMultipleSelect(allowMultipleSelect: Boolean) =
            apply { this.allowMultipleSelect = allowMultipleSelect }

        fun build() = MediaPickerExtras(
            mediaTypes,
            allowMultipleSelect
        )
    }

    fun toBuilder(): Builder {
        return Builder()
            .mediaTypes(mediaTypes)
            .allowMultipleSelect(allowMultipleSelect)
    }
}