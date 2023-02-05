package com.likeminds.feedsx.utils

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import com.likeminds.feedsx.media.model.IMAGE
import com.likeminds.feedsx.media.model.PDF
import com.likeminds.feedsx.media.model.VIDEO

object ValueUtils {

    @JvmStatic
    fun <K, V> getOrDefault(map: Map<K, V>, key: K, defaultValue: V): V? {
        return if (map.containsKey(key)) map[key] else defaultValue
    }

    fun String.getValidTextForLinkify(): String {
        return this.replace("\u202C", "")
            .replace("\u202D", "")
            .replace("\u202E", "")
    }

    fun <T> List<T>.getItemInList(position: Int): T? {
        if (position < 0 || position >= this.size) {
            return null
        }
        return this[position]
    }

    /**
     * This function run filter and map operation in single loop
     */
    inline fun <T, R, P> Iterable<T>.filterThenMap(
        predicate: (T) -> Pair<Boolean, P>,
        transform: (Pair<T, P>) -> R
    ): List<R> {
        return filterThenMap(ArrayList(), predicate, transform)
    }

    inline fun <T, R, P, C : MutableCollection<in R>>
            Iterable<T>.filterThenMap(
        collection: C, predicate: (T) -> Pair<Boolean, P>,
        transform: (Pair<T, P>) -> R
    ): C {
        for (element in this) {
            val response = predicate(element)
            if (response.first) {
                collection.add(transform(Pair(element, response.second)))
            }
        }
        return collection
    }

    /**
     * Check mediaType from Media mimeType
     * */
    fun String?.getMediaType(): String? {
        var mediaType: String? = null
        if (this != null) {
            when {
                this.startsWith("image") -> mediaType = IMAGE
                this.startsWith("video") -> mediaType = VIDEO
                this == "application/pdf" -> mediaType = PDF
            }
        }
        return mediaType
    }

    fun Uri?.getMediaType(context: Context): String? {
        var mediaType: String? = null
        this?.let {
            mediaType = this.getMimeType(context).getMediaType()
        }
        return mediaType
    }

    fun Uri.getMimeType(context: Context): String? {
        var type = context.contentResolver.getType(this)
        if (type == null) {
            val fileExtension = MimeTypeMap.getFileExtensionFromUrl(this.toString())
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension.lowercase())
        }
        return type
    }

    fun Int.isValidIndex(items: List<*>? = null): Boolean {
        return if (items != null) {
            this > -1 && this < items.size
        } else {
            this > -1
        }
    }

    fun String.isValidYoutubeLink(): Boolean {
        val uri = Uri.parse(this)
        return uri.host.equals("youtube") ||
                uri.host.equals("youtu.be") ||
                uri.host.equals("www.youtube.com")
    }

}