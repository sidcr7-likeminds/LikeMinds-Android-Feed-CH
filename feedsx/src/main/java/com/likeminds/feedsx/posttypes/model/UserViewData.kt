package com.likeminds.feedsx.posttypes.model

import android.os.Parcelable
import com.likeminds.feedsx.utils.model.BaseViewType
import com.likeminds.feedsx.utils.model.ITEM_USER
import kotlinx.parcelize.Parcelize

@Parcelize
class UserViewData private constructor(
    val id: Int,
    val name: String,
    val imageUrl: String,
    val userUniqueId: String,
    val customTitle: String?,
    val isGuest: Boolean,
    val isDeleted: Boolean?,
    val updatedAt: Long,
    val sdkClientInfoViewData: SDKClientInfoViewData,
    val uuid: String,
    val listOfQuestionAnswerViewData: List<QuestionViewData>?
) : Parcelable, BaseViewType {
    override val viewType: Int
        get() = ITEM_USER

    class Builder {
        private var id: Int = 0
        private var name: String = ""
        private var imageUrl: String = ""
        private var userUniqueId: String = ""
        private var customTitle: String? = null
        private var isGuest: Boolean = false
        private var isDeleted: Boolean? = null
        private var updatedAt: Long = 0
        private var sdkClientInfoViewData: SDKClientInfoViewData =
            SDKClientInfoViewData.Builder().build()
        private var uuid: String = ""
        private var listOfQuestionAnswerViewData: List<QuestionViewData>? = null

        fun id(id: Int) = apply { this.id = id }
        fun name(name: String) = apply { this.name = name }
        fun imageUrl(imageUrl: String) = apply { this.imageUrl = imageUrl }
        fun userUniqueId(userUniqueId: String) = apply { this.userUniqueId = userUniqueId }
        fun customTitle(customTitle: String?) = apply { this.customTitle = customTitle }
        fun isGuest(isGuest: Boolean) = apply { this.isGuest = isGuest }
        fun isDeleted(isDeleted: Boolean?) = apply { this.isDeleted = isDeleted }
        fun updatedAt(updatedAt: Long) = apply { this.updatedAt = updatedAt }
        fun sdkClientInfoViewData(sdkClientInfoViewData: SDKClientInfoViewData) =
            apply { this.sdkClientInfoViewData = sdkClientInfoViewData }

        fun uuid(uuid: String) = apply { this.uuid = uuid }
        fun listOfQuestionAnswerViewData(listOfQuestionAnswerViewData: List<QuestionViewData>?) =
            apply { this.listOfQuestionAnswerViewData = listOfQuestionAnswerViewData }

        fun build() = UserViewData(
            id,
            name,
            imageUrl,
            userUniqueId,
            customTitle,
            isGuest,
            isDeleted,
            updatedAt,
            sdkClientInfoViewData,
            uuid,
            listOfQuestionAnswerViewData
        )
    }

    fun toBuilder(): Builder {
        return Builder().id(id)
            .name(name)
            .imageUrl(imageUrl)
            .userUniqueId(userUniqueId)
            .customTitle(customTitle)
            .isGuest(isGuest)
            .isDeleted(isDeleted)
            .updatedAt(updatedAt)
            .uuid(uuid)
            .sdkClientInfoViewData(sdkClientInfoViewData)
            .listOfQuestionAnswerViewData(listOfQuestionAnswerViewData)
    }
}