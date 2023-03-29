package com.likeminds.feedsx.media.model

import androidx.annotation.StringDef
import java.io.File

const val IMAGE = "image"
const val VIDEO = "video"
const val PDF = "pdf"

@StringDef(
    IMAGE, VIDEO, PDF
)

@Retention(AnnotationRetention.SOURCE)
annotation class MediaType {
    companion object {

        fun getFileType(type: List<String>): String {
            return when {
                isImage(type) -> {
                    IMAGE
                }
                isVideo(type) -> {
                    VIDEO
                }
                isBothImageAndVideo(type) -> {
                    "$IMAGE/$VIDEO"
                }
                isPDF(type) -> {
                    PDF
                }
                else -> {
                    ""
                }
            }
        }

        fun contains(type: String): Boolean {
            return type == IMAGE ||
                    type == VIDEO ||
                    type == PDF
        }

        fun isImage(mediaType: String?): Boolean {
            return mediaType == IMAGE
        }

        fun isImage(mediaTypes: List<String>): Boolean {
            return mediaTypes.contains(IMAGE)
        }

        fun isVideo(mediaType: String?): Boolean {
            return mediaType == VIDEO
        }

        fun isVideo(mediaTypes: List<String>): Boolean {
            return mediaTypes.contains(VIDEO)
        }

        fun isPDF(mediaType: String?): Boolean {
            return mediaType == PDF
        }

        fun isPDF(mediaTypes: List<String>): Boolean {
            return mediaTypes.contains(PDF)
        }

        fun isBothImageAndVideo(mediaTypes: List<String>): Boolean {
            return mediaTypes.contains(IMAGE) && mediaTypes.contains(VIDEO)
        }

        fun isImageOrVideo(mediaTypes: List<String>): Boolean {
            return mediaTypes.contains(IMAGE) || mediaTypes.contains(VIDEO)
        }

        fun getMediaFileInitial(mediaType: String?, isThumbnail: Boolean = false): String {
            var initial = when (mediaType) {
                IMAGE -> "IMG_"
                VIDEO -> "VID_"
                PDF -> "DOC_"
                else -> "MEDIA_"
            }
            if (isThumbnail) {
                initial += "THUMB_"
            }
            return initial
        }
    }
}