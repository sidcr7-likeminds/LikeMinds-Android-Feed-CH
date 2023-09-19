package com.likeminds.feedsx.media.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class ImageCropExtras private constructor(
    val singleUriData: SingleUriData?,
    val cropWidth: Int,
    val cropHeight: Int
) : Parcelable {
    class Builder {
        private var singleUriData: SingleUriData? = null
        private var cropWidth: Int = 0
        private var cropHeight: Int = 0

        fun singleUriData(singleUriData: SingleUriData?) =
            apply { this.singleUriData = singleUriData }

        fun cropWidth(cropWidth: Int) = apply { this.cropWidth = cropWidth }
        fun cropHeight(cropHeight: Int) = apply { this.cropHeight = cropHeight }

        fun build() = ImageCropExtras(
            singleUriData,
            cropWidth,
            cropHeight
        )
    }

    fun toBuilder(): Builder {
        return Builder().cropWidth(cropWidth)
            .cropHeight(cropHeight)
            .singleUriData(singleUriData)
    }
}