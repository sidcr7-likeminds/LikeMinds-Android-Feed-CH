package com.likeminds.feedsx.utils.membertagging.model

import android.graphics.drawable.Drawable

class MemberTagViewData private constructor(
    var name: String,
    var imageUrl: String,
    var id: Int,
    var isGuest: Boolean,
    var userUniqueId: String,
    var placeholder: Drawable?,
    var route: String,
    var tag: String,
    var description: String,
    var isLastItem: Boolean
) {
    class Builder {
        private var name: String = ""
        private var imageUrl: String = ""
        private var id: Int = 0
        private var isGuest: Boolean = false
        private var userUniqueId: String = ""
        private var placeholder: Drawable? = null
        private var route: String = ""
        private var tag: String = ""
        private var description: String = ""
        private var isLastItem: Boolean = false

        fun name(name: String) = apply { this.name = name }
        fun imageUrl(imageUrl: String) = apply { this.imageUrl = imageUrl }
        fun id(id: Int) = apply { this.id = id }
        fun isGuest(isGuest: Boolean) = apply { this.isGuest = isGuest }
        fun userUniqueId(userUniqueId: String) = apply { this.userUniqueId = userUniqueId }
        fun placeHolder(placeholder: Drawable?) = apply { this.placeholder = placeholder }
        fun route(route: String) = apply { this.route = route }
        fun tag(tag: String) = apply { this.tag = tag }
        fun description(description: String) = apply { this.description = description }
        fun isLastItem(isLastItem: Boolean) = apply { this.isLastItem = isLastItem }

        fun build() = MemberTagViewData(
            name,
            imageUrl,
            id,
            isGuest,
            userUniqueId,
            placeholder,
            route,
            tag,
            description,
            isLastItem
        )
    }

    fun toBuilder(): Builder {
        return Builder().name(name)
            .imageUrl(imageUrl)
            .id(id)
            .isGuest(isGuest)
            .userUniqueId(userUniqueId)
            .placeHolder(placeholder)
            .route(route)
            .tag(tag)
            .description(description)
            .isLastItem(isLastItem)
    }
}