package com.likeminds.feedsx.posttypes.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class QuestionViewData private constructor(
    val id: String?,
    val questionTitle: String,
    val state: Int,
    val value: String?,
    val optional: Boolean,
    val rank: Int?,
    val canAddOtherOptions: Boolean?,
    val helpText: String?,
    val imageUrl: String?,
    val answerImageUrl: String?,
    val isAnswerEditable: Boolean?,
    val isCompulsory: Boolean?,
    val isHidden: Boolean?,
    val tag: String?,
    val answerOfQuestion: String?,
    val questionChangeState: Int?,
    val communityId: String?,
    val memberId: String?,
) : Parcelable {

    class Builder {
        private var id: String? = null
        private var questionTitle: String = ""
        private var state: Int = 0
        private var value: String? = null
        private var optional: Boolean = false
        private var rank: Int? = null
        private var canAddOtherOptions: Boolean? = null
        private var helpText: String? = null
        private var imageUrl: String? = null
        private var answerImageUrl: String? = null
        private var isAnswerEditable: Boolean? = null
        private var isCompulsory: Boolean? = null
        private var isHidden: Boolean? = null
        private var tag: String? = null
        private var answerOfQuestion: String? = null
        private var questionChangeState: Int? = null
        private var communityId: String? = null
        private var memberId: String? = null

        fun id(id: String?) = apply { this.id = id }
        fun questionTitle(questionTitle: String) = apply { this.questionTitle = questionTitle }
        fun state(state: Int) = apply { this.state = state }
        fun value(value: String?) = apply { this.value = value }
        fun optional(optional: Boolean) = apply { this.optional = optional }
        fun helpText(helpText: String?) = apply { this.helpText = helpText }
        fun isCompulsory(isCompulsory: Boolean?) = apply { this.isCompulsory = isCompulsory }
        fun isHidden(isHidden: Boolean?) = apply { this.isHidden = isHidden }
        fun imageUrl(imageUrl: String?) = apply { this.imageUrl = imageUrl }
        fun answerImageUrl(answerImageUrl: String?) = apply { this.answerImageUrl = answerImageUrl }
        fun canAddOtherOptions(canAddOtherOptions: Boolean?) =
            apply { this.canAddOtherOptions = canAddOtherOptions }

        fun answerOfQuestion(answerOfQuestion: String?) =
            apply { this.answerOfQuestion = answerOfQuestion }

        fun tag(tag: String?) = apply { this.tag = tag }
        fun rank(rank: Int?) = apply { this.rank = rank }
        fun questionChangeState(questionChangeState: Int?) =
            apply { this.questionChangeState = questionChangeState }

        fun isAnswerEditable(isAnswerEditable: Boolean?) =
            apply { this.isAnswerEditable = isAnswerEditable }

        fun communityId(communityId: String?) = apply { this.communityId = communityId }
        fun memberId(memberId: String?) = apply { this.memberId = memberId }

        fun build() = QuestionViewData(
            id,
            questionTitle,
            state,
            value,
            optional,
            rank,
            canAddOtherOptions,
            helpText,
            imageUrl,
            answerImageUrl,
            isAnswerEditable,
            isCompulsory,
            isHidden,
            tag,
            answerOfQuestion,
            questionChangeState,
            communityId,
            memberId
        )
    }

    fun toBuilder(): Builder {
        return Builder().id(id)
            .questionTitle(questionTitle)
            .state(state)
            .value(value)
            .optional(optional)
            .rank(rank)
            .isCompulsory(isCompulsory)
            .canAddOtherOptions(canAddOtherOptions)
            .helpText(helpText)
            .imageUrl(imageUrl)
            .answerImageUrl(answerImageUrl)
            .isAnswerEditable(isAnswerEditable)
            .isHidden(isHidden)
            .tag(tag)
            .answerOfQuestion(answerOfQuestion)
            .questionChangeState(questionChangeState)
            .communityId(communityId)
            .memberId(memberId)
    }
}