package com.likeminds.feedsx.utils

import android.content.Context
import android.net.Uri
import android.util.Patterns
import android.webkit.MimeTypeMap
import android.webkit.URLUtil
import com.likeminds.feedsx.media.model.*
import com.likeminds.feedsx.utils.ViewUtils.isValidUrl
import java.util.regex.Matcher
import java.util.regex.Pattern


object ValueUtils {

    private const val youtubeVideoIdRegex =
        "^((?:https?:)?//)?((?:www|m)\\.)?(youtube(-nocookie)?\\.com|youtu.be)(/(?:[\\w\\-]+\\?v=|embed/|live/|v/|shorts/)?)([\\w\\-]+)(\\S+)?\$"

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

    private fun Uri.getMimeType(context: Context): String? {
        var type = context.contentResolver.getType(this)
        if (type == null) {
            val fileExtension = MimeTypeMap.getFileExtensionFromUrl(this.toString())
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension.lowercase())
        }
        return type
    }

    fun String.getUrlIfExist(): String? {
        return try {
            val links: MutableList<String> = ArrayList()
            val matcher = Patterns.WEB_URL.matcher(this)
            while (matcher.find()) {
                val link = matcher.group()
                if (URLUtil.isValidUrl(link)) {
                    links.add(link)
                } else {
                    val newHttpsLink = "https://$link"
                    if (URLUtil.isValidUrl(newHttpsLink)) {
                        links.add(newHttpsLink)
                    }
                }
            }
            if (links.isNotEmpty()) {
                links.first()
            } else null
        } catch (e: Exception) {
            return null
        }
    }

    fun String?.isImageValid(): Boolean {
        return this?.isValidUrl() ?: false
    }

    /**
     * http://www.youtube.com/watch?v=-wtIMTCHWuI
     * http://www.youtube.com/v/-wtIMTCHWuI
     * http://youtu.be/-wtIMTCHWuI
     */
    fun String.isValidYoutubeLink(): Boolean {
        val uri = Uri.parse(this)
        return uri.host.equals("youtube") ||
                uri.host.equals("youtube.com") ||
                uri.host.equals("youtu.be") ||
                uri.host.equals("www.youtube.com")
    }

    // returns video id from the youtube video link
    fun String.getYoutubeVideoId(): String? {
        var videoId: String? = null
        val pattern: Pattern = Pattern.compile(
            youtubeVideoIdRegex,
            Pattern.CASE_INSENSITIVE
        )
        val matcher: Matcher = pattern.matcher(this)
        if (matcher.matches()) {
            videoId = matcher.group(6)
        }
        return videoId
    }
}