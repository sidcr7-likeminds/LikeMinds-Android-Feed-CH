package com.likeminds.feedsample.utils.mediauploader.model

import android.util.Base64
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver
import com.likeminds.feedsample.utils.mediauploader.utils.AWSKeys

class AWSFileResponse private constructor(
    var name: String,
    var awsFolderPath: String,
    var transferObserver: TransferObserver?,
    var index: Int,
    var fileType: Int,
    var width: Int?,
    var height: Int?,
    var pageCount: Int?,
    var size: Long?,
    var duration: Int?,
    var uuid: String?
) {
    val downloadUrl: String
        get() = String(Base64.decode(AWSKeys.getBucketBaseUrl(), Base64.DEFAULT)) + awsFolderPath

    class Builder {

        private var name: String = ""
        private var awsFolderPath: String = ""
        private var transferObserver: TransferObserver? = null
        private var index: Int = 0
        private var fileType: Int = 0
        private var width: Int? = null
        private var height: Int? = null
        private var pageCount: Int? = null
        private var size: Long? = null
        private var duration: Int? = null
        private var uuid: String? = null

        fun name(name: String) = apply { this.name = name }
        fun awsFolderPath(awsFolderPath: String) = apply { this.awsFolderPath = awsFolderPath }
        fun transferObserver(transferObserver: TransferObserver?) =
            apply { this.transferObserver = transferObserver }

        fun index(index: Int) = apply { this.index = index }
        fun fileType(fileType: Int) = apply { this.fileType = fileType }
        fun width(width: Int?) = apply { this.width = width }
        fun height(height: Int?) = apply { this.height = height }
        fun pageCount(pageCount: Int?) = apply { this.pageCount = pageCount }
        fun size(size: Long?) = apply { this.size = size }
        fun duration(duration: Int?) = apply { this.duration = duration }
        fun uuid(uuid: String?) = apply { this.uuid = uuid }

        fun build() = AWSFileResponse(
            name,
            awsFolderPath,
            transferObserver,
            index,
            fileType,
            width,
            height,
            pageCount,
            size,
            duration,
            uuid
        )
    }

    fun toBuilder(): Builder {
        return Builder().name(name)
            .awsFolderPath(awsFolderPath)
            .transferObserver(transferObserver)
            .index(index)
            .fileType(fileType)
            .width(width)
            .height(height)
            .pageCount(pageCount)
            .size(size)
            .duration(duration)
            .uuid(uuid)
    }
}