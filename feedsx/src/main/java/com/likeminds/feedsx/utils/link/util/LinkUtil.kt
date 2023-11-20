package com.likeminds.feedsx.utils.link.util

import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.likeminds.feedsx.databinding.LmFeedLayoutCreatePostLinkBinding
import com.likeminds.feedsx.utils.ViewUtils

object LinkUtil {
    // if image url is invalid/empty then handle link preview constraints
    fun handleLinkPreviewConstraints(
        binding: LmFeedLayoutCreatePostLinkBinding,
        isImageValid: Boolean
    ) {
        binding.apply {
            val constraintLayout: ConstraintLayout = clLink
            val constraintSet = ConstraintSet()
            constraintSet.clone(constraintLayout)
            if (isImageValid) {
                // if image is valid then we show link image and set title constraints
                setValidLinkImageConstraints(
                    binding,
                    constraintSet
                )
            } else {
                // if image is not valid then we don't show image and set title constraints
                setInvalidLinkImageConstraints(
                    binding,
                    constraintSet
                )
            }
            constraintSet.applyTo(constraintLayout)
        }
    }

    // sets constraints of link preview when image is invalid
    private fun setInvalidLinkImageConstraints(
        binding: LmFeedLayoutCreatePostLinkBinding,
        constraintSet: ConstraintSet
    ) {
        binding.apply {
            val margin16 = ViewUtils.dpToPx(16)
            val margin4 = ViewUtils.dpToPx(4)
            constraintSet.connect(
                tvLinkUrl.id,
                ConstraintSet.TOP,
                ConstraintSet.PARENT_ID,
                ConstraintSet.TOP,
                margin16
            )
            constraintSet.connect(
                tvLinkUrl.id,
                ConstraintSet.START,
                ConstraintSet.PARENT_ID,
                ConstraintSet.START,
                margin16
            )
            constraintSet.connect(
                tvLinkUrl.id,
                ConstraintSet.END,
                ivDeleteLink.id,
                ConstraintSet.START,
                margin4
            )
        }
    }

    // sets constraints of link preview when image is valid
    private fun setValidLinkImageConstraints(
        binding: LmFeedLayoutCreatePostLinkBinding,
        constraintSet: ConstraintSet
    ) {
        binding.apply {
            val margin = ViewUtils.dpToPx(16)
            constraintSet.connect(
                tvLinkUrl.id,
                ConstraintSet.END,
                ConstraintSet.PARENT_ID,
                ConstraintSet.END,
                margin
            )
            constraintSet.connect(
                tvLinkUrl.id,
                ConstraintSet.START,
                ConstraintSet.PARENT_ID,
                ConstraintSet.START,
                margin
            )
            constraintSet.connect(
                tvLinkUrl.id,
                ConstraintSet.TOP,
                ivLink.id,
                ConstraintSet.BOTTOM,
                margin
            )
        }
    }
}