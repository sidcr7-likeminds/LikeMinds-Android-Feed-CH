package com.likeminds.feedsx.media.model

import android.net.Uri
import android.os.Parcelable
import com.likeminds.feedsx.utils.model.BaseViewType
import com.likeminds.feedsx.utils.model.ITEM_MEDIA_PICKER_SINGLE
import kotlinx.parcelize.Parcelize

@Parcelize
class MediaViewData private constructor(
    var uri: Uri,
    var mimeType: String?,
    var mediaType: String,
    var date: Long,
    var size: Long,
    var duration: Int?,
    var bucketId: String?,
    var dateTimeStampHeader: String,
    var mediaName: String?,
    var dynamicViewType: Int?,
    var filteredKeywords: List<String>?,
    var pdfPageCount: Int?
) : Parcelable, BaseViewType {
    override val viewType: Int
        get() = dynamicViewType ?: ITEM_MEDIA_PICKER_SINGLE

    class Builder {
        private var uri: Uri = Uri.parse("")
        private var mimeType: String? = null
        private var mediaType: String = ""
        private var date: Long = 0
        private var size: Long = 0
        private var duration: Int? = null
        private var bucketId: String? = null
        private var dateTimeStampHeader: String = ""
        private var mediaName: String? = null
        private var dynamicViewType: Int? = null
        private var filteredKeywords: List<String>? = null
        private var pdfPageCount: Int? = null

        fun uri(uri: Uri) = apply { this.uri = uri }
        fun mimeType(mimeType: String?) = apply { this.mimeType = mimeType }
        fun mediaType(mediaType: String) = apply { this.mediaType = mediaType }
        fun date(date: Long) = apply { this.date = date }
        fun size(size: Long) = apply { this.size = size }
        fun duration(duration: Int?) = apply { this.duration = duration }
        fun bucketId(bucketId: String?) = apply { this.bucketId = bucketId }
        fun dateTimeStampHeader(dateTimeStampHeader: String) =
            apply { this.dateTimeStampHeader = dateTimeStampHeader }

        fun mediaName(mediaName: String?) = apply { this.mediaName = mediaName }
        fun dynamicViewType(dynamicViewType: Int?) = apply { this.dynamicViewType = dynamicViewType }
        fun filteredKeywords(filteredKeywords: List<String>?) =
            apply { this.filteredKeywords = filteredKeywords }

        fun pdfPageCount(pdfPageCount: Int?) = apply { this.pdfPageCount = pdfPageCount }

        fun build() = MediaViewData(
            uri,
            mimeType,
            mediaType,
            date,
            size,
            duration,
            bucketId,
            dateTimeStampHeader,
            mediaName,
            dynamicViewType,
            filteredKeywords,
            pdfPageCount
        )
    }

    fun toBuilder(): Builder {
        return Builder().uri(uri)
            .mimeType(mimeType)
            .mediaType(mediaType)
            .date(date)
            .size(size)
            .duration(duration)
            .bucketId(bucketId)
            .dateTimeStampHeader(dateTimeStampHeader)
            .mediaName(mediaName)
            .dynamicViewType(dynamicViewType)
            .filteredKeywords(filteredKeywords)
            .pdfPageCount(pdfPageCount)
    }
}