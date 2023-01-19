package com.likeminds.feedsx.post.model

import android.os.Parcelable
import com.likeminds.feedsx.utils.model.BaseViewType
import com.likeminds.feedsx.utils.model.ITEM_USER
import kotlinx.parcelize.Parcelize

@Parcelize
class UserViewData private constructor(
    var id: Int,
    var name: String,
    var imageUrl: String,
    var userUniqueId: String,
    var isGuest: Boolean,
    var isDeleted: Boolean,
) : Parcelable, BaseViewType {
    override val viewType: Int
        get() = ITEM_USER

    class Builder {
        private var id: Int = 0
        private var name: String = ""
        private var imageUrl: String = ""
        private var userUniqueId: String = ""
        private var isGuest: Boolean = false
        private var isDeleted: Boolean = false

        fun id(id: Int) = apply { this.id = id }
        fun name(name: String) = apply { this.name = name }
        fun imageUrl(imageUrl: String) = apply { this.imageUrl = imageUrl }
        fun userUniqueId(userUniqueId: String) = apply { this.userUniqueId = userUniqueId }
        fun isGuest(isGuest: Boolean) = apply { this.isGuest = isGuest }
        fun isDeleted(isDeleted: Boolean) = apply { this.isDeleted = isDeleted }

        fun build() = UserViewData(
            id,
            name,
            imageUrl,
            userUniqueId,
            isGuest,
            isDeleted
        )
    }

    fun toBuilder(): Builder {
        return Builder().id(id)
            .name(name)
            .imageUrl(imageUrl)
            .userUniqueId(userUniqueId)
            .isGuest(isGuest)
            .isDeleted(isDeleted)
    }
}