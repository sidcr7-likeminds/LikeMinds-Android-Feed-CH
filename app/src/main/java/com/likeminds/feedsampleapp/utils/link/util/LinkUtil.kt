package com.likeminds.feedsampleapp.utils.link.util

import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.likeminds.feedsampleapp.databinding.LayoutCreatePostLinkBinding
import com.likeminds.feedsampleapp.utils.ViewUtils

object LinkUtil {
    // if image url is invalid/empty then handle link preview constraints
    fun handleLinkPreviewConstraints(
        binding: LayoutCreatePostLinkBinding,
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
        binding: LayoutCreatePostLinkBinding,
        constraintSet: ConstraintSet
    ) {
        binding.apply {
            val margin16 = ViewUtils.dpToPx(16)
            val margin4 = ViewUtils.dpToPx(4)
            constraintSet.connect(
                tvLinkTitle.id,
                ConstraintSet.TOP,
                ConstraintSet.PARENT_ID,
                ConstraintSet.TOP,
                margin16
            )
            constraintSet.connect(
                tvLinkTitle.id,
                ConstraintSet.START,
                ConstraintSet.PARENT_ID,
                ConstraintSet.START,
                margin16
            )
            constraintSet.connect(
                tvLinkTitle.id,
                ConstraintSet.END,
                ivCross.id,
                ConstraintSet.START,
                margin4
            )
        }
    }

    // sets constraints of link preview when image is valid
    private fun setValidLinkImageConstraints(
        binding: LayoutCreatePostLinkBinding,
        constraintSet: ConstraintSet
    ) {
        binding.apply {
            val margin = ViewUtils.dpToPx(16)
            constraintSet.connect(
                tvLinkTitle.id,
                ConstraintSet.END,
                ConstraintSet.PARENT_ID,
                ConstraintSet.END,
                margin
            )
            constraintSet.connect(
                tvLinkTitle.id,
                ConstraintSet.START,
                ConstraintSet.PARENT_ID,
                ConstraintSet.START,
                margin
            )
            constraintSet.connect(
                tvLinkTitle.id,
                ConstraintSet.TOP,
                ivLink.id,
                ConstraintSet.BOTTOM,
                margin
            )
        }
    }
}