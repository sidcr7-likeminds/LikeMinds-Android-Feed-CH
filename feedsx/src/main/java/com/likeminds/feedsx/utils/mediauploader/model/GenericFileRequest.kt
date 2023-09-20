package com.likeminds.feedsx.utils.mediauploader.model

import android.net.Uri

class GenericFileRequest private constructor(
    val name: String?,
    val fileUri: Uri?,
    val fileType: Int,
    val awsFolderPath: String,
    val localFilePath: String?,
    val index: Int,
    val width: Int?,
    val height: Int?,
    val pageCount: Int?,
    val size: Long?,
    val duration: Int?,
    val thumbnailUri: Uri?,
    val thumbnailAWSFolderPath: String?,
    val thumbnailLocalFilePath: String?,
    val isThumbnail: Boolean?,
    val hasThumbnail: Boolean?
) {

    class Builder {

        private var name: String? = null
        private var fileUri: Uri? = null
        private var fileType: Int = 1
        private var awsFolderPath: String = ""
        private var localFilePath: String? = null
        private var index: Int = 0
        private var width: Int? = null
        private var height: Int? = null
        private var pageCount: Int? = null
        private var size: Long? = null
        private var duration: Int? = null
        private var thumbnailUri: Uri? = null
        private var thumbnailAWSFolderPath: String? = null
        private var thumbnailLocalFilePath: String? = null
        private var isThumbnail: Boolean? = null
        private var hasThumbnail: Boolean? = null

        fun name(name: String?) = apply { this.name = name }
        fun fileUri(fileUri: Uri?) = apply { this.fileUri = fileUri }
        fun fileType(fileType: Int) = apply { this.fileType = fileType }
        fun awsFolderPath(awsFolderPath: String) = apply { this.awsFolderPath = awsFolderPath }
        fun localFilePath(localFilePath: String?) = apply { this.localFilePath = localFilePath }
        fun index(index: Int) = apply { this.index = index }
        fun width(width: Int?) = apply { this.width = width }
        fun height(height: Int?) = apply { this.height = height }
        fun pageCount(pageCount: Int?) = apply { this.pageCount = pageCount }
        fun size(size: Long?) = apply { this.size = size }
        fun duration(duration: Int?) = apply { this.duration = duration }
        fun thumbnailUri(thumbnailUri: Uri?) = apply { this.thumbnailUri = thumbnailUri }
        fun thumbnailAWSFolderPath(thumbnailAWSFolderPath: String?) =
            apply { this.thumbnailAWSFolderPath = thumbnailAWSFolderPath }

        fun thumbnailLocalFilePath(thumbnailLocalFilePath: String?) =
            apply { this.thumbnailLocalFilePath = thumbnailLocalFilePath }

        fun isThumbnail(isThumbnail: Boolean?) = apply { this.isThumbnail = isThumbnail }
        fun hasThumbnail(hasThumbnail: Boolean?) = apply { this.hasThumbnail = hasThumbnail }

        fun build() = GenericFileRequest(
            name,
            fileUri,
            fileType,
            awsFolderPath,
            localFilePath,
            index,
            width,
            height,
            pageCount,
            size,
            duration,
            thumbnailUri,
            thumbnailAWSFolderPath,
            thumbnailLocalFilePath,
            isThumbnail,
            hasThumbnail
        )
    }

    fun toBuilder(): Builder {
        return Builder().name(name)
            .fileUri(fileUri)
            .fileType(fileType)
            .awsFolderPath(awsFolderPath)
            .localFilePath(localFilePath)
            .index(index)
            .width(width)
            .height(height)
            .pageCount(pageCount)
            .size(size)
            .duration(duration)
            .thumbnailUri(thumbnailUri)
            .thumbnailAWSFolderPath(thumbnailAWSFolderPath)
            .thumbnailLocalFilePath(thumbnailLocalFilePath)
            .isThumbnail(isThumbnail)
            .hasThumbnail(hasThumbnail)
    }
}