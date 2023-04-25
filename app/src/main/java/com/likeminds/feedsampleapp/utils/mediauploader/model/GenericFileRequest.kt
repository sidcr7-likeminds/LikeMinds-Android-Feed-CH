package com.likeminds.feedsampleapp.utils.mediauploader.model

import android.net.Uri

class GenericFileRequest private constructor(
    var name: String?,
    var fileUri: Uri?,
    var fileType: Int,
    var awsFolderPath: String,
    var localFilePath: String?,
    var index: Int,
    var width: Int?,
    var height: Int?,
    var pageCount: Int?,
    var size: Long?,
    var duration: Int?
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
            duration
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
    }
}