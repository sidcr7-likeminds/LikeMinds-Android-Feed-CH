package com.likeminds.feedsx.media.model

import android.os.Parcelable
import com.likeminds.feedsx.posttypes.model.AttachmentViewData
import kotlinx.parcelize.Parcelize

@Parcelize
internal class MediaExtras private constructor(
    var isExternallyShared: Boolean,
    var mediaScreenType: Int,
    var title: String?,
    var subtitle: String?,
    var attachments: List<AttachmentViewData>?,
    var singleUriData: SingleUriData?,
    var mediaUris: ArrayList<SingleUriData>?,
    var selectedMediaPosition: Int?,
    var text: String?,
    var position: Int?,
    var medias: List<MediaSwipeViewData>?,
    var postId: String?,
    var communityId: Int?,
    var cropSquare: Boolean?,
    var downloadableContentTypes: List<String>?,
    var communityName: String?,
    var searchKey: String?,
) : Parcelable {

    internal class Builder {
        private var isExternallyShared: Boolean = false
        private var mediaScreenType: Int = -1
        private var title: String? = null
        private var subtitle: String? = null
        private var attachments: List<AttachmentViewData>? = null
        private var singleUriData: SingleUriData? = null
        private var mediaUris: ArrayList<SingleUriData>? = null
        private var selectedMediaPosition: Int? = null
        private var text: String? = null
        private var position: Int? = null
        private var medias: List<MediaSwipeViewData>? = null
        private var postId: String? = null
        private var communityId: Int? = null
        private var cropSquare: Boolean? = null
        private var downloadableContentTypes: List<String>? = null
        private var communityName: String? = null
        private var searchKey: String? = null

        fun isExternallyShared(isExternallyShared: Boolean) =
            apply { this.isExternallyShared = isExternallyShared }

        fun mediaScreenType(mediaScreenType: Int) = apply { this.mediaScreenType = mediaScreenType }
        fun title(title: String?) = apply { this.title = title }
        fun subtitle(subtitle: String?) = apply { this.subtitle = subtitle }
        fun attachments(attachments: List<AttachmentViewData>?) =
            apply { this.attachments = attachments }

        fun singleUriData(singleUriData: SingleUriData?) =
            apply { this.singleUriData = singleUriData }

        fun mediaUris(mediaUris: ArrayList<SingleUriData>?) = apply { this.mediaUris = mediaUris }
        fun selectedMediaPosition(selectedMediaPosition: Int?) =
            apply { this.selectedMediaPosition = selectedMediaPosition }

        fun text(text: String?) = apply { this.text = text }
        fun position(position: Int?) = apply { this.position = position }
        fun medias(medias: List<MediaSwipeViewData>?) = apply { this.medias = medias }
        fun postId(postId: String?) = apply { this.postId = postId }
        fun communityId(communityId: Int?) = apply { this.communityId = communityId }
        fun cropSquare(cropSquare: Boolean?) = apply { this.cropSquare = cropSquare }
        fun downloadableContentTypes(downloadableContentTypes: List<String>?) =
            apply { this.downloadableContentTypes = downloadableContentTypes }

        fun communityName(communityName: String?) = apply { this.communityName = communityName }
        fun searchKey(searchKey: String?) = apply { this.searchKey = searchKey }

        fun build() = MediaExtras(
            isExternallyShared,
            mediaScreenType,
            title,
            subtitle,
            attachments,
            singleUriData,
            mediaUris,
            selectedMediaPosition,
            text,
            position,
            medias,
            postId,
            communityId,
            cropSquare,
            downloadableContentTypes,
            communityName,
            searchKey
        )
    }

    fun toBuilder(): Builder {
        return Builder().isExternallyShared(isExternallyShared)
            .mediaScreenType(mediaScreenType)
            .title(title)
            .subtitle(subtitle)
            .attachments(attachments)
            .singleUriData(singleUriData)
            .mediaUris(mediaUris)
            .selectedMediaPosition(selectedMediaPosition)
            .text(text)
            .position(position)
            .medias(medias)
            .postId(postId)
            .communityId(communityId)
            .cropSquare(cropSquare)
            .downloadableContentTypes(downloadableContentTypes)
            .communityName(communityName)
            .searchKey(searchKey)
    }
}