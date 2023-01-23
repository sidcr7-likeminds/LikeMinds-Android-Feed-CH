package com.likeminds.feedsx.media.util

import com.likeminds.feedsx.utils.MemoryUnitFormat

object MediaUtils {

    private const val TAG = "MediaUtils"

    /**
     * Return the file size in string format e.g. 10 MB
     * */
    fun getFileSizeText(sizeBytes: Long): String {
        return MemoryUnitFormat.formatBytes(sizeBytes)
    }


    fun isVideoType(contentType: String?): Boolean {
        return null != contentType && contentType.startsWith("video/")
    }

    fun isAudioType(contentType: String?): Boolean {
        return null != contentType && contentType.startsWith("audio/")
    }

    fun isImageType(contentType: String?): Boolean {
        return null != contentType && contentType.startsWith("image/")
    }

    fun isPdfType(contentType: String?): Boolean {
        return null != contentType && contentType == "application/pdf"
    }
}